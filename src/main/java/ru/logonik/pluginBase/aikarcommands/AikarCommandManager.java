package ru.logonik.pluginBase.aikarcommands;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.Plugin;
import ru.logonik.pluginBase.servicelocator.PluginDisableListener;
import ru.logonik.pluginBase.servicelocator.PluginStartListener;
import ru.logonik.pluginBase.servicelocator.ServicesLocator;

import java.util.Map;

public abstract class AikarCommandManager implements PluginStartListener, PluginDisableListener {

    protected PaperCommandManager commandManager;

    @Override
    public void start(ServicesLocator servicesLocator) throws Exception {
        Plugin plugin = servicesLocator.getServiceOrThrow(Plugin.class);
        commandManager = new PaperCommandManager(plugin);

        Map<Class<?>, Object> services = servicesLocator.getServices();

        for (Map.Entry<Class<?>, Object> entry : services.entrySet()) {
            try {
                commandManager.registerDependency(entry.getKey(), entry.getValue());
            } catch (IllegalStateException exception) {
                if(!exception.getMessage().startsWith("There is already an instance of ")) {
                    throw exception;
                }
            }
        }
    }

    @Override
    public void disable() throws Exception {

    }
}
