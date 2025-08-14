package ch.lupogryph.quarkus.actuator.configs;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.vertx.http.runtime.devmode.Json;
import io.smallrye.config.ConfigMapping;

@ApplicationScoped
public class ConfigsService {

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "application")
    String applicationName;

    @Inject
    BeanManager beanManager;

    public Json.JsonObjectBuilder contexts() {
        var contexts = Json.object();
        var application = Json.object();
        application.put("beans", beans());
        contexts.put(applicationName, application);
        return Json.object().put("contexts", contexts);
    }

    public Json.JsonObjectBuilder beans() {
        var object = Json.object();
        Set<Class<?>> configClasses = beanManager.getBeans(Object.class).stream()
                .map(Bean::getBeanClass)
                .filter(clazz -> clazz.isAnnotationPresent(ConfigMapping.class))
                .collect(Collectors.toSet());
        for (Class<?> configClass : configClasses) {
            object.put(configClass.getName(), bean(configClass));
        }
        return object;
    }

    public Json.JsonObjectBuilder bean(Class<?> configClass) {
        var object = Json.object();
        var prefix = prefix(configClass);
        object.put("prefix", prefix);
        object.put("properties", properties(prefix));
        return object;
    }

    public Json.JsonObjectBuilder properties(String prefix) {
        var object = Json.object();
        Config config = ConfigProvider.getConfig();
        StreamSupport.stream(config.getPropertyNames().spliterator(), false)
                .filter(name -> name.startsWith(prefix))
                .forEach(name -> object.put(stripName(prefix, name), raw(name)));
        return object;
    }

    public static String raw(String name) {
        return ConfigProvider.getConfig().getConfigValue(name).getRawValue();
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

}
