package ch.lupogryph.quarkus.actuator.admin;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "spring-boot-admin")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface SpringBootAdminApi {

    @POST
    @Path("instances")
    @ClientHeaderParam(name = "Connection", value = "Upgrade, HTTP2-Settings")
    @ClientHeaderParam(name = "HTTP2-Settings", value = "AAEAAEAAAAIAAAAAAAMAAAAAAAQBAAAAAAUAAEAAAAYABgAA")
    @ClientHeaderParam(name = "Upgrade", value = "h2c")
    InstancesResponse postInstances(InstancesRequest instancesRequest);

}
