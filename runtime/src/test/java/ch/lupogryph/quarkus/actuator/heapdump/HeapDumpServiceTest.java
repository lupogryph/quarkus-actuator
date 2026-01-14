package ch.lupogryph.quarkus.actuator.heapdump;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

import org.junit.jupiter.api.Test;

class HeapDumpServiceTest {

    @Test
    void testDumpHeapJVM() throws Exception {
        HeapDumpService.dumpHeapJVM();
        var dump = new File(HeapDumpService.TMP);
        assertTrue(dump.exists());
        System.out.println(dump.length());
        assertTrue(dump.length() > 0);
        HeapDumpService.deleteTmpDump();
    }

}
