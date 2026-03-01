package ch.lupogryph.quarkus.actuator.env;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class EnvServiceTest {

    @Inject
    EnvService service;

    @Test
    void testEnv() {
        var env = service.env().build().toString();
        assertThat(env).isNotBlank();
    }

}
