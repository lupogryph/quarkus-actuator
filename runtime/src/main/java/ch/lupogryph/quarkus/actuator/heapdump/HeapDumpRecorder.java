package ch.lupogryph.quarkus.actuator.heapdump;

import static jakarta.ws.rs.core.HttpHeaders.CONTENT_DISPOSITION;
import static jakarta.ws.rs.core.HttpHeaders.CONTENT_TYPE;

import io.quarkus.runtime.annotations.Recorder;
import io.vertx.ext.web.RoutingContext;

@Recorder
public class HeapDumpRecorder {

    public io.vertx.core.Handler<RoutingContext> dump() {
        return rc -> {
            try {
                HeapDumpService.dumpHeapJVM();
                rc.response()
                        .putHeader(CONTENT_TYPE, "application/octet-stream")
                        .putHeader(CONTENT_DISPOSITION, "attachment; filename=\"heapdump.hprof\"")
                        .sendFile(HeapDumpService.TMP)
                        .onComplete(ar -> {
                            HeapDumpService.deleteTmpDump();
                        });
            } catch (Exception e) {
                rc.response().setStatusCode(500).end(e.getMessage());
            }
        };
    }
}
