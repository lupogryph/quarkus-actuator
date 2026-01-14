package ch.lupogryph.quarkus.actuator.threaddump;

import java.lang.management.ThreadInfo;

public record ThreadDump(ThreadInfo[] threads) {}
