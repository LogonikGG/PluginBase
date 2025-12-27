package ru.logonik.pluginBase.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class UtilGrid {

    public static void alignPlayerToGrid(Player player) {
        Location loc = player.getLocation();

        // Выравниваем X и Z по сетке 3x3 внутри блока
        double x = alignTo5PointGrid(loc.getX());
        double y = loc.getY();
        double z = alignTo5PointGrid(loc.getZ());

        // Округляем yaw до 8 направлений
        float yaw = normalizeYaw(loc.getYaw());
        float alignedYaw = roundYawTo8(yaw);

        // Округляем pitch до 45°
        float pitch = roundPitchTo45(loc.getPitch());

        Location newLoc = new Location(loc.getWorld(), x, y, z, alignedYaw, pitch);
        player.teleport(newLoc);
    }

    private static double alignTo5PointGrid(double coord) {
        double blockBase = Math.floor(coord);
        double offset = coord - blockBase;

        double alignedOffset;
        if (offset < 1.0 / 6.0) {
            alignedOffset = 0.0; // Край блока (начало)
        } else if (offset < 1.0 / 3.0) {
            alignedOffset = 1.0 / 6.0; // Центр первой трети
        } else if (offset < 2.0 / 3.0) {
            alignedOffset = 0.5; // Центр блока
        } else if (offset < 5.0 / 6.0) {
            alignedOffset = 5.0 / 6.0; // Центр последней трети
        } else {
            alignedOffset = 1.0; // Край блока (конец)
        }

        return blockBase + alignedOffset;
    }

    // Нормализует yaw в диапазон [0, 360)
    private static float normalizeYaw(float yaw) {
        yaw %= 360;
        return yaw < 0 ? yaw + 360 : yaw;
    }

    // Округляет yaw до ближайших 45 градусов (8 направлений)
    private static float roundYawTo8(float yaw) {
        int sector = Math.round(yaw / 45.0f) % 8;
        return sector * 45.0f;
    }

    // Округляет pitch до -45°, 0° или 45°
    private static float roundPitchTo45(float pitch) {
        pitch = Math.max(-90.0f, Math.min(90.0f, pitch)); // Ограничиваем диапазон
        int sector = Math.round(pitch / 45.0f);
        return sector * 45.0f;
    }
}