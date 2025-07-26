package ru.logonik.pluginBase;

import java.util.logging.Level;

public class Logger {

    private static java.util.logging.Logger logger;

    public static void error(String s, Throwable ex) {
        logger.log(Level.SEVERE, s, ex);
    }

    public static void error(String s) {
        logger.log(Level.SEVERE, s);
    }

    public static void warn(String s) {
        logger.warning(s);
    }

    public static void warnWithStack(String message) {
        logWithStack(Level.WARNING, message);
    }

    public static void errorWithStack(String message) {
        logWithStack(Level.SEVERE, message);
    }

    private static void logWithStack(Level level, String message) {
        logger.log(level, message);
        StackTraceElement[] stack = new Exception().getStackTrace();
        for (int i = 2; i < stack.length; i++) {
            logger.log(level, "    at " + stack[i]);
        }
    }

    public static void info(String s) {
        logger.info(s);
    }

    public static void setLogger(java.util.logging.Logger logger) {
        Logger.logger = logger;
    }

}
