package ch.lupogryph.quarkus.actuator.configs;

import static ch.lupogryph.quarkus.actuator.util.Headers.ACTUATOR_CONTENT_TYPE;
import static io.vertx.core.http.HttpHeaders.CONTENT_TYPE;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class ConfigsRecorder {

    private static final Logger log = LoggerFactory.getLogger(ConfigsRecorder.class);

    public Handler<RoutingContext> configs() {
        return rc -> {
            var service = Arc.container().instance(ConfigsService.class).get();
            log.debug("-> {} /configprops", rc.request().method());
            var json = service.contexts().build();
            log.debug("<- {} /q : 200", rc.request().method());
            log.trace(json);
            rc.response()
                    .putHeader(CONTENT_TYPE, ACTUATOR_CONTENT_TYPE)
                    .end(json);
        };
    }

}
