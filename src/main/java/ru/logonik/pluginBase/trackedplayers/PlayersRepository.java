package ru.logonik.pluginBase.trackedplayers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.stream.Collectors;

public class PlayersRepository implements Listener {

    protected final Map<UUID, TrackedPlayer> trackedPlayers = new HashMap<>();

    public void addTarget(TrackedPlayer trackedPlayer) {
        trackedPlayers.putIfAbsent(trackedPlayer.getUuid(), trackedPlayer);
        trackedPlayer.updatePlayer();
    }

    public void addTarget(Player player) {
        trackedPlayers.putIfAbsent(player.getUniqueId(), new TrackedPlayer(player));
    }

    public void removeTarget(UUID player) {
        trackedPlayers.remove(player);
    }

    public boolean contains(UUID player) {
        return trackedPlayers.containsKey(player);
    }

    public List<TrackedPlayer> getAll() {
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
        for (TrackedPlayer trackedPlayer : trackedPlayers.values()) {
            String name = trackedPlayer.getName() + "@" + trackedPlayer.getUuid();
            result.add(name);
        }
        return result;
    }

    public TrackedPlayer getByUnique(String unique) {
        String[] split = unique.split("@", 2);
        String uuidString = split.length == 2 ? split[1] : split[0];
        try {
            return trackedPlayers.get(UUID.fromString(uuidString));
        } catch (IllegalArgumentException ignore) {
            return null;
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        TrackedPlayer trackedPlayer = trackedPlayers.get(e.getPlayer().getUniqueId());
        if (trackedPlayer != null) {
            trackedPlayer.setPlayer(e.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        TrackedPlayer trackedPlayer = trackedPlayers.get(e.getPlayer().getUniqueId());
        if (trackedPlayer != null) {
            trackedPlayer.setPlayer(null);
        }
    }
}
