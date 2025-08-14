package ch.lupogryph.quarkus.actuator.loggers;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record Loggers(
        String configuredLevel,
        String effectiveLevel) {

    public Loggers(String configuredLevel, String effectiveLevel) {
        this.configuredLevel = configuredLevel;
        this.effectiveLevel = effectiveLevel;
    }

    public Loggers(String level) {
        this(level, level);
    }
}
