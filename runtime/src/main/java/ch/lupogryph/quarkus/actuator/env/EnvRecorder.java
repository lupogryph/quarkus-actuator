package ch.lupogryph.quarkus.actuator.env;

import static ch.lupogryph.quarkus.actuator.util.Handler.handler;
import static io.vertx.core.http.HttpMethod.GET;

import java.util.Map;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class EnvRecorder {

    public Handler<RoutingContext> env() {
        var service = Arc.container().instance(EnvService.class).get();
        return handler(Map.of(GET, rc -> service.env().build()));
    }

}
