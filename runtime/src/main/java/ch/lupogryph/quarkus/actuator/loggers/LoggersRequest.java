package ch.lupogryph.quarkus.actuator.loggers;

import java.util.Optional;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record LoggersRequest(Optional<String> configuredLevel) {
}
