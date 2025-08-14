package ch.lupogryph.quarkus.actuator.beans;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.vertx.http.runtime.devmode.Json;

@ApplicationScoped
public class BeansService {

    @Inject
    BeanManager beanManager;

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "quarkus-actuator")
    String applicationName;

    public Json.JsonObjectBuilder contexts() {
        var contexts = Json.object();
        var application = Json.object();
        application.put("beans", beans());
        contexts.put(applicationName, application);
        return Json.object().put("contexts", contexts);
    }

    public Json.JsonObjectBuilder beans() {
        var object = Json.object();
        Set<Bean<?>> allBeans = beanManager.getBeans(Object.class, new jakarta.enterprise.util.AnnotationLiteral[] {});
        for (Bean<?> bean : allBeans) {
            object.put(bean.getBeanClass().getName(), bean(bean));
        }
        return object;
    }

    public Json.JsonObjectBuilder bean(Bean<?> bean) {
        var object = Json.object();
        object.put("aliases", aliases(bean));
        object.put("scope", bean.getScope().getSimpleName());
        bean.getTypes().stream().findFirst().ifPresent(type -> object.put("type", type.getTypeName()));
        object.put("dependencies", dependencies(bean));
        return object;
    }

    public Json.JsonArrayBuilder aliases(Bean<?> bean) {
        var array = Json.array();
        for (var qualifier : bean.getQualifiers()) {
            array.add(qualifier.toString());
        }
        return array;
    }

    public Json.JsonArrayBuilder dependencies(Bean<?> bean) {
        var array = Json.array();
        for (var field : bean.getBeanClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                array.add(field.getName());
            }
        }
        return array;
    }

}
