package ru.logonik.pluginBase.util;

public class UtilThread {
    public static String getCallMethodName() {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2];
        return e.getMethodName();
    }
}
