package ch.lupogryph.quarkus.actuator.loggers;

import java.util.Map;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record LoggersResponse(
        String[] levels,
        Map<String, Loggers> loggers,
        Map<String, Groups> groups) {

    public static final String[] LEVELS = { "OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE" };

    public LoggersResponse(Map<String, Loggers> loggers, Map<String, Groups> groups) {
        this(LEVELS, loggers, groups);
    }
}
