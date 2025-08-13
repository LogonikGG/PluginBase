package ru.logonik.pluginBase;

import java.util.logging.Level;

public class Logger {

    private final java.util.logging.Logger logger;

    public Logger(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    public void error(String s, Throwable ex) {
        logger.log(Level.SEVERE, s, ex);
    }

    public void error(String s) {
        logger.log(Level.SEVERE, s);
    }

    public void warn(String s) {
        logger.warning(s);
    }

    public void warnWithStack(String message) {
        logWithStack(Level.WARNING, message);
    }

    public void errorWithStack(String message) {
        logWithStack(Level.SEVERE, message);
    }

    private void logWithStack(Level level, String message) {
        logger.log(level, message);
        StackTraceElement[] stack = new Exception().getStackTrace();
        for (int i = 2; i < stack.length; i++) {
            logger.log(level, "    at " + stack[i]);
        }
    }

    public void info(String s) {
        logger.info(s);
    }
}
