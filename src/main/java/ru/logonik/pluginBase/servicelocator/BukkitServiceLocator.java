package ru.logonik.pluginBase.servicelocator;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.logonik.pluginBase.BukkitScheduler;

public class BukkitServiceLocator extends ServicesLocator implements PluginStartListener, Listener {

    public BukkitServiceLocator(Plugin plugin) {
        super(new BukkitScheduler(plugin));
        registerService(Plugin.class, plugin);
        registerService(ServicesLocator.class, this);
    }

    @Override
    public void start(ServicesLocator servicesLocator) throws Exception {
        Plugin plugin = servicesLocator.getService(Plugin.class);
        for (Object service : servicesLocator.getAllServices()) {
            if(service instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) service, plugin);
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        onPlayerQuit(event.getPlayer());
    }
}
