package ru.logonik.pluginBase.trackedplayers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.stream.Collectors;

public abstract class TrackedPlayersRepository<TP extends TrackedPlayer> implements Listener {

    protected final Map<UUID, TP> trackedPlayers = new HashMap<>();

    public void addTarget(TP trackedPlayer) {
        trackedPlayers.putIfAbsent(trackedPlayer.getUuid(), trackedPlayer);
        trackedPlayer.updatePlayer();
    }

    public void addTarget(Player player) {
        trackedPlayers.putIfAbsent(player.getUniqueId(), createTrackedPlayer(player));
    }

    protected abstract TP createTrackedPlayer(Player player);

    public void removeTarget(UUID player) {
        trackedPlayers.remove(player);
    }

    public boolean contains(UUID player) {
        return trackedPlayers.containsKey(player);
    }

    public List<TP> getAll() {
        return List.copyOf(trackedPlayers.values());
    }

    public List<Player> getOnlinePlayers() {
        return trackedPlayers.values().stream()
                .map(TrackedPlayer::getPlayer)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<String> getAllUnique() {
        ArrayList<String> result = new ArrayList<>();
        for (TP trackedPlayer : trackedPlayers.values()) {
            String name = trackedPlayer.getName() + "#" + trackedPlayer.getUuid();
            result.add(name);
        }
        return result;
    }

    public TP getByUnique(String unique) {
        String uuidString = unique.substring(unique.lastIndexOf("#") + 1);
        try {
            return trackedPlayers.get(UUID.fromString(uuidString));
        } catch (IllegalArgumentException ignore) {
            return null;
        }
    }

    public TP getByUUID(UUID uuid) {
        return trackedPlayers.get(uuid);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        TP trackedPlayer = trackedPlayers.get(e.getPlayer().getUniqueId());
        if (trackedPlayer != null) {
            trackedPlayer.setPlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        TP trackedPlayer = trackedPlayers.get(e.getPlayer().getUniqueId());
        if (trackedPlayer != null) {
            trackedPlayer.setPlayer(null);
        }
    }
}
