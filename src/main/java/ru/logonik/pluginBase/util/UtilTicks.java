package ru.logonik.pluginBase.util;

public abstract class UtilTicks {
    public static String ticksToLastTime(Number ticks) {
        int totalSecs = (int) (ticks.longValue() / 20);
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;
        int seconds = totalSecs % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}