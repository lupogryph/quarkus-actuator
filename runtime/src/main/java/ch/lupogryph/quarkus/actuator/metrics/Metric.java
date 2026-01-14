package ch.lupogryph.quarkus.actuator.metrics;

import java.util.List;
import java.util.Set;

public record Metric(
        String name,
        String description,
        String baseUnit,
        List<Measurement> measurements,
        List<AvailableTags> availableTags) {

    public record Measurement(
            String statistic,
            Double value) {
    }

    public record AvailableTags(
            String tag,
            Set<String> values) {
    }

}
