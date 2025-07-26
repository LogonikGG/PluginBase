package ru.logonik.pluginBase.servicelocator;

import org.bukkit.entity.Player;

public interface PlayerAvailableListener {
    void onPlayerAvailableAsync(Player player) throws Exception;
}
