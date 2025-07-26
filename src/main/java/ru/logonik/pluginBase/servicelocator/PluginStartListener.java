package ru.logonik.pluginBase.servicelocator;

public interface PluginStartListener {
    void start(ServicesLocator servicesLocator) throws Exception;
}
