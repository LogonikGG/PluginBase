package ru.logonik.pluginBase.services;

import org.bukkit.entity.Player;
import ru.logonik.pluginBase.execptions.SaveLoadException;
import ru.logonik.pluginBase.saveload.LoadSaver;
import ru.logonik.pluginBase.servicelocator.PlayerAvailableListener;
import ru.logonik.pluginBase.servicelocator.PlayerQuitListenerAsync;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PerPlayerDataManager<D, M> implements PlayerAvailableListener, PlayerQuitListenerAsync {

    private final Map<UUID, D> playerData = new ConcurrentHashMap<>();
    private final Function<Player, D> dataFactory;
    private final LoadSaver<UUID, M> saver;
    private final Function<D, M> toModel;
    private final Function<M, D> fromModel;

    public PerPlayerDataManager(
            Function<Player, D> dataFactory,
            LoadSaver<UUID, M> saver,
            Function<D, M> toModel,
            Function<M, D> fromModel
    ) {
        this.dataFactory = dataFactory;
        this.saver = saver;
        this.toModel = toModel;
        this.fromModel = fromModel;
    }

    @Override
    public void onPlayerAvailableAsync(Player player) throws Exception {
        UUID uuid = player.getUniqueId();
        M loadedModel = saver.load(uuid);
        if (loadedModel != null) {
            D data = fromModel.apply(loadedModel);
            playerData.put(uuid, data);
        }
    }

    @Override
    public void onPlayerQuitAsync(Player player) throws Exception {
        UUID uuid = player.getUniqueId();
        D data = playerData.get(uuid);
        if (data != null) {
            M model = toModel.apply(data);
            saver.save(uuid, model);
            playerData.remove(uuid);
        }
    }

    public D getOrCompute(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), uuid -> dataFactory.apply(player));
    }

    public D getOrDefault(Player player) {
        return playerData.getOrDefault(player.getUniqueId(), dataFactory.apply(player));
    }

    public D get(Player player) {
        return playerData.computeIfAbsent(player.getUniqueId(), uuid -> dataFactory.apply(player));
    }

    public D remove(Player player) throws SaveLoadException {
        D removed = playerData.remove(player.getUniqueId());
        saver.delete(player.getUniqueId());
        return removed;
    }

    public boolean hasCash(Player player) {
        return playerData.containsKey(player.getUniqueId());
    }

    public void clear() {
        playerData.clear();
    }

    public Map<UUID, D> getAll() {
        return playerData;
    }
}
