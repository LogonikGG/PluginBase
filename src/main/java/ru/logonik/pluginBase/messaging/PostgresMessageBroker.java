package ru.logonik.pluginBase.messaging;

import org.postgresql.PGConnection;
import org.postgresql.PGNotification;
import ru.logonik.pluginBase.Logger;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class PostgresMessageBroker implements MessageBroker {

    private static final int RECEIVE_TIMEOUT_MILLIS = 1000;
    private static final long RECONNECT_DELAY_MILLIS = 5000;

    private final String url;
    private final String username;
    private final String password;

    private final Map<String, List<Consumer<String>>> listeners =
            new ConcurrentHashMap<>();

    private volatile boolean running;
    private Thread listenerThread;

    public PostgresMessageBroker(
            String url,
            String username,
            String password
    ) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    @Override
    public void subscribe(String channel, Consumer<String> listener) {
        if (running) {
            throw new IllegalStateException(
                    "Cannot subscribe after broker start"
            );
        }
        listeners.computeIfAbsent(
                channel,
                ignored -> new CopyOnWriteArrayList<>()
        ).add(listener);
    }

    @Override
    public void publish(String channel, String message) {
        try (Connection connection = getConnection()) {

            try (PreparedStatement statement =
                         connection.prepareStatement(
                                 "SELECT pg_notify(?, ?)"
                         )) {

                statement.setString(1, channel);
                statement.setString(2, message);
                statement.execute();
            }

        } catch (SQLException e) {
            Logger.error(
                    "Failed to publish message to channel: " + channel,
                    e
            );
        }
    }

    @Override
    public void start() {
        if (running) {
            return;
        }

        running = true;

        listenerThread = new Thread(
                this::runListenerLoop,
                "postgres-message-broker"
        );

        listenerThread.setDaemon(true);
        listenerThread.start();
        Logger.info("Postgres MessageBroker started thread: " + listenerThread.getName());
    }

    private void runListenerLoop() {

        while (running) {

            try (Connection connection = getConnection()) {

                connection.setAutoCommit(true);

                listenToAllChannels(connection);

                PGConnection pgConnection =
                        connection.unwrap(PGConnection.class);

                while (running &&
                        !Thread.currentThread().isInterrupted()) {

                    PGNotification[] notifications =
                            pgConnection.getNotifications(
                                    RECEIVE_TIMEOUT_MILLIS
                            );

                    if (notifications == null) {
                        continue;
                    }

                    for (PGNotification notification : notifications) {
                        handleNotification(notification);
                    }
                }

            } catch (Exception e) {

                if (!running) {
                    break;
                }

                Logger.error(
                        "Postgres listener disconnected. Reconnecting...",
                        e
                );

                sleepSilently(RECONNECT_DELAY_MILLIS);
            }
        }
    }

    private void listenToAllChannels(Connection connection)
            throws SQLException {

        try (Statement statement = connection.createStatement()) {

            for (String channel : listeners.keySet()) {

                statement.execute(
                        "LISTEN \"" +
                                channel.replace("\"", "\"\"") +
                                "\""
                );

                Logger.info(
                        "Listening postgres channel: " + channel
                );
            }
        }
    }

    private void handleNotification(PGNotification notification) {

        List<Consumer<String>> handlers =
                listeners.get(notification.getName());

        if (handlers == null) {
            return;
        }

        String message = notification.getParameter();

        for (Consumer<String> handler : handlers) {

            try {
                handler.accept(message);
            } catch (Exception e) {
                Logger.error(
                        "Failed to process postgres message",
                        e
                );
            }
        }
    }

    @Override
    public void close() {

        running = false;

        if (listenerThread != null) {

            listenerThread.interrupt();

            try {
                listenerThread.join(2000);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                url,
                username,
                password
        );
    }

    private void sleepSilently(long millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}