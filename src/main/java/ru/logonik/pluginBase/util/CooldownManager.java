package ru.logonik.pluginBase.util;

import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class CooldownManager<K> {

    private final ConcurrentMap<K, Long> cooldowns = new ConcurrentHashMap<>();

    public void setCooldown(K key, long durationTicks) {
        cooldowns.put(key, Bukkit.getCurrentTick() + durationTicks);
    }

    public boolean hasCooldown(K key) {
        return getRemainingTicks(key) > 0;
    }

    public long getRemainingTicks(K key) {
        Long expire = cooldowns.get(key);
        if (expire == null) return 0;
        long remaining = expire - Bukkit.getCurrentTick();
        return Math.max(0, remaining);
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
}
