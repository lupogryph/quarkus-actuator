package ch.lupogryph.quarkus.actuator.caches;

import static com.lodh.arte.actuator.util.Handler.handler;
import static io.vertx.core.http.HttpMethod.DELETE;
import static io.vertx.core.http.HttpMethod.GET;

import java.util.Map;

import io.quarkus.arc.Arc;
import io.quarkus.runtime.annotations.Recorder;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class CachesRecorder {

    public Handler<RoutingContext> allCaches() {
        var service = Arc.container().instance(CachesService.class).get();
        return handler(Map.of(
                GET, rc -> service.allCaches().build(),
                DELETE, rc -> service.clearAllCaches()));
    }

    public Handler<RoutingContext> cache() {
        var service = Arc.container().instance(CachesService.class).get();
        return handler(Map.of(
                GET, rc -> service.cache(rc.pathParam("name")).build(),
                DELETE, rc -> service.clearCache(rc.pathParam("name"))));
    }

}
