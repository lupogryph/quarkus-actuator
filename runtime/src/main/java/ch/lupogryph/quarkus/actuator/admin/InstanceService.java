package ch.lupogryph.quarkus.actuator.admin;

import java.net.UnknownHostException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.vertx.http.runtime.management.ManagementConfig;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

@ApplicationScoped
public class InstanceService {

    Logger log = LoggerFactory.getLogger(InstanceService.class);

    @Inject
    @RestClient
    SpringBootAdminApi api;

    @ConfigProperty(name = "quarkus.application.name", defaultValue = "lo-actuator")
    String applicationName;

    @ConfigProperty(name = "quarkus.rest-client.spring-boot-admin.url")
    String adminUrl;

    @Inject
    ActuatorConfig actuatorConfig;

    @Inject
    ManagementConfig managementConfig;

    String instanceId;

    void postInstances(@Observes StartupEvent ev) {
        var url = "%s/instances".formatted(adminUrl);
        try {
            var request = createRequest();
            log.debug("<- POST {}", url);
            log.trace(Json.encode(request));
            var res = api.postInstances(request);
            log.debug("-> Registered in Spring Boot Admin : {}/{}", url, res.id());
            instanceId = res.id();
        } catch (UnknownHostException e) {
            log.error("Could not send instance request: {}", e.getMessage());
        } catch (WebApplicationException e) {
            log.error("-> POST {} [{}] : Could not register in Spring Boot Admin : {}", url, e.getResponse().getStatus(),
                    e.getMessage());
        } catch (RuntimeException e) {
            log.error("Could not register in Spring Boot Admin : {}", e.getMessage());
        }
    }

    public InstancesRequest createRequest() throws UnknownHostException {
        return new InstancesRequest(applicationName, actuatorConfig.getLocalAddress(),
                managementConfig.determinePort(LaunchMode.current()));
    }

    public void routes(@Observes Router router) {
        router.get("/q/actuator/instance").handler(rc -> {
            if (instanceId == null || instanceId.isBlank()) {
                rc.response().setStatusCode(404).end("InstanceId does not exist");
            } else {
                rc.response().setStatusCode(303).putHeader("Location", instanceUrl()).end();
            }
        });
    }

    public String instanceUrl() {
        return "%s/instances/%s/details".formatted(adminUrl, instanceId);
    }

}
