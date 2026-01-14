package ch.lupogryph.quarkus.actuator.metrics;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.Collections;
import java.util.List;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class MetricsServiceTest {

    private static final Logger log = LoggerFactory.getLogger(MetricsServiceTest.class);
    @Inject
    MetricsService metricsService;

    @Test
    void testNames() {
        var names = metricsService.names();
        assertThat(names).isNotNull();
        assertThat(names.names()).isNotEmpty().contains("jvm.memory.max", "jvm.memory.used");
    }

    @Test
    void testMetrics() {
        var metric = metricsService.metrics("jvm.memory.max", Collections.emptyList());
        assertThat(metric).isNotNull();
        assertThat(metric.name()).isEqualTo("jvm.memory.max");
        assertThat(metric.description())
                .isEqualTo("The maximum amount of memory in bytes that can be used for memory management");
        assertThat(metric.baseUnit()).isEqualTo("bytes");
        assertThat(metric.measurements()).isNotEmpty();
        assertThat(metric.availableTags()).isNotEmpty();
    }

    @Test
    void testMetricsHeapAndNonHeap() {
        var usedHeap = metricsService.metrics("jvm.memory.used", List.of("area:heap"));
        var usedNonHeap = metricsService.metrics("jvm.memory.used", List.of("area:nonheap"));
        assertThat(usedHeap).isNotNull();
        assertThat(usedNonHeap).isNotNull();
        assertThat(usedHeap.measurements().getFirst().value())
                .isNotEqualTo(usedNonHeap.measurements().getFirst().value());
    }

}
