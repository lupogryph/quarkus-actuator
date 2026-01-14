package ch.lupogryph.quarkus.actuator.env;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.spi.ConfigSource;

import io.quarkus.vertx.http.runtime.devmode.Json;

@ApplicationScoped
public class EnvService {

    @ConfigProperty(name = "quarkus.actuator.env.hide", defaultValue = ".*(?i)password.*,.*(?i)secret.*,.*(?i)key.*,.*(?i)token.*,.*(?i)credentials.*")
    Set<String> hidePatterns;

    public Json.JsonObjectBuilder env() {
        var object = Json.object();
        object.put("activeProfiles", activeProfiles());
        object.put("defaultProfiles", defaultProfiles());
        object.put("propertySources", propertySources());
        return object;
    }

    public Json.JsonArrayBuilder activeProfiles() {
        var array = Json.array();
        ConfigProvider.getConfig().getOptionalValues("quarkus.profile", String.class)
                .orElse(List.of())
                .forEach(array::add);
        return array;
    }

    public Json.JsonArrayBuilder defaultProfiles() {
        var array = Json.array();
        array.add("prod"); // in quarkus there is no default profiles, but we can assume prod as default
        return array;
    }

    public Json.JsonArrayBuilder propertySources() {
        var array = Json.array();
        ConfigProvider.getConfig().getConfigSources().forEach(configSource -> array.add(propertySource(configSource)));
        return array;
    }

    public Json.JsonObjectBuilder propertySource(ConfigSource source) {
        var object = Json.object();
        object.put("name", source.getName());
        object.put("properties", properties(source));
        return object;
    }

    public Json.JsonObjectBuilder properties(ConfigSource source) {
        var object = Json.object();
        source.getProperties()
                .forEach((key, value) -> object.put(key, propertyValue(hide(key) ? "******" : value)));
        return object;
    }

    public Json.JsonObjectBuilder propertyValue(String value) {
        var object = Json.object();
        object.put("value", value);
        return object;
    }

    public boolean hide(String name) {
        return hidePatterns.stream().anyMatch(p -> Pattern.matches(p, name));
    }

}
