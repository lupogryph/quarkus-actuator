package ch.lupogryph.quarkus.actuator.threaddump;

import static ch.lupogryph.quarkus.actuator.util.Headers.ACTUATOR_CONTENT_TYPE;
import static io.vertx.core.http.HttpMethod.GET;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class ThreadDumpRecorder {

    private static final Logger log = LoggerFactory.getLogger(ThreadDumpRecorder.class);

    public Handler<RoutingContext> dump() {
        return rc -> {
            var method = rc.request().method();
            var path = rc.request().path();
            log.debug("-> {} {}", method, path);
            if (GET.equals(rc.request().method())) {
                var accept = rc.request().getHeader("Accept");
                if (accept != null) {
                    String dump;
                    if (accept.contains(TEXT_PLAIN)) {
                        dump = ThreadDumpService.textThreadDump();
                        log.debug("<- {} {} : 200", method, path);
                        log.trace(dump);
                        rc.response()
                                .putHeader(CONTENT_TYPE, TEXT_PLAIN)
                                .end(dump);
                    } else if (accept.contains(APPLICATION_JSON)) {
                        dump = Json.encode(ThreadDumpService.threadDump());
                        log.debug("<- {} {} : 200", method, path);
                        log.trace(dump);
                        rc.response()
                                .putHeader(CONTENT_TYPE, ACTUATOR_CONTENT_TYPE)
                                .end(dump);
                    } else {
                        log.warn("<- {} {} : 406", method, path);
                        rc.response().setStatusCode(406).end();
                    }
                }
            } else {
                log.warn("<- {} {} : 405", method, path);
                rc.response().setStatusCode(405).end();
            }
        };
    }

}
