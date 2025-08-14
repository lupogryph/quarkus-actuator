package ch.lupogryph.quarkus.actuator.loggers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class LoggersServiceTest {

    Logger logger = LoggerFactory.getLogger(LoggersServiceTest.class);

    @Test
    void test() {
        var loggers = LoggersService.getLoggers();
        assertThat(loggers.levels()).containsExactly("OFF", "ERROR", "WARN", "INFO", "DEBUG", "TRACE");
        assertThat(loggers.loggers()).hasSizeGreaterThan(0)
                .extractingByKey(logger.getName())
                .extracting(Loggers::configuredLevel, Loggers::effectiveLevel)
                .containsExactly("DEBUG", "DEBUG");
    }

}
