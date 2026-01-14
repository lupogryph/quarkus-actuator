package ch.lupogryph.quarkus.actuator.threaddump;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.function.Function;

public class ThreadDumpService {

    private static final PlainTextThreadDumpFormatter plainTextFormatter = new PlainTextThreadDumpFormatter();

    public static ThreadDump threadDump() {
        return getFormattedThreadDump(ThreadDump::new);
    }

    public static String textThreadDump() {
        return getFormattedThreadDump(plainTextFormatter::format);
    }

    private static <T> T getFormattedThreadDump(Function<ThreadInfo[], T> formatter) {
        return formatter.apply(ManagementFactory.getThreadMXBean().dumpAllThreads(true, true));
    }

}
