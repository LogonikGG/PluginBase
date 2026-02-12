package ru.logonik.pluginBase;

import org.bukkit.plugin.java.JavaPlugin;
import ru.logonik.pluginBase.servicelocator.BukkitServiceLocator;
import ru.logonik.pluginBase.util.LogoUtils;

public abstract class BootstrapPlugin extends JavaPlugin {

    protected final BukkitServiceLocator serviceLocator;
    protected boolean stableStart;

    public BootstrapPlugin() {
        this.serviceLocator = new BukkitServiceLocator(this);
    }

    @Override
    public void onEnable() {
        stableStart = false;
        try {
            serviceLocator.onStart();
        } catch (Exception e) {
            LogoUtils.sneakyThrow(e);
        }
        stableStart = true;
    }

    @Override
    public void onDisable() {
        if (stableStart) {
            serviceLocator.onStop();
        }
    }

    public BukkitServiceLocator getServiceLocator() {
        return serviceLocator;
    }
}
