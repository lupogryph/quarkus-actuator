package ch.lupogryph.quarkus.actuator.admin;

import java.time.LocalDateTime;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record Metadata(
        LocalDateTime startup) {
}
