package ch.lupogryph.quarkus.actuator.env;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import jakarta.enterprise.context.ApplicationScoped;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.config.spi.ConfigSource;

@ApplicationScoped
public class EnvService {

    @ConfigProperty(name = "quarkus.actuator.env.hide", defaultValue = ".*(?i)password.*,.*(?i)secret.*,.*(?i)key.*,.*(?i)token.*,.*(?i)credentials.*")
    Set<String> hidePatterns;

    public JsonObjectBuilder env() {
        var object = Json.createObjectBuilder();
        object.add("activeProfiles", activeProfiles());
        object.add("defaultProfiles", defaultProfiles());
        object.add("propertySources", propertySources());
        return object;
    }

    public JsonArrayBuilder activeProfiles() {
        var array = Json.createArrayBuilder();
        ConfigProvider.getConfig().getOptionalValues("quarkus.profile", String.class)
                .orElse(List.of())
                .forEach(array::add);
        return array;
    }

    public JsonArrayBuilder defaultProfiles() {
        var array = Json.createArrayBuilder();
        array.add("prod"); // in quarkus there is no default profiles, but we can assume prod as default
        return array;
    }

    public JsonArrayBuilder propertySources() {
        var array = Json.createArrayBuilder();
        ConfigProvider.getConfig().getConfigSources().forEach(configSource -> array.add(propertySource(configSource)));
        return array;
    }

    public JsonObjectBuilder propertySource(ConfigSource source) {
        var object = Json.createObjectBuilder();
        object.add("name", source.getName());
        object.add("properties", properties(source));
        return object;
    }

    public JsonObjectBuilder properties(ConfigSource source) {
        var object = Json.createObjectBuilder();
        source.getProperties()
                .forEach((key, value) -> object.add(key, propertyValue(hide(key) ? "******" : value)));
        return object;
    }

    public JsonObjectBuilder propertyValue(String value) {
        var object = Json.createObjectBuilder();
        object.add("value", value);
        return object;
    }

    public boolean hide(String name) {
        return hidePatterns.stream().anyMatch(p -> Pattern.matches(p, name));
    }

}
