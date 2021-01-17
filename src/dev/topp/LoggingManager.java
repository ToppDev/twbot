package dev.topp;

public class LoggingManager {

    public enum LoggingLevel {
        SILENT,
        CRITICAL,
        WARNING,
        INFO,
        DEBUG
    }

    private static LoggingLevel LOGGING_LEVEL = LoggingLevel.DEBUG;

    /**
     * Prints Logging information to console or file, depending on mode.
     * @param loggingLevel level for importance
     * @param debugInfo Information you want to send.
     * @param functionName Function name with parameter types, e.g. test(String, int)
     * @param className Class name, typically: this.getClass().getSimpleName()
     */
    public static void log(LoggingLevel loggingLevel, String debugInfo, String functionName, String className) {
        if(loggingLevel == LoggingLevel.DEBUG && LOGGING_LEVEL.compareTo(LoggingLevel.DEBUG) >= 0) {
            System.out.println("DEBUG: " + className + "->" + functionName + ": " + debugInfo);
        }
        else if(loggingLevel == LoggingLevel.INFO && LOGGING_LEVEL.compareTo(LoggingLevel.INFO) >= 0) {
            System.out.println("INFO: " + className + "->" + functionName + ": " + debugInfo);
        }
        else if(loggingLevel == LoggingLevel.WARNING && LOGGING_LEVEL.compareTo(LoggingLevel.WARNING) >= 0) {
            System.out.println("WARNING: " + className + "->" + functionName + ": " + debugInfo);
        }
        else if(loggingLevel == LoggingLevel.CRITICAL && LOGGING_LEVEL.compareTo(LoggingLevel.CRITICAL) >= 0) {
            System.out.println("CRITICAL: " + className + "->" + functionName + ": " + debugInfo);
            if(!Main.closeAll())
                System.out.println("Closing all System resources failed. Exiting now anyway.");
            else {
                System.out.println("All System resources were closed. Exiting now.");
            }
            System.exit(0);
        }
    }

    public static void setLoggingLevel(LoggingLevel loggingLevel) {
        LOGGING_LEVEL = loggingLevel;
    }

}
