package ch.lupogryph.quarkus.actuator.loggers;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record Groups(
        String configuredLevel,
        String[] members) {
}
