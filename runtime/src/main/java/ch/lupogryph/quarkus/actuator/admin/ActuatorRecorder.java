package ch.lupogryph.quarkus.actuator.admin;

import static ch.lupogryph.quarkus.actuator.util.Headers.ACTUATOR_CONTENT_TYPE;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.net.UnknownHostException;

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

    public Handler<RoutingContext> actuator(ActuatorConfig actuatorConfig, ManagementConfig managementConfig) {

        String host;
        try {
            host = actuatorConfig.getLocalAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        var port = managementConfig.determinePort(LaunchMode.current());
        var baseUrl = "http://%s:%d/q".formatted(host, port);

        return rc -> {
            log.debug("-> {} /q", rc.request().method());
            var json = """
                    {
                      "_links": {
                        "self": {
                          "href": "%1$s",
                          "templated": false
                        },
                        "beans": {
                          "href": "%1$s/beans",
                          "templated": false
                        },
                        "caches-cache": {
                          "href": "%1$s/caches/{cache}",
                          "templated": true
                        },
                        "caches": {
                          "href": "%1$s/caches",
                          "templated": false
                        },
                        "health": {
                          "href": "%1$s/health",
                          "templated": false
                        },
                        "health-path": {
                          "href": "%1$s/health/{*path}",
                          "templated": true
                        },
                        "info": {
                          "href": "%1$s/info",
                          "templated": false
                        },
                        "conditions": {
                          "href": "%1$s/conditions",
                          "templated": false
                        },
                        "configprops": {
                          "href": "%1$s/configprops",
                          "templated": false
                        },
                        "configprops-prefix": {
                          "href": "%1$s/configprops/{prefix}",
                          "templated": true
                        },
                        "env": {
                          "href": "%1$s/env",
                          "templated": false
                        },
                        "env-toMatch": {
                          "href": "%1$s/env/{toMatch}",
                          "templated": true
                        },
                        "loggers": {
                          "href": "%1$s/loggers",
                          "templated": false
                        },
                        "loggers-name": {
                          "href": "%1$s/loggers/{name}",
                          "templated": true
                        },
                        "threaddump": {
                          "href": "%1$s/threaddump",
                          "templated": false
                        },
                        "metrics-requiredMetricName": {
                          "href": "%1$s/metrics/{requiredMetricName}",
                          "templated": true
                        },
                        "metrics": {
                          "href": "%1$s/metrics",
                          "templated": false
                        },
                        "quartz": {
                          "href": "%1$s/quartz",
                          "templated": false
                        },
                        "quartz-jobs-group-name": {
                          "href": "%1$s/quartz/{jobs}/{group}/{name}",
                          "templated": true
                        },
                        "quartz-jobsOrTriggers-group": {
                          "href": "%1$s/quartz/{jobsOrTriggers}/{group}",
                          "templated": true
                        },
                        "quartz-jobsOrTriggers": {
                          "href": "%1$s/quartz/{jobsOrTriggers}",
                          "templated": true
                        },
                        "quartz-jobsOrTriggers-group-name": {
                          "href": "%1$s/quartz/{jobsOrTriggers}/{group}/{name}",
                          "templated": true
                        },
                        "sbom-id": {
                          "href": "%1$s/sbom/{id}",
                          "templated": true
                        },
                        "sbom": {
                          "href": "%1$s/sbom",
                          "templated": false
                        },
                        "scheduledtasks": {
                          "href": "%1$s/scheduledtasks",
                          "templated": false
                        },
                        "mappings": {
                          "href": "%1$s/mappings",
                          "templated": false
                        }
                      }
                    }
                    """.formatted(baseUrl);
            log.debug("<- {} /q : 200", rc.request().method());
            log.trace(json);
            rc.response()
                    .putHeader(CONTENT_TYPE, ACTUATOR_CONTENT_TYPE)
                    .end(json);
        };
    }

}
