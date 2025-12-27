package ru.logonik.pluginBase.util;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class UtilBukkit {
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + String.valueOf('&') + "[0-9A-FK-OR]");


    public static String colorize(String raw) {
        return ChatColor.translateAlternateColorCodes('&', raw);
    }

    public static String removeColorCodes(String input) {
        return input == null ? null : ChatColor.stripColor(STRIP_COLOR_PATTERN.matcher(input).replaceAll(""));
    }

    public static Location getLocFromSection(ConfigurationSection section, World world) {
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        double yaw = section.getDouble("yaw");
        double pitch = section.getDouble("pitch");
        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    public static void setLocation(YamlConfiguration yaml, String path, Location location) {
        yaml.set(path + ".x", location.getX());
        yaml.set(path + ".y", location.getY());
        yaml.set(path + ".z", location.getZ());
        yaml.set(path + ".yaw", location.getYaw());
        yaml.set(path + ".pitch", location.getPitch());
    }

    public static List<Block> getBlocksAround(Block block) {
        ArrayList<Block> result = new ArrayList<>();
        for (int x = -1; x < 2; x = x + 1) {
            for (int y = -1; y < 2; y = y + 1) {
                for (int z = -1; z < 2; z = z + 1) {
                    result.add(block.getRelative(x, y, z));
                }
            }
        }
        return result;
    }

    public static List<Block> getBlocksAround(Block block, int radius) {
        List<Block> result = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    result.add(block.getRelative(x, y, z));
                }
            }
        }
        return result;
    }

    public static List<Location> getAroundCircleLocations(Location center, int radius, int counts, boolean highestBlock) {
        List<Location> result = new ArrayList<>();
        int randomShift = new Random().nextInt(360);
        int base_angle = 360 / counts;
        for (int i = 1; i <= counts; i++) {
            int angle = randomShift + base_angle * i;
            double radians = Math.toRadians(angle);

            double cos = Math.cos(radians);
            double sin = Math.sin(radians);

            double x = center.getX() + cos * radius;
            double z = center.getZ() + sin * radius;

            double dx = center.getX() - x;
            double dz = center.getZ() - z;
            float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));

            Location loc = new Location(center.getWorld(), x, highestBlock ? center.getWorld().getHighestBlockAt((int) x, (int) z).getY() : center.getY(), z, yaw,0);
            result.add(loc);
        }
        return result;
    }

    public static World getWorldOrThrow(String name) {
        World world = Bukkit.getWorld(name);
        if (world == null) {
            throw new IllegalArgumentException("World with name '" + name + "' not found, or not loaded");
        }
        return world;
    }

    public static World getWorldOrThrow(UUID uuid) {
        World world = Bukkit.getWorld(uuid);
        if (world == null) {
            throw new IllegalArgumentException("World with uuid '" + uuid + "' not found, or not loaded");
        }
        return world;
    }

    public static String getNames(Collection<? extends Player> gamers) {
        return gamers.stream().map(Player::getName).collect(Collectors.joining(", "));
    }
    public static List<String> getNamesList(Collection<? extends Player> gamers) {
        return gamers.stream().map(Player::getName).collect(Collectors.toList());
    }

    public static String getStrWithPass(String[] args, int pass) {
        return Arrays.stream(args).skip(pass).collect(Collectors.joining(" "));
    }


    public static Block findSolidBlockUnder(Block block) {
        do {
            block = block.getRelative(BlockFace.DOWN);
        } while (!block.getType().isSolid() && block.getY() >= block.getWorld().getMinHeight());
        return block;
    }

    public static void giveItemOrDrop(Player player, ItemStack... itemStacks) {
        HashMap<Integer, ItemStack> notFit = player.getInventory().addItem(itemStacks);
        Location location = player.getLocation();
        for (ItemStack notFitted : notFit.values()) {
            if(notFitted == null) continue;
            location.getWorld().dropItem(location, notFitted);
        }
    }

    public static ItemStack[] deepCopyItemStackArray(ItemStack[] original) {
        if (original == null) {
            return null;
        }
        ItemStack[] copy = new ItemStack[original.length];
        for (int i = 0; i < original.length; i++) {
            if (original[i] != null) {
                copy[i] = original[i].clone();
            }
        }
        return copy;
    }

    public static Collection<PotionEffect> deepCopyPotionEffects(Collection<PotionEffect> original) {
        if (original == null) {
            return null;
        }
        Collection<PotionEffect> copy = new ArrayList<>(original.size());
        for (PotionEffect effect : original) {
            if (effect != null) {
                copy.add(new PotionEffect(
                        effect.getType(),
                        effect.getDuration(),
                        effect.getAmplifier(),
                        effect.isAmbient(),
                        effect.hasParticles(),
                        effect.hasIcon()
                ));
            }
        }

        return copy;
    }
}
