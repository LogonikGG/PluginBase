package ru.logonik.pluginBase.servicelocator;

import org.bukkit.entity.Player;

public interface PlayerQuitListenerSync {
    void onPlayerQuitSync(Player player) throws Exception;
}
