package ru.logonik.pluginBase.servicelocator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.logonik.pluginBase.BukkitScheduler;
import ru.logonik.pluginBase.Logger;
import ru.logonik.pluginBase.Scheduler;

import java.util.Collection;

public class BukkitServiceLocator extends ServicesLocator implements PluginStartListener, Listener {

    protected final Scheduler scheduler;

    public BukkitServiceLocator(Plugin plugin, Logger logger) {
        super(logger);
        this.scheduler = new BukkitScheduler(plugin);
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



    public void onStart() throws Exception {
        for (Object value : services.values()) {
            if (value instanceof PluginStartListener) {
                PluginStartListener startListener = (PluginStartListener) value;
                try {
                    startListener.start(this);
                } catch (Exception e) {
                    logger.error("Error while start", e);
                    throw e;
                }
            }
        }
        initAlreadyExistedPlayers();
    }

    protected void initAlreadyExistedPlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        scheduler.runAsync(() -> {
            for (Object value : services.values()) {
                if (value instanceof PlayerAvailableListener) {
                    PlayerAvailableListener playerAvailableListener = (PlayerAvailableListener) value;
                    try {
                        for (Player player : players) {
                            if (player.isOnline()) {
                                playerAvailableListener.onPlayerAvailableAsync(player);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error while handle player join", e);
                    }
                }
            }
        });
    }

    public void onStop() {
        for (Object value : services.values()) {
            if (value instanceof PluginDisableListener) {
                PluginDisableListener disableListener = (PluginDisableListener) value;
                try {
                    disableListener.disable();
                } catch (Exception e) {
                    logger.error("Error while disable", e);
                }
            }
        }
    }

    protected void onPlayerJoin(Player player) {
        scheduler.runAsync(() -> {
            for (Object value : services.values()) {
                if (value instanceof PlayerAvailableListener) {
                    PlayerAvailableListener playerAvailableListener = (PlayerAvailableListener) value;
                    try {
                        playerAvailableListener.onPlayerAvailableAsync(player);
                    } catch (Exception e) {
                        logger.error("Error while handle player join", e);
                    }
                }
            }
        });
    }

    protected void onPlayerQuit(Player player) {
        scheduler.runAsync(() -> {
            for (Object value : services.values()) {
                if (value instanceof PlayerQuitListenerAsync) {
                    PlayerQuitListenerAsync playerQuitListenerAsync = (PlayerQuitListenerAsync) value;
                    try {
                        playerQuitListenerAsync.onPlayerQuitAsync(player);
                    } catch (Exception e) {
                        logger.error("Error while handle player quit (async)", e);
                    }
                }
            }
        });
        for (Object value : services.values()) {
            if (value instanceof PlayerQuitListenerSync) {
                PlayerQuitListenerSync playerQuitListenerSync = (PlayerQuitListenerSync) value;
                try {
                    playerQuitListenerSync.onPlayerQuitSync(player);
                } catch (Exception e) {
                    logger.error("Error while handle player quit", e);
                }
            }
        }
    }
}
