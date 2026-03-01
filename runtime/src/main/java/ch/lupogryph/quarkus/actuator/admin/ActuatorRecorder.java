package ch.lupogryph.quarkus.actuator.admin;

import static ch.lupogryph.quarkus.actuator.util.Headers.ACTUATOR_CONTENT_TYPE;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.net.UnknownHostException;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.vertx.http.runtime.management.ManagementConfig;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class ActuatorRecorder {

    private static final Logger log = LoggerFactory.getLogger(ActuatorRecorder.class);

    String baseUrl;

    public Handler<RoutingContext> actuator(ActuatorConfig actuatorConfig, ManagementConfig managementConfig, boolean caches,
            boolean quartz) {

        String host;
        try {
            host = actuatorConfig.getLocalAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        var port = managementConfig.determinePort(LaunchMode.current());
        this.baseUrl = "http://%s:%d/q".formatted(host, port);

        var json = Json.createObjectBuilder();

        var links = Json.createObjectBuilder();
        links.add("self", link("self", "/actuator"));
        links.add("beans", link("beans", "/actuator/beans"));
        if (caches) {
            links.add("caches-cache", link("caches-cache", "/actuator/caches/{cache}"));
            links.add("caches", link("caches", "/actuator/caches"));
        }
        links.add("health", link("health", "/health"));
        links.add("health-path", link("health-path", "/health/{*path}"));
        links.add("info", link("info", "/info"));
        links.add("conditions", link("conditions", "/actuator/conditions"));
        links.add("configprops", link("configprops", "/actuator/configprops"));
        links.add("configprops-prefix", link("configprops-prefix", "/actuator/configprops/{prefix}"));
        links.add("env", link("env", "/actuator/env"));
        links.add("env-toMatch", link("env-toMatch", "/actuator/env/{toMatch}"));
        links.add("loggers", link("loggers", "/actuator/loggers"));
        links.add("loggers-name", link("loggers-name", "/actuator/loggers/{name}"));
        links.add("threaddump", link("threaddump", "/actuator/threaddump"));
        links.add("heapdump", link("heapdump", "/actuator/heapdump"));
        links.add("metrics-requiredMetricName", link("metrics-requiredMetricName", "/actuator/metrics/{requiredMetricName}"));
        links.add("metrics", link("metrics", "/actuator/metrics"));
        if (quartz) {
            links.add("quartz", link("quartz", "/actuator/quartz"));
            links.add("quartz-jobs-group-name", link("quartz-jobs-group-name", "/actuator/quartz/{jobs}/{group}/{name}"));
            links.add("quartz-jobsOrTriggers-group",
                    link("quartz-jobsOrTriggers-group", "/actuator/quartz/{jobsOrTriggers}/{group}"));
            links.add("quartz-jobsOrTriggers", link("quartz-jobsOrTriggers", "/actuator/quartz/{jobsOrTriggers}"));
            links.add("quartz-jobsOrTriggers-group-name",
                    link("quartz-jobsOrTriggers-group-name", "/actuator/quartz/{jobsOrTriggers}/{group}/{name}"));
        }
        //links.add("sbom-id", link("sbom-id", "/actuator/sbom/{id}"));
        //links.add("sbom", link("sbom", "/actuator/sbom"));
        //links.add("scheduledtasks", link("scheduledtasks", "/actuator/scheduledtasks"));
        //links.add("mappings", link("mappings", "/actuator/mappings"));

        json.add("_links", links);

        var response = json.build();

        return rc -> {
            log.debug("-> {} /q/actuator", rc.request().method());
            log.debug("<- {} /q/actuator : 200", rc.request().method());
            log.trace(response.toString());
            rc.response()
                    .putHeader(CONTENT_TYPE, ACTUATOR_CONTENT_TYPE)
                    .end(response.toString());
        };
    }

    JsonObjectBuilder link(String name, String path) {
        return Json.createObjectBuilder()
                .add("href", baseUrl + path)
                .add("templated", path.contains("{"));
    }

}
