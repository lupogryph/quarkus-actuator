package ch.lupogryph.quarkus.actuator.configs;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.smallrye.config.ConfigMapping;

@ApplicationScoped
public class ConfigsService {

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "application")
    String applicationName;

    @ConfigProperty(name = "quarkus.actuator.configs.hide", defaultValue = ".*(?i)password.*,.*(?i)secret.*,.*(?i)key.*,.*(?i)token.*,.*(?i)credentials.*")
    Set<String> hidePatterns;

    @Inject
    BeanManager beanManager;

    public JsonObjectBuilder contexts() {
        var contexts = Json.createObjectBuilder();
        var application = Json.createObjectBuilder();
        application.add("beans", beans());
        contexts.add(applicationName, application);
        return Json.createObjectBuilder().add("contexts", contexts);
    }

    public JsonObjectBuilder beans() {
        var object = Json.createObjectBuilder();
        Set<Class<?>> configClasses = beanManager.getBeans(Object.class).stream()
                .map(Bean::getBeanClass)
                .filter(clazz -> clazz.isAnnotationPresent(ConfigMapping.class))
                .collect(Collectors.toSet());
        for (Class<?> configClass : configClasses) {
            object.add(configClass.getName(), bean(configClass));
        }
        return object;
    }

    public JsonObjectBuilder bean(Class<?> configClass) {
        var object = Json.createObjectBuilder();
        var prefix = prefix(configClass);
        object.add("prefix", prefix);
        object.add("properties", properties(prefix));
        return object;
    }

    public JsonObjectBuilder properties(String prefix) {
        var object = Json.createObjectBuilder();
        Config config = ConfigProvider.getConfig();
        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(name -> name.startsWith(prefix))
                .forEach(name -> object.add(stripName(prefix, name), raw(name)));
        return object;
    }

    public String raw(String name) {
        return hide(name) ? "******" : ConfigProvider.getConfig().getConfigValue(name).getRawValue();
    }

    public static String sourceName(String name) {
        return ConfigProvider.getConfig().getConfigValue(name).getSourceName();
    }

    public static String prefix(Class<?> configClass) {
        return configClass.getAnnotation(ConfigMapping.class).prefix();
    }

    public static String stripName(String prefix, String name) {
        return name.substring(prefix.length() + 1); // +1 for the dot
    }

    public boolean hide(String name) {
        return hidePatterns.stream().anyMatch(p -> Pattern.matches(p, name));
    }

}

