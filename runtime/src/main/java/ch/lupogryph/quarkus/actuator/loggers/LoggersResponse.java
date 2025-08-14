package ch.lupogryph.quarkus.actuator.loggers;

import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record LoggersResponse(
        String[] levels,
        Map<String, Loggers> loggers,
        Map<String, Groups> groups) {
}
