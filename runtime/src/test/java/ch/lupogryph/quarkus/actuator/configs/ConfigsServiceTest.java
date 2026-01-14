package ch.lupogryph.quarkus.actuator.configs;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ConfigsServiceTest {

    @Inject
    ConfigsService configsService;

    @Test
    void hide() {
        assertThat(configsService.hide("nothing.to.hide")).isFalse();
        assertThat(configsService.hide("something.with.password")).isTrue();
        assertThat(configsService.hide("something.with.Password")).isTrue();
        assertThat(configsService.hide("something.password.hide")).isTrue();
        assertThat(configsService.hide("something.Password.hide")).isTrue();
    }
}
