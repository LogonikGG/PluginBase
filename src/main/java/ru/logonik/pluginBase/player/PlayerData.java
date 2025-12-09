package ru.logonik.pluginBase.player;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerData extends CharacterPlayerData {

    private Location lastLocation;
    private GameMode gameMode;

    public PlayerData(Player player) {
        super(player);
        lastLocation = player.getLocation().clone();
        gameMode = player.getGameMode();
    }

    public void loadData(Player player) {
        super.loadData(player);
        player.teleport(lastLocation);
        player.setGameMode(gameMode);
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }
}