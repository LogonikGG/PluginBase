package ru.logonik.pluginBase.util;

import org.bukkit.Location;
import org.bukkit.World;

public class LocationConverter {
    
    /**
     * Конвертирует Location в Long (пакетные координаты)
     * Формат: 26 бит X | 26 бит Z | 12 бит Y
     */
    public static long locationToLong(Location loc) {
        // Ограничиваем координаты для упаковки в 26 бит
        long x = (long) loc.getBlockX() & 0x3FFFFFF; // 26 бит (67,108,863)
        long z = (long) loc.getBlockZ() & 0x3FFFFFF; // 26 бит
        long y = (long) loc.getBlockY() & 0xFFF;     // 12 бит (4095)
        
        // Упаковываем: X в старшие 26 бит, Z в следующие 26, Y в младшие 12
        return (x << 38) | (z << 12) | y;
    }
    
    /**
     * Конвертирует Long обратно в Location (без World)
     */
    public static Location longToLocation(long packed, World world) {
        // Извлекаем координаты из упакованного значения
        int x = (int) ((packed >> 38) & 0x3FFFFFF);
        int z = (int) ((packed >> 12) & 0x3FFFFFF);
        int y = (int) (packed & 0xFFF);
        
        // Корректируем отрицательные значения (если координаты были отрицательными)
        if (x > 0x2000000) x -= 0x4000000; // Если 26-й бит установлен, то число отрицательное
        if (z > 0x2000000) z -= 0x4000000;
        
        return new Location(world, x, y, z);
    }
    
    /**
     * Альтернативная версия с поддержкой дробных координат
     * Формат: 21 бит X | 21 бит Z | 14 бит Y | 4 бита на дробные части
     */
    public static long locationToLongPrecise(Location loc) {
        // Масштабируем для сохранения одной цифры после запятой
        long x = (long) (loc.getX() * 2) & 0x1FFFFF; // 21 бит
        long z = (long) (loc.getZ() * 2) & 0x1FFFFF; // 21 бит
        long y = (long) (loc.getY() * 4) & 0x3FFF;   // 14 бит
        
        // 4 бита для флагов/доп. информации
        long flags = 0;
        
        return (x << 39) | (z << 18) | (y << 4) | flags;
    }
    
    public static Location longToLocationPrecise(long packed, World world) {
        long x = (packed >> 39) & 0x1FFFFF;
        long z = (packed >> 18) & 0x1FFFFF;
        long y = (packed >> 4) & 0x3FFF;
        
        // Корректируем отрицательные значения
        if (x > 0x100000) x -= 0x200000;
        if (z > 0x100000) z -= 0x200000;
        
        return new Location(world, x / 2.0, y / 4.0, z / 2.0);
    }
    
    /**
     * Версия с направлением (yaw/pitch)
     * Формат: 25 бит X | 25 бит Z | 10 бит Y | 8 бит yaw | 8 бит pitch
     */
    public static long locationToLongWithRotation(Location loc) {
        long x = (long) loc.getBlockX() & 0x1FFFFFF;    // 25 бит
        long z = (long) loc.getBlockZ() & 0x1FFFFFF;    // 25 бит
        long y = (long) loc.getBlockY() & 0x3FF;        // 10 бит (1023)
        long yaw = (long) ((loc.getYaw() + 180) / 360 * 255) & 0xFF;
        long pitch = (long) ((loc.getPitch() + 90) / 180 * 255) & 0xFF;
        
        return (x << 39) | (z << 14) | (y << 8) | yaw;
    }
}