package ru.logonik.pluginBase.servicelocator;

import ru.logonik.pluginBase.Logger;
import ru.logonik.pluginBase.Scheduler;

import java.util.*;
import java.util.function.Consumer;

public class ServicesLocator {
    protected final Map<Class<?>, Object> services = new HashMap<>();
    protected final Logger logger;

    public ServicesLocator(Logger logger) {
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

    public Map<Class<?>, Object> getServices() {
        return Collections.unmodifiableMap(services);
    }

    public <T> void safeConsumeForInstanceof(Class<T> clazz, Consumer<T> consumer) {
        for (Object value : services.values()) {
            if (clazz.isInstance(value)) {
                T t = clazz.cast(value);
                try {
                    consumer.accept(t);
                } catch (Exception e) {
                    logger.error("Error while consume", e);
                }
            }
        }
    }
}
