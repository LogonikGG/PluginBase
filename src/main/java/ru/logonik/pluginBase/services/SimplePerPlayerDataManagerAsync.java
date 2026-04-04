package ru.logonik.pluginBase.services;

import org.bukkit.entity.Player;
import ru.logonik.pluginBase.execptions.SaveLoadException;
import ru.logonik.pluginBase.saveload.LoadSaver;
import ru.logonik.pluginBase.servicelocator.PlayerAvailableListenerAsync;
import ru.logonik.pluginBase.servicelocator.PlayerQuitListenerAsync;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SimplePerPlayerDataManagerAsync<V> implements PlayerAvailableListenerAsync, PlayerQuitListenerAsync {

    private final Map<UUID, V> playerData = new ConcurrentHashMap<>();
    private final LoadSaver<UUID, V> saver;

    public SimplePerPlayerDataManagerAsync(LoadSaver<UUID, V> saver) {
        this.saver = saver;
    }

    @Override
    public void onPlayerAvailableAsync(Player player) throws Exception {
        UUID uuid = player.getUniqueId();
        V loadedData = saver.load(uuid);
        if (loadedData != null) {
            playerData.put(uuid, loadedData);
        }
    }

    @Override
    public void onPlayerQuitAsync(Player player) throws Exception {
        UUID uuid = player.getUniqueId();
        V data = playerData.get(uuid);
        if (data != null) {
            saver.save(uuid, data);
            playerData.remove(uuid);
        }
    }

    public V get(Player player) {
        return playerData.get(player.getUniqueId());
    }

    public V getOrDefault(Player player, V defaultValue) {
        return playerData.getOrDefault(player.getUniqueId(), defaultValue);
    }

    public void put(Player player, V data) throws SaveLoadException {
        UUID uuid = player.getUniqueId();
        playerData.put(uuid, data);
        saver.save(uuid, data);
    }

    public V remove(Player player) throws SaveLoadException {
        UUID uuid = player.getUniqueId();
        V removed = playerData.remove(uuid);
        saver.delete(uuid);
        return removed;
    }

    public boolean hasData(Player player) {
        return playerData.containsKey(player.getUniqueId());
    }

    public void clear() {
        playerData.clear();
    }

    public Map<UUID, V> getAll() {
        return playerData;
    }
}