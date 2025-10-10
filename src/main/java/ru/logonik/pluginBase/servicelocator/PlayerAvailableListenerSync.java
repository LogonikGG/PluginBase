package ru.logonik.pluginBase.servicelocator;

import org.bukkit.entity.Player;

public interface PlayerAvailableListenerSync {
    void onPlayerAvailableSync(Player player) throws Exception;
}
