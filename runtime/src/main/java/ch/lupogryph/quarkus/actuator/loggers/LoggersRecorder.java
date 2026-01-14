package ch.lupogryph.quarkus.actuator.loggers;

import static ch.lupogryph.quarkus.actuator.util.Handler.handler;
import static io.vertx.core.http.HttpMethod.*;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class LoggersRecorder {

    Logger log = LoggerFactory.getLogger(LoggersRecorder.class);

    public Handler<RoutingContext> loggers() {
        return handler(Map.of(
                GET, rc -> LoggersService.getLoggers()));
    }

    public Handler<RoutingContext> namedLoggers() {
        return handler(Map.of(
                GET, rc -> LoggersService.getLoggers(rc.pathParams().get("name")),
                POST, rc -> LoggersService.setLogger(
                        rc.pathParams().get("name"),
                        rc.body().asPojo(LoggersRequest.class))));
    }

}
