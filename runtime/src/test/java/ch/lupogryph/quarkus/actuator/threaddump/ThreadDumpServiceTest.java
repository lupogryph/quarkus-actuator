package ch.lupogryph.quarkus.actuator.threaddump;

import org.junit.jupiter.api.Test;

import io.vertx.core.json.Json;

class ThreadDumpServiceTest {

    @Test
    void testThreadDump() {
        var json = Json.encode(ThreadDumpService.threadDump());
        System.out.println(json);
    }

    @Test
    void testTextThreadDump() {
        var text = ThreadDumpService.textThreadDump();
        System.out.println(text);
    }

}
