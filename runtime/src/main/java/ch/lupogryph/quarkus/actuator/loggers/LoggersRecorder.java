package ch.lupogryph.quarkus.actuator.loggers;

import static ch.lupogryph.quarkus.actuator.util.Headers.ACTUATOR_CONTENT_TYPE;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;
import static io.vertx.core.http.HttpMethod.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class LoggersRecorder {

    Logger log = LoggerFactory.getLogger(LoggersRecorder.class);

    public Handler<RoutingContext> loggers() {
        return rc -> {
            var method = rc.request().method();

            log.debug("-> {} /loggers", method);

            if (OPTIONS == method) {
                rc.response().end();
            } else if (GET == method) {
                var loggers = LoggersService.getLoggers();
                var json = Json.encode(loggers);
                log.debug("<- GET /loggers : 200");
                log.trace(json);
                rc.response().putHeader(CONTENT_TYPE, ACTUATOR_CONTENT_TYPE).end(json);
            } else {
                rc.response().setStatusCode(405).end(); // Method not allowed
            }
        };
    }

    public Handler<RoutingContext> namedLoggers() {
        return rc -> {
            var name = rc.pathParams().get("name");
            var method = rc.request().method();

            log.debug("-> {} /loggers/{}", method, name);
            log.trace(rc.body().asString());

            if (GET == method) {
                var loggers = LoggersService.getLoggers(name);
                var json = Json.encode(loggers);
                log.debug("<- GET /loggers/{}", name);
                log.trace(json);
                rc.response().putHeader(CONTENT_TYPE, ACTUATOR_CONTENT_TYPE).end(json);
            } else if (POST == method) {
                var loggersRequest = rc.body().asPojo(LoggersRequest.class);
                LoggersService.setLogger(name, loggersRequest);
                log.debug("<- POST /loggers/{} : 204", name);
                rc.response().setStatusCode(204).end();
            } else {
                rc.response().setStatusCode(405).end(); // Method not allowed
            }

        };
    }

}
