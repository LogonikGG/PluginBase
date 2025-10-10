package ru.logonik.pluginBase.servicelocator;

import org.bukkit.entity.Player;

public interface PlayerAvailableListenerAsync {
    void onPlayerAvailableAsync(Player player) throws Exception;
}
