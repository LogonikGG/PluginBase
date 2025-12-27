package ru.logonik.pluginBase.servicelocator;

import ru.logonik.pluginBase.util.LogoUtils;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {
    void accept(T t) throws E;

    default Consumer<T> unchecked() {
        return t -> {
            try {
                accept(t);
            } catch (Throwable e) {
                LogoUtils.sneakyThrow(e);
            }
        };
    }
}
