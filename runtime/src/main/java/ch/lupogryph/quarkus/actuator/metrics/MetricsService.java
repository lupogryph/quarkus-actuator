package ch.lupogryph.quarkus.actuator.metrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

@ApplicationScoped
public class MetricsService {

    @Inject
    MeterRegistry registry;

    public Names names() {
        var names = registry.getMeters().stream()
                .map(m -> m.getId().getName())
                .collect(Collectors.toSet());
        return new Names(names);
    }

    public Tags tags(List<String> tags) {
        return Tags.of(tags.stream()
                .flatMap(tag -> Arrays.stream(tag.split(":")))
                .toArray(String[]::new));
    }

    public Metric metrics(String name, List<String> tags) {
        var rs = registry.get(name);
        if (!tags.isEmpty()) {
            rs.tags(tags(tags));
        }
        Meter meter = rs.meter();
        var gauges = rs.gauges();
        return new Metric(
                meter.getId().getName(),
                meter.getId().getDescription(),
                meter.getId().getBaseUnit(),
                measurements(meter),
                availableTags(gauges));
    }

    public List<Metric.Measurement> measurements(Meter meter) {
        var list = new ArrayList<Metric.Measurement>();
        meter.measure().forEach(measurement -> list.add(
                new Metric.Measurement(
                        measurement.getStatistic().getTagValueRepresentation(),
                        measurement.getValue())));
        return list;
    }

    public List<Metric.AvailableTags> availableTags(Collection<Gauge> gauges) {
        return gauges.stream()
                .flatMap(g -> g.getId().getTags().stream())
                .collect(Collectors.groupingBy(
                        Tag::getKey,
                        Collectors.mapping(Tag::getValue, Collectors.toSet())))
                .entrySet().stream()
                .map(e -> new Metric.AvailableTags(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

}
