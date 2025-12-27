package ru.logonik.pluginBase.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import ru.logonik.pluginBase.util.UtilBukkit;

import java.util.Iterator;
import java.util.Objects;
import java.util.UUID;

public class Zone implements Cloneable {
    private transient Vector firstPosition;
    private transient Vector secondPosition;

    private UUID worldId;
    private int topY;
    private int topX;
    private int topZ;

    private int minX;
    private int minY;
    private int minZ;

    public Zone(UUID worldId, int topX, int topY, int topZ, int minX, int minY, int minZ) {
        this.worldId = worldId;
        this.topX = topX;
        this.topY = topY;
        this.topZ = topZ;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
    }

    public Zone(Zone zone) {
        this.worldId = zone.worldId;
        this.topX = zone.topX;
        this.topY = zone.topY;
        this.topZ = zone.topZ;
        this.minX = zone.minX;
        this.minY = zone.minY;
        this.minZ = zone.minZ;
    }

    public static Zone infinity(UUID worldId) {
        return new Zone(worldId, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
    }

    public static Zone zero(UUID worldId) {
        return new Zone(worldId, 0, 0, 0, 0, 0, 0);
    }

    public boolean isDefault() {
        return (
                topX == Integer.MAX_VALUE && topY == Integer.MAX_VALUE && topZ == Integer.MAX_VALUE &&
                        minX == Integer.MIN_VALUE && minY == Integer.MIN_VALUE && minZ == Integer.MIN_VALUE)

                || (topX == 0 && topY == 0 && topZ == 0 && minX == 0 && minY == 0 && minZ == 0);
    }

    public static Zone normalize(UUID worldId, int firstX, int firstY, int firstZ, int secondX, int secondY, int secondZ) {
        int temp;
        if (firstX < secondX) {
            temp = firstX;
            firstX = secondX;
            secondX = temp;
        }
        if (firstY < secondY) {
            temp = firstY;
            firstY = secondY;
            secondY = temp;
        }
        if (firstZ < secondZ) {
            temp = firstZ;
            firstZ = secondZ;
            secondZ = temp;
        }
        return new Zone(worldId, firstX, firstY, firstZ, secondX, secondY, secondZ);
    }

    public static Zone squareExpandToSky(Location location, int radius) {
        return new Zone(location.getWorld().getUID(),
                location.getBlockX() + radius, location.getWorld().getMaxHeight(), location.getBlockZ() + radius,
                location.getBlockX() - radius, location.getBlockY(), location.getBlockZ() - radius);
    }

    public static Zone squareExpandToSky(Block block, int radius) {
        return new Zone(block.getWorld().getUID(),
                block.getX() + radius, block.getWorld().getMaxHeight(), block.getZ() + radius,
                block.getX() - radius, block.getY(), block.getZ() - radius);
    }

    public static Zone squareOne(Location location, int radius) {
        return new Zone(location.getWorld().getUID(),
                location.getBlockX() + radius, location.getBlockY(), location.getBlockZ() + radius,
                location.getBlockX() - radius, location.getBlockY(), location.getBlockZ() - radius);
    }

    public static Zone square(Location location, int radius) {
        return new Zone(location.getWorld().getUID(),
                location.getBlockX() + radius, location.getBlockY() + radius, location.getBlockZ() + radius,
                location.getBlockX() - radius, location.getBlockY() - radius, location.getBlockZ() - radius);
    }

    public Location getCenter() {
        int centerX = (minX + topX) / 2;
        int centerY = (minY + topY) / 2;
        int centerZ = (minZ + topZ) / 2;
        return new Location(Bukkit.getWorld(worldId), centerX + 0.5, centerY + 0.5, centerZ + 0.5);
    }

    public Location getCenter(World world) {
        int centerX = (minX + topX) / 2;
        int centerY = (minY + topY) / 2;
        int centerZ = (minZ + topZ) / 2;
        return new Location(world, centerX + 0.5, centerY + 0.5, centerZ + 0.5);
    }

    public boolean anyInside(Iterable<Block> blocks) {
        for (Block block : blocks) {
            if (isInside(block)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInside(Block b) {
        int x = b.getX();
        int y = b.getY();
        int z = b.getZ();
        return isInside(b.getWorld().getUID(), x, y, z);
    }

    public boolean isInside(Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return isInside(loc.getWorld().getUID(), x, y, z);
    }

    public boolean isInsideWorldIgnore(Block b) {
        int x = b.getX();
        int y = b.getY();
        int z = b.getZ();
        return isInside(x, y, z);
    }

    public boolean isInsideWorldIgnore(Location loc) {
        int x = loc.getBlockX();
        int y = loc.getBlockY();
        int z = loc.getBlockZ();
        return isInside(x, y, z);
    }

    public boolean isInside(int x, int y, int z) {
        return topX >= x && x >= minX && topY >= y && y >= minY && topZ >= z && z >= minZ;
    }

    public boolean isInside(UUID world, int x, int y, int z) {
        return this.worldId.equals(world) && topX >= x && x >= minX && topY >= y && y >= minY && topZ >= z && z >= minZ;
    }

    public boolean isIntersecting(Zone other) {
        if (!this.worldId.equals(other.worldId)) {
            return false;
        }

        return this.minX <= other.topX && this.topX >= other.minX &&  // По оси X
                this.minY <= other.topY && this.topY >= other.minY &&  // По оси Y
                this.minZ <= other.topZ && this.topZ >= other.minZ;    // По оси Z
    }

    public int getTopY() {
        return topY;
    }

    public int getTopX() {
        return topX;
    }

    public int getTopZ() {
        return topZ;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMinZ() {
        return minZ;
    }

    public Iterator<Block> iterateBottom() {
        World world = UtilBukkit.getWorldOrThrow(worldId);
        return iterateBottom(world);
    }

    public Iterator<Block> iterateBottom(World world) {
        return new Iterator<Block>() {
            int nowX = getMinX() - 1;
            int nowZ = getMinZ();
            final int topX = getTopX();
            final int topZ = getTopZ();

            @Override
            public boolean hasNext() {
                // Если текущий Z меньше или равен topZ, то есть еще блоки для итерации
                return nowZ <= topZ;
            }

            @Override
            public Block next() {
                Block block = world.getBlockAt(nowX, minY, nowZ);

                nowX++;
                if (nowX > topX) {
                    nowX = minX;
                    nowZ++;
                }

                return block;
            }
        };
    }

    @Override
    public String toString() {
        return "Zone{" +
                "topY=" + topY +
                ", topX=" + topX +
                ", topZ=" + topZ +
                ", minX=" + minX +
                ", minY=" + minY +
                ", minZ=" + minZ +
                '}';
    }

    public Iterator<Block> iterate() {
        World world = UtilBukkit.getWorldOrThrow(worldId);
        return iterate(world);
    }

    public Iterator<Block> iterate(World world) {
        return new Iterator<Block>() {
            int nowX = getMinX() - 1;
            int nowY = getMinY();
            int nowZ = getMinZ();
            Block now = world.getBlockAt(nowX, nowY, nowZ);

            @Override
            public boolean hasNext() {
                return nowX != topX || nowY != topY || nowZ != topZ;
            }

            @Override
            public Block next() {
                if (nowX != topX) {
                    nowX++;
                    now = now.getRelative(1, 0, 0);
                    return now;
                }
                if (nowZ != topZ) {
                    nowX = minX;
                    nowZ++;
                    now = world.getBlockAt(nowX, nowY, nowZ);
                    return now;
                }
                if (nowY != topY) {
                    nowX = minX;
                    nowZ = minZ;
                    nowY++;
                    now = world.getBlockAt(nowX, nowY, nowZ);
                    return now;
                }
                throw new IllegalStateException();
            }
        };
    }

    public Iterator<Block> iterateWalls() {
        World world = UtilBukkit.getWorldOrThrow(worldId);
        return iterateWalls(world);
    }

    public Iterator<Block> iterateWalls(World world) {
        return new Iterator<Block>() {
            int minX = getMinX();
            int maxX = getTopX();
            int minY = getMinY();
            int maxY = getTopY();
            int minZ = getMinZ();
            int maxZ = getTopZ();

            int nowX = minX;
            int nowY = minY;
            int nowZ = minZ;
            int direction = 0; // 0: x+, 1: z+, 2: x-, 3: z-
            boolean layerUp = false;

            Block now = world.getBlockAt(nowX, nowY, nowZ);

            @Override
            public boolean hasNext() {
                return nowY <= maxY;
            }

            @Override
            public Block next() {
                Block current = now;

                if (direction == 0) { // x+
                    if (nowX < maxX) {
                        nowX++;
                    } else {
                        direction = 1;
                        nowZ++;
                    }
                } else if (direction == 1) { // z+
                    if (nowZ < maxZ) {
                        nowZ++;
                    } else {
                        direction = 2;
                        nowX--;
                    }
                } else if (direction == 2) { // x-
                    if (nowX > minX) {
                        nowX--;
                    } else {
                        direction = 3;
                        nowZ--;
                    }
                } else if (direction == 3) { // z-
                    if (nowZ > minZ) {
                        nowZ--;
                    } else {
                        direction = 0;
                        nowY++;
                        layerUp = true;
                    }
                }

                if (layerUp) {
                    layerUp = false;
                    nowX = minX;
                    nowZ = minZ;
                }

                now = world.getBlockAt(nowX, nowY, nowZ);
                return current;
            }
        };
    }

    public void drawBorderParticles(Particle particle,
                                    double spacing,
                                    Player player,
                                    double radius) {
        World world = UtilBukkit.getWorldOrThrow(worldId);
        drawBorderParticles(world, particle, spacing, player, radius);
    }

    public void drawBorderParticles(World world,
                                    Particle particle,
                                    double spacing,
                                    Player player,
                                    double radius) {
        // 1) Собираем 8 вершин A…H нашего AABB
        double x1 = Math.min(minX, topX), x2 = Math.max(minX, topX) + 1;
        double y1 = Math.min(minY, topY), y2 = Math.max(minY, topY) + 1;
        double z1 = Math.min(minZ, topZ), z2 = Math.max(minZ, topZ) + 1;

        Location P = player.getLocation();
        Vector Pvec = P.toVector();
        double r2 = radius * radius;

        // 2) Список пар вершин — 12 рёбер
        Vector A = new Vector(x1, y1, z1);
        Vector B = new Vector(x2, y1, z1);
        Vector C = new Vector(x1, y2, z1);
        Vector D = new Vector(x2, y2, z1);
        Vector E = new Vector(x1, y1, z2);
        Vector F = new Vector(x2, y1, z2);
        Vector G = new Vector(x1, y2, z2);
        Vector H = new Vector(x2, y2, z2);

        Vector[][] edges = new Vector[][]{
                {A, B}, {A, C}, {A, E},
                {B, D}, {B, F},
                {C, D}, {C, G},
                {D, H},
                {E, F}, {E, G},
                {F, H},
                {G, H}
        };

        // 3) Для каждого ребра: решаем квадратичное |(A + d·t) - P|^2 = r^2
        for (Vector[] edge : edges) {
            Vector start = edge[0];
            Vector end = edge[1];
            Vector dir = end.clone().subtract(start);
            double L = dir.length();
            dir.normalize(); // единичный вектор направления

            // Коэффициенты квадрата: t^2 + 2*b*t + c = 0
            Vector m = start.clone().subtract(Pvec);
            double b = dir.dot(m);           // b = dir·m
            double c = m.dot(m) - r2;        // c = m·m - r^2
            double disc = b * b - c;           // дискриминант: (2b)^2 - 4·1·c  /4 = b^2 - c
            if (disc < 0) continue; // нет пересечений

            // Найдём t-интервалы, где точка внутри сферы
            double sqrtD = Math.sqrt(disc);
            double t1 = -b - sqrtD;
            double t2 = -b + sqrtD;

            // Пересечение с отрезком [0, L]
            double tMin = Math.max(0, Math.min(t1, t2));
            double tMax = Math.min(L, Math.max(t1, t2));
            if (tMin > tMax) continue;

            // 4) Спавним частицы по отрезку [tMin..tMax] с шагом spacing
            for (double t = tMin; t <= tMax; t += spacing) {
                Vector point = start.clone().add(dir.clone().multiply(t));
                Location loc = new Location(world, point.getX(), point.getY(), point.getZ());
                world.spawnParticle(particle, loc, 1, 0, 0, 0, 0);
            }
        }
    }


    public Zone expandWalls(int expand) {
        this.topX += expand;
        this.topZ += expand;

        this.minX -= expand;
        this.minZ -= expand;
        return this;
    }

    /**
     * Returns a SubZone start from down
     * <p>
     * This method working with {@link Zone#downSubZoneFromDown} so you can use same shiftY
     *
     * @param shiftY Shift from down
     * @return SubZone
     */
    public Zone upSubZoneFromDown(int shiftY) {
        if (shiftY < 0 || shiftY > topY - minY) {
            throw new IllegalStateException("Invalid shiftY value: `" + shiftY + "`, Zone height is " + (topY - minY));
        }
        return new Zone(worldId, topX, topY, topZ, minX, minY + shiftY, minZ);
    }

    /**
     * Returns a SubZone start from down
     * <p>
     * This method working with {@link Zone#upSubZoneFromDown} so you can use same shiftY
     *
     * @param shiftY Shift from down
     * @return down
     */
    public Zone downSubZoneFromDown(int shiftY) {
        if (shiftY < 0 || shiftY > topY - minY) {
            throw new IllegalStateException("Invalid shiftY value: `" + shiftY + "`, Zone height is " + (topY - minY));
        }
        return new Zone(worldId, topX, minY + shiftY - 1, topZ, minX, minY, minZ);
    }

    public Zone upSubZoneFromTop(int shiftY) {
        if (shiftY < 0 || shiftY > topY - minY) {
            throw new IllegalStateException("Invalid shiftY value: `" + shiftY + "`, Zone height is " + (topY - minY));
        }
        return new Zone(worldId, topX, topY, topZ, minX, topY - shiftY, minZ);
    }

    public Zone downSubZoneFromTop(int shiftY) {
        if (shiftY < 0 || shiftY > topY - minY) {
            throw new IllegalStateException("Invalid shiftY value: `" + shiftY + "`, Zone height is " + (topY - minY));
        }
        return new Zone(worldId, topX, topY - shiftY - 1, topZ, minX, minY, minZ);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Zone zone = (Zone) o;
        return topY == zone.topY && topX == zone.topX && topZ == zone.topZ && minX == zone.minX && minY == zone.minY && minZ == zone.minZ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(topY, topX, topZ, minX, minY, minZ);
    }

    @Override
    public Zone clone() {
        try {
            Zone clone = (Zone) super.clone();
            clone.topY = topY;
            clone.topX = topX;
            clone.topZ = topZ;

            clone.minX = minX;
            clone.minY = minY;
            clone.minZ = minZ;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void setFirstPos(Location location) {
        initTempPositions();
        firstPosition = location.toVector();
        recountMaxMinByPositions();
    }

    public void setSecondPos(Location location) {
        initTempPositions();
        secondPosition = location.toVector();
        recountMaxMinByPositions();
    }

    public Vector getFirstPos() {
        initTempPositions();
        return firstPosition;
    }

    public Vector getSecondPos() {
        initTempPositions();
        return secondPosition;
    }

    private void initTempPositions() {
        if (firstPosition == null || secondPosition == null) {
            firstPosition = new Vector(topX, topY, topZ);
            secondPosition = new Vector(minX, minY, minZ);
        }
    }

    private void recountMaxMinByPositions() {
        if (firstPosition != null && secondPosition != null) {
            topX = Math.max(firstPosition.getBlockX(), secondPosition.getBlockX());
            topY = Math.max(firstPosition.getBlockY(), secondPosition.getBlockY());
            topZ = Math.max(firstPosition.getBlockZ(), secondPosition.getBlockZ());

            minX = Math.min(firstPosition.getBlockX(), secondPosition.getBlockX());
            minY = Math.min(firstPosition.getBlockY(), secondPosition.getBlockY());
            minZ = Math.min(firstPosition.getBlockZ(), secondPosition.getBlockZ());
        }
    }
}

