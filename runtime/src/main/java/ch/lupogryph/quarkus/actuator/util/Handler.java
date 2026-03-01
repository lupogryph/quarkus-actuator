package ch.lupogryph.quarkus.actuator.util;

import static ch.lupogryph.quarkus.actuator.util.Headers.ACTUATOR_CONTENT_TYPE;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

public class Handler {

    private static final Logger log = LoggerFactory.getLogger(Handler.class);

    @FunctionalInterface
    public interface ThrowingFunction<RC, O> {
        O apply(RC t) throws Exception;
    }

    public static io.vertx.core.Handler<RoutingContext> handler(
            Map<HttpMethod, ThrowingFunction<RoutingContext, Object>> actions) {
        return rc -> {
            var method = rc.request().method();
            var path = rc.request().path();
            var param = rc.pathParams();
            var body = rc.body().asString();

            log.debug("-> {} {} {} : {}", method, path, param, body);

            try {
                if (actions.containsKey(method)) {
                    var result = actions.get(method).apply(rc);

                    String json;
                    if (result instanceof String s) {
                        json = s;
                    } else {
                        json = Json.encode(result);
                    }

                    log.debug("<- {} {} {} : 200", method, path, param);
                    log.trace(json);
                    rc.response()
                            .putHeader(CONTENT_TYPE, ACTUATOR_CONTENT_TYPE)
                            .end(json);
                } else {
                    rc.response().setStatusCode(405).end();
                }
            } catch (Exception e) {
                log.warn("<- {} {}/{} : 500 : {}", method, path, param, e.getMessage());
                rc.response().setStatusCode(500).end(e.getMessage());
            }
        };
    }
}
