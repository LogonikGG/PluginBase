package ru.logonik.pluginBase.servicelocator.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class BukkitScheduler implements Scheduler {
    private final Plugin plugin;

    public BukkitScheduler(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void runRepeatingAsync(Runnable task, long delayMillis, long periodMillis) {
        long delayTicks = delayMillis / 50;
        long periodTicks = periodMillis / 50;
        plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
    }

    @Override
    public void runRepeatingSync(Runnable task, long delayMillis, long periodMillis) {
        long delayTicks = delayMillis / 50;
        long periodTicks = periodMillis / 50;
        plugin.getServer().getScheduler().runTaskTimer(plugin, task, delayTicks, periodTicks);
    }

    @Override
    public void runAsync(Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, task);
    }

    @Override
    public void runSync(Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    @Override
    public void runInMainThread(Runnable task) {
        if(Bukkit.isPrimaryThread()) {
            task.run();
        } else {
            plugin.getServer().getScheduler().runTask(plugin, task);
        }
    }
}