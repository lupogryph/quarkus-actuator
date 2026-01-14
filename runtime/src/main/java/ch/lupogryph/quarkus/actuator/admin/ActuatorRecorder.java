package ch.lupogryph.quarkus.actuator.admin;

import static com.lodh.arte.actuator.util.Headers.ACTUATOR_CONTENT_TYPE;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.annotations.Recorder;
import io.quarkus.vertx.http.runtime.devmode.Json;
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

        var json = Json.object();

        var links = Json.object();
        links.put("self", link("self", "/actuator"));
        links.put("beans", link("beans", "/actuator/beans"));
        if (caches) {
            links.put("caches-cache", link("caches-cache", "/actuator/caches/{cache}"));
            links.put("caches", link("caches", "/actuator/caches"));
        }
        links.put("health", link("health", "/health"));
        links.put("health-path", link("health-path", "/health/{*path}"));
        links.put("info", link("info", "/info"));
        links.put("conditions", link("conditions", "/actuator/conditions"));
        links.put("configprops", link("configprops", "/actuator/configprops"));
        links.put("configprops-prefix", link("configprops-prefix", "/actuator/configprops/{prefix}"));
        links.put("env", link("env", "/actuator/env"));
        links.put("env-toMatch", link("env-toMatch", "/actuator/env/{toMatch}"));
        links.put("loggers", link("loggers", "/actuator/loggers"));
        links.put("loggers-name", link("loggers-name", "/actuator/loggers/{name}"));
        links.put("threaddump", link("threaddump", "/actuator/threaddump"));
        links.put("heapdump", link("heapdump", "/actuator/heapdump"));
        links.put("metrics-requiredMetricName", link("metrics-requiredMetricName", "/actuator/metrics/{requiredMetricName}"));
        links.put("metrics", link("metrics", "/actuator/metrics"));
        if (quartz) {
            links.put("quartz", link("quartz", "/actuator/quartz"));
            links.put("quartz-jobs-group-name", link("quartz-jobs-group-name", "/actuator/quartz/{jobs}/{group}/{name}"));
            links.put("quartz-jobsOrTriggers-group",
                    link("quartz-jobsOrTriggers-group", "/actuator/quartz/{jobsOrTriggers}/{group}"));
            links.put("quartz-jobsOrTriggers", link("quartz-jobsOrTriggers", "/actuator/quartz/{jobsOrTriggers}"));
            links.put("quartz-jobsOrTriggers-group-name",
                    link("quartz-jobsOrTriggers-group-name", "/actuator/quartz/{jobsOrTriggers}/{group}/{name}"));
        }
        //links.put("sbom-id", link("sbom-id", "/actuator/sbom/{id}"));
        //links.put("sbom", link("sbom", "/actuator/sbom"));
        //links.put("scheduledtasks", link("scheduledtasks", "/actuator/scheduledtasks"));
        //links.put("mappings", link("mappings", "/actuator/mappings"));

        json.put("_links", links);

        var response = json.build();

        return rc -> {
            log.debug("-> {} /q/actuator", rc.request().method());
            log.debug("<- {} /q/actuator : 200", rc.request().method());
            log.trace(response);
            rc.response()
                    .putHeader(CONTENT_TYPE, ACTUATOR_CONTENT_TYPE)
                    .end(response);
        };
    }

    Json.JsonObjectBuilder link(String name, String path) {
        return Json.object()
                .put("href", baseUrl + path)
                .put("templated", path.contains("{"));
    }

}
