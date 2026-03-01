package ch.lupogryph.quarkus.actuator.admin;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record InstancesResponse(String id) {
}
