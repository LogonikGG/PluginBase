package ru.logonik.pluginBase;

import org.bukkit.plugin.Plugin;
import ru.logonik.pluginBase.servicelocator.BukkitServiceLocator;

public class BootstrapPlugin {

    private BukkitServiceLocator serviceLocator;
    private boolean stableStart;

    public BootstrapPlugin(Plugin plugin) {
        Logger.setLogger(plugin.getLogger());
        this.serviceLocator = new BukkitServiceLocator(plugin);
        stableStart = false;
    }

    public void start() throws Exception {
        serviceLocator.onStart();
        stableStart = true;
    }

    public void stop() {
        if (stableStart) {
            serviceLocator.onStop();
        }
    }

    public BukkitServiceLocator getServiceLocator() {
        return serviceLocator;
    }
}
