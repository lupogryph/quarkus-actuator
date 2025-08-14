package ch.lupogryph.quarkus.actuator.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.UnknownHostException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;

@QuarkusTest
public class InstanceServiceTest {

    @InjectSpy
    InstanceService instanceService;

    @InjectMock
    @RestClient
    SpringBootAdminApi api;

    @Test
    void testCreateRequest() throws UnknownHostException {
        var expected = new InstancesRequest(
                "lo-actuator",
                "http://localhost:19001/",
                "http://localhost:19001/q",
                "http://localhost:19001/q/health",
                new Metadata(null));

        var req = instanceService.createRequest();

        assertEquals(expected.name(), req.name());
        assertEquals(expected.serviceUrl(), req.serviceUrl());
        assertEquals(expected.managementUrl(), req.managementUrl());
        assertEquals(expected.healthUrl(), req.healthUrl());
        assertNotNull(req.metadata());
        assertNotNull(req.metadata().startup());
    }

}
