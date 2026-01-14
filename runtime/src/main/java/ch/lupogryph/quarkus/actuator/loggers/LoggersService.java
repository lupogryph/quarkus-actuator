package ch.lupogryph.quarkus.actuator.loggers;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;

import org.jboss.logmanager.LogContext;

public final class LoggersService {

    private static final LogContext logContext = LogContext.getLogContext();

    private LoggersService() {
    }

    public static LoggersResponse getLoggers() {
        return new LoggersResponse(mapLoggers(), mapGroups());
    }

    static SortedMap<String, Loggers> mapLoggers() {
        var loggers = new TreeMap<String, Loggers>();
        var loggerNames = logContext.getLoggerNames();
        while (loggerNames.hasMoreElements()) {
            var loggerName = loggerNames.nextElement();
            if (loggerName.isBlank()) {
                loggerName = "ROOT";
            }
            loggers.put(loggerName, getLoggers(loggerName));
        }
        return loggers;
    }

    static Loggers getLoggers(String name) {
        var logger = logContext.getLogger(name);
        var level = getEffectiveLogLevel(logger);
        return new Loggers(level);
    }

    static SortedMap<String, Groups> mapGroups() {
        return new TreeMap<>();
    }

    public static Void setLogger(String name, LoggersRequest loggersRequest) {
        var logger = logContext.getLogger(name);
        var level = loggersRequest.configuredLevel().map(Level::parse).orElse(logger.getParent().getLevel());
        logger.setLevel(level);
        return null;
    }

    public static String getEffectiveLogLevel(org.jboss.logmanager.Logger logger) {
        if (logger == null) {
            return null;
        }
        if (logger.getLevel() != null) {
            return logger.getLevel().getName();
        }
        return getEffectiveLogLevel(logger.getParent());
    }

}
