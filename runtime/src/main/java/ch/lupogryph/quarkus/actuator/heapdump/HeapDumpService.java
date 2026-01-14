package ch.lupogryph.quarkus.actuator.heapdump;

import java.lang.management.ManagementFactory;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.management.HotSpotDiagnosticMXBean;

public class HeapDumpService {

    private static final Logger log = LoggerFactory.getLogger(HeapDumpService.class);

    public static final String TMP = Path.of(System.getProperty("java.io.tmpdir"), "heapdump.hprof").toString();

    public static void dumpHeapJVM() throws Exception {
        HotSpotDiagnosticMXBean mxBean = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        mxBean.dumpHeap(TMP, true);
        log.debug("Heap dump created at {}", TMP);
    }

    public static void deleteTmpDump() {
        var dump = Path.of(TMP).toFile();
        if (dump.exists()) {
            if (dump.delete()) {
                log.debug("Heap dump {} deleted", TMP);
            } else {
                log.warn("Heap dump {} could not be deleted", TMP);
            }
        } else {
            log.debug("Heap dump {} does not exist, nothing to delete", TMP);
        }
    }

}
