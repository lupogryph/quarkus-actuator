package ch.lupogryph.quarkus.actuator.metrics;

import static ch.lupogryph.quarkus.util.Handler.handler;
import static io.vertx.core.http.HttpMethod.GET;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class MetricsRecorder {

    Logger log = LoggerFactory.getLogger(MetricsRecorder.class);

    public Handler<RoutingContext> names() {
        var service = Arc.container().instance(MetricsService.class).get();
        return handler(Map.of(GET, rc -> service.names()));
    }

    public Handler<RoutingContext> metric() {
        var service = Arc.container().instance(MetricsService.class).get();
        return handler(Map.of(GET, rc -> service.metrics(rc.pathParam("name"), rc.queryParam("tag"))));
    }
}
