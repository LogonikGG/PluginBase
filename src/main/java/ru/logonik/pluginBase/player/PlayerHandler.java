package ru.logonik.pluginBase.player;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class PlayerHandler {
    private final HashMap<UUID, PlayerData> data = new HashMap<>();

    public PlayerData savePlayerData(Player player) {
        PlayerData playerData = new PlayerData(player);
        data.put(player.getUniqueId(), playerData);
        return playerData;
    }

    public void loadPlayerData(Player player) {
        PlayerData playerData = data.get(player.getUniqueId());
        Objects.requireNonNull(playerData);
        playerData.loadData(player);
    }

    public void removePlayerData(Player player) {
        data.remove(player.getUniqueId());
    }

    public static void toDefaultPlayer(Player player) {
        player.getInventory().clear();
        if(player.isDead()) {
            player.spigot().respawn();
        }
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.setExp(0);
        player.setLevel(0);
        player.setFireTicks(0);
        player.setNoDamageTicks(80);
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }
    }

    public static void healPlayer(Player player) {
        if(player.isDead()) {
            player.spigot().respawn();
        }
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.setFireTicks(0);
    }

    public static void healAndEffectsClear(Player player) {
        healPlayer(player);
        for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
            player.removePotionEffect(activePotionEffect.getType());
        }
    }

    public static void toDefaultFullyPlayer(Player player) {
        toDefaultPlayer(player);
        player.setDisplayName(null);
        player.setPlayerListName(null);
    }

    public void loadAndRemovePlayerData(Player player) {
        PlayerData playerData = data.remove(player.getUniqueId());
        if (playerData == null) return;
        playerData.loadData(player);
    }
}