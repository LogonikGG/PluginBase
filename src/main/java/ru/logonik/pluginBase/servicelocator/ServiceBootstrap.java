package ru.logonik.pluginBase.servicelocator;

public interface ServiceBootstrap {
    void onCreate(ServicesLocator servicesLocator);
    void onBuildLinks(ServicesLocator servicesLocator);
    void onInit(ServicesLocator servicesLocator);
}
