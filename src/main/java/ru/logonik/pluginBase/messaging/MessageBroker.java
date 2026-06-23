package ru.logonik.pluginBase.messaging;

import java.util.function.Consumer;

public interface MessageBroker extends AutoCloseable {

    void subscribe(String channel, Consumer<String> listener);

    void publish(String channel, String message);

    void start();
}