package ru.logonik.pluginBase.util;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.List;

public abstract class UtilFormat {
    public static String format(double value) {
        long integralPart = (long) value;
        int decimalPart = (int) ((value - integralPart) * 10);
        return integralPart + "." + Math.abs(decimalPart);
    }

    public static String format(Location location) {
        return location.getWorld().getName() + " " + format(location.getX()) + " " + format(location.getY()) + " " + format(location.getZ());
    }

    public static String formatWithoutWorld(Location location) {
        return format(location.getX()) + " " + format(location.getY()) + " " + format(location.getZ());
    }

    public static String formatWithoutWorld(Vector location) {
        return format(location.getX()) + " " + format(location.getY()) + " " + format(location.getZ());
    }

    public static List<String> formatToReadableList(Location location) {
        return List.of("X: " + format(location.getX()), "Y: " + format(location.getY()), "Z: " + format(location.getZ()));
    }
}