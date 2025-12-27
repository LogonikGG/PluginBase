package ru.logonik.pluginBase.servicelocator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import ru.logonik.pluginBase.Logger;
import ru.logonik.pluginBase.servicelocator.scheduler.BukkitScheduler;
import ru.logonik.pluginBase.servicelocator.scheduler.Scheduler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BukkitServiceLocator extends ServicesLocator implements Listener {

    protected final Scheduler scheduler;
    private final Map<UUID, String> playerSessions = new ConcurrentHashMap<>();

    public BukkitServiceLocator(Plugin plugin) {
        this.scheduler = new BukkitScheduler(plugin);
        registerService(Plugin.class, plugin);
        registerService(ServicesLocator.class, this);
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
        Plugin plugin = getService(Plugin.class);
        for (Object service : services.values()) {
            if (service instanceof Listener) {
                Bukkit.getPluginManager().registerEvents((Listener) service, plugin);
            }
            if (service instanceof PluginStartListener) {
                PluginStartListener startListener = (PluginStartListener) service;
                startListener.start(this);
            }
        }
        initAlreadyExistedPlayers();
    }

    protected void initAlreadyExistedPlayers() {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        Map<UUID, String> initialSessions = new HashMap<>();
        for (Player player : players) {
            String sessionId = UUID.randomUUID().toString();
            initialSessions.put(player.getUniqueId(), sessionId);
            playerSessions.put(player.getUniqueId(), sessionId);
        }
        scheduler.runAsync(() -> {
            for (Object service : services.values()) {
                if (service instanceof PlayerAvailableListenerAsync) {
                    PlayerAvailableListenerAsync playerAvailableListenerAsync = (PlayerAvailableListenerAsync) service;
                    try {
                        List<Player> playersCopy = new ArrayList<>(players);
                        for (Player player : playersCopy) {
                            UUID playerId = player.getUniqueId();
                            String expectedSession = initialSessions.get(playerId);
                            String currentSession = playerSessions.get(playerId);

                            if (expectedSession != null &&
                                    expectedSession.equals(currentSession) &&
                                    player.isOnline()) {

                                playerAvailableListenerAsync.onPlayerAvailableAsync(player);
                            }
                        }
                    } catch (Exception e) {
                        Logger.error("Error while handle async player available(been joined) in " + service.getClass().getSimpleName(), e);
                    }
                }
            }
        });
        for (Object value : services.values()) {
            if (value instanceof PlayerAvailableListenerSync) {
                PlayerAvailableListenerSync playerAvailableListenerSync = (PlayerAvailableListenerSync) value;
                for (Player player : players) {
                    try {
                        playerAvailableListenerSync.onPlayerAvailableSync(player);
                    } catch (Exception e) {
                        Logger.error("Error while handle player available(been joined) in " + value.getClass().getSimpleName(), e);
                    }
                }
            }
        }
    }

    public void onStop() {
        playerSessions.clear();
        for (Object value : services.values()) {
            if (value instanceof PluginDisableListener) {
                PluginDisableListener disableListener = (PluginDisableListener) value;
                try {
                    disableListener.disable();
                } catch (Exception e) {
                    Logger.error("Error while handle disable plugin in " + value.getClass().getSimpleName(), e);
                }
            }
        }
    }

    protected void onPlayerJoin(Player player) {
        String sessionId = UUID.randomUUID().toString();
        playerSessions.put(player.getUniqueId(), sessionId);
        scheduler.runAsync(() -> {
            for (Object service : services.values()) {
                if (service instanceof PlayerAvailableListenerAsync) {
                    PlayerAvailableListenerAsync playerAvailableListenerAsync = (PlayerAvailableListenerAsync) service;
                    try {
                        String currentSession = playerSessions.get(player.getUniqueId());
                        if (currentSession == null || !currentSession.equals(sessionId)) {
                            break;
                        }
                        playerAvailableListenerAsync.onPlayerAvailableAsync(player);
                    } catch (Exception e) {
                        Logger.error("Error in async join handler " + service.getClass().getSimpleName(), e);
                    }
                }
            }
        });
        for (Object service : services.values()) {
            if (service instanceof PlayerAvailableListenerSync) {
                PlayerAvailableListenerSync playerAvailableListenerSync = (PlayerAvailableListenerSync) service;
                try {
                    playerAvailableListenerSync.onPlayerAvailableSync(player);
                } catch (Exception e) {
                    Logger.error("Error in sync join handler " + service.getClass().getSimpleName(), e);
                }
            }
        }
    }

    protected void onPlayerQuit(Player player) {
        scheduler.runAsync(() -> {
            for (Object service : services.values()) {
                if (service instanceof PlayerQuitListenerAsync) {
                    PlayerQuitListenerAsync playerQuitListenerAsync = (PlayerQuitListenerAsync) service;
                    try {
                        playerQuitListenerAsync.onPlayerQuitAsync(player);
                    } catch (Exception e) {
                        Logger.error("Error in async quit handler " + service.getClass().getSimpleName(), e);
                    }
                }
            }
        });
        for (Object service : services.values()) {
            if (service instanceof PlayerQuitListenerSync) {
                PlayerQuitListenerSync playerQuitListenerSync = (PlayerQuitListenerSync) service;
                try {
                    playerQuitListenerSync.onPlayerQuitSync(player);
                } catch (Exception e) {
                    Logger.error("Error in sync quit handler " + service.getClass().getSimpleName(), e);
                }
            }
        }
    }
}
