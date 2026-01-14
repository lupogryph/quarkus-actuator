package ch.lupogryph.quarkus.actuator.beans;

import static ch.lupogryph.quarkus.actuator.util.Handler.handler;
import static io.vertx.core.http.HttpMethod.GET;

import java.util.Map;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class BeansRecorder {

    public Handler<RoutingContext> beans() {
        var service = Arc.container().instance(BeansService.class).get();
        return handler(Map.of(
                GET, rc -> service.contexts().build()));
    }

}
