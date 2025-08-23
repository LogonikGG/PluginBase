package ru.logonik.pluginBase.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CooldownManager<K> {

    private static final ConcurrentMap<Class<?>, CooldownManager<?>> INSTANCES = new ConcurrentHashMap<>();

    private final ConcurrentMap<K, Long> cooldowns = new ConcurrentHashMap<>();
    private final Plugin plugin;
    private final AtomicBoolean cleaning = new AtomicBoolean(false);
    private BukkitTask cleanerTask;

    private CooldownManager(Plugin plugin) {
        this.plugin = plugin;
        startCleaner();
    }

    @SuppressWarnings("unchecked")
    public static <K> CooldownManager<K> getManager(Plugin plugin) {
        return (CooldownManager<K>) INSTANCES.computeIfAbsent(plugin.getClass(), k -> new CooldownManager<>(plugin));
    }

    public void setCooldown(K key, long durationTicks) {
        cooldowns.put(key, System.currentTimeMillis() + ticksToMillis(durationTicks));
    }

    public boolean hasCooldown(K key) {
        return getRemainingTicks(key) > 0;
    }

    public long getRemainingTicks(K key) {
        Long expire = cooldowns.get(key);
        if (expire == null) return 0;
        long remaining = expire - System.currentTimeMillis();
        return Math.max(0, millisToTicks(remaining));
    }

    public void clearCooldown(K key) {
        cooldowns.remove(key);
    }

    public Set<K> getAllKeys() {
        return Collections.unmodifiableSet(cooldowns.keySet());
    }

    public String getFormattedTime(K key) {
        long ticks = getRemainingTicks(key);
        if (ticks <= 0) return "0s";

        long seconds = ticks / 20;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return minutes > 0 ? minutes + "m " + seconds + "s" : seconds + "s";
    }

    public long getRemaining(K key, TimeUnit unit) {
        long ticks = getRemainingTicks(key);
        switch (unit) {
            case SECONDS:
                return ticks / 20;
            case MINUTES:
                return ticks / (20 * 60);
            case MILLISECONDS:
                return ticksToMillis(ticks);
            default:
                return ticks;
        }
    }

    private void startCleaner() {
        if (cleaning.compareAndSet(false, true)) {
            cleanerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                long now = System.currentTimeMillis();
                cooldowns.entrySet().removeIf(e -> e.getValue() <= now);
            }, 20L, 20L);
        }
    }

    public void stopCleaner() {
        if (cleanerTask != null) cleanerTask.cancel();
        cleaning.set(false);
    }

    private static long ticksToMillis(long ticks) {
        return ticks * 50L;
    }

    private static long millisToTicks(long millis) {
        return millis / 50L;
    }
}
