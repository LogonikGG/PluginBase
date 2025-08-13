package ru.logonik.pluginBase.servicelocator;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.logonik.pluginBase.Logger;
import ru.logonik.pluginBase.Scheduler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ServicesLocator {
    private final Map<Class<?>, Object> services = new HashMap<>();
    private final Scheduler scheduler;
    private final Logger logger;

    public ServicesLocator(Scheduler scheduler, Logger logger) {
        this.scheduler = scheduler;
        this.logger = logger;
        registerService(Logger.class, logger);
    }

    public <T> void registerService(Class<T> clazz, T object) {
        services.put(clazz, object);
    }

    public <T> T getServiceOrThrow(Class<T> clazz) {
        Object o = services.get(clazz);
        Objects.requireNonNull(o);
        return clazz.cast(o);
    }

    public <T> T getService(Class<T> clazz) {
        return clazz.cast(services.get(clazz));
    }

    public Collection<Object> getAllServices() {
        return services.values();
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
                        logger.error("Error while handle player join (async)", e);
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
                    logger.error("Error while handle player join", e);
                }
            }
        }
    }
}
