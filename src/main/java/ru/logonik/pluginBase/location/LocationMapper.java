package ru.logonik.pluginBase.location;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationMapper {
    public static String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," +
                loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," +
                loc.getYaw() + "," + loc.getPitch();
    }

    public static Location deserializeLocation(String str) {
        String[] parts = str.split(",");
        if (parts.length != 6) return null;
        return new Location(
                Bukkit.getWorld(parts[0]),
                Double.parseDouble(parts[1]),
                Double.parseDouble(parts[2]),
                Double.parseDouble(parts[3]),
                Float.parseFloat(parts[4]),
                Float.parseFloat(parts[5])
        );
    }
}
