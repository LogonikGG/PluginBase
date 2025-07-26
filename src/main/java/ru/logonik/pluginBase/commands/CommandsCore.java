package ru.logonik.pluginBase.commands;

import co.aikar.commands.PaperCommandManager;
import org.bukkit.plugin.Plugin;
import ru.logonik.pluginBase.servicelocator.PluginStartListener;
import ru.logonik.pluginBase.servicelocator.ServicesLocator;

import java.util.Locale;

public class CommandsCore implements PluginStartListener {
    private PaperCommandManager commandManager;

    @Override
    public void start(ServicesLocator servicesLocator) throws Exception {
        this.commandManager = new PaperCommandManager(servicesLocator.getService(Plugin.class));
        registerCommands(servicesLocator);
    }

    protected void registerCommands(ServicesLocator servicesLocator) {
        Locale ruLocale = Locale.of("ru");
        commandManager.addSupportedLanguage(ruLocale);
        commandManager.getLocales().setDefaultLocale(ruLocale);
    }
}
