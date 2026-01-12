package ru.logonik.pluginBase.trackedplayers;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class TrackedPlayer {
    private final UUID uuid;
    private final String name;
    private transient Player player; // null, если оффлайн

    public TrackedPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.name = player.getName();
        this.player = player;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isOnline() {
        return player != null;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getUniqueKey() {
        return name + "#" + uuid;
    }

    public static UUID parseUuidFromUniqueKey(String unique) {
        String uuidString = unique.substring(unique.lastIndexOf("#") + 1);
        return UUID.fromString(uuidString);
    }

    public void updatePlayer() {
        this.player = Bukkit.getPlayer(uuid);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        TrackedPlayer that = (TrackedPlayer) object;
        return Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(uuid);
    }
}
