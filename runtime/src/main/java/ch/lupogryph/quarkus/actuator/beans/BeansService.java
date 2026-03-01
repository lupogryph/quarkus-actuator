package ch.lupogryph.quarkus.actuator.beans;

import java.util.Set;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class BeansService {

    @Inject
    BeanManager beanManager;

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "quarkus-actuator")
    String applicationName;

    public JsonObjectBuilder contexts() {
        var contexts = Json.createObjectBuilder();
        var application = Json.createObjectBuilder();
        application.add("beans", beans());
        contexts.add(applicationName, application);
        return Json.createObjectBuilder().add("contexts", contexts);
    }

    public JsonObjectBuilder beans() {
        var object = Json.createObjectBuilder();
        Set<Bean<?>> allBeans = beanManager.getBeans(Object.class, new jakarta.enterprise.util.AnnotationLiteral[] {});
        for (Bean<?> bean : allBeans) {
            object.add(bean.getBeanClass().getName(), bean(bean));
        }
        return object;
    }

    public JsonObjectBuilder bean(Bean<?> bean) {
        var object = Json.createObjectBuilder();
        object.add("aliases", aliases(bean));
        object.add("scope", bean.getScope().getSimpleName());
        bean.getTypes().stream().findFirst().ifPresent(type -> object.add("type", type.getTypeName()));
        object.add("dependencies", dependencies(bean));
        return object;
    }

    public JsonArrayBuilder aliases(Bean<?> bean) {
        var array = Json.createArrayBuilder();
        for (var qualifier : bean.getQualifiers()) {
            array.add(qualifier.toString());
        }
        return array;
    }

    public JsonArrayBuilder dependencies(Bean<?> bean) {
        var array = Json.createArrayBuilder();
        for (var field : bean.getBeanClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                array.add(field.getName());
            }
        }
        return array;
    }

}
