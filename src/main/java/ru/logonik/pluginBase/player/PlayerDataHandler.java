package ru.logonik.pluginBase.player;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerDataHandler {
    private final HashMap<UUID, PlayerData> data = new HashMap<>();

    public PlayerData savePlayerData(Player player) {
        Objects.requireNonNull(player, "player must be not null");
        PlayerData playerData = new PlayerData(player);
        data.put(player.getUniqueId(), playerData);
        return playerData;
    }

    public void loadIfExistPlayerData(Player player) {
        PlayerData playerData = data.get(player.getUniqueId());
        playerData.loadData(player);
    }

    public void loadIfExistAndRemovePlayerData(Player player) {
        PlayerData playerData = data.remove(player.getUniqueId());
        if (playerData == null) return;
        playerData.loadData(player);
    }

    public PlayerData removePlayerData(UUID playerId) {
        return data.remove(playerId);
    }

    public PlayerData getAndRemovePlayerDate(UUID playerId) {
        return data.remove(playerId);
    }

    public PlayerData getPlayerDate(UUID playerId) {
        return data.get(playerId);
    }

    public PlayerData putPlayerDate(UUID playerId, PlayerData playerData) {
        return data.put(playerId, playerData);
    }

    public boolean containPlayerData(UUID playerId) {
        return data.containsKey(playerId);
    }

    public void clear() {
        data.clear();
    }
}