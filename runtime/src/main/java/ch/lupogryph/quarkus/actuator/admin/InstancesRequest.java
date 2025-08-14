package ch.lupogryph.quarkus.actuator.admin;

import java.time.LocalDateTime;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record InstancesRequest(
        String name,
        String serviceUrl,
        String managementUrl,
        String healthUrl,
        Metadata metadata) {

    public InstancesRequest(String name, String host, int port) {
        this(name, "http://%s:%d/".formatted(host, port));
    }

    public InstancesRequest(String name, String url) {
        this(
                name,
                url,
                "%sq".formatted(url),
                "%sq/health".formatted(url),
                new Metadata(LocalDateTime.now()));
    }

}
