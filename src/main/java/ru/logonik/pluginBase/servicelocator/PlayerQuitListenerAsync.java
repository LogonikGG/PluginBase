package ru.logonik.pluginBase.servicelocator;

import org.bukkit.entity.Player;

public interface PlayerQuitListenerAsync {
    void onPlayerQuitAsync(Player player) throws Exception;
}
