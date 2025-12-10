package ru.logonik.pluginBase.player;

import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class PlayerResetUtils {
    
    public static void reset(Player player) {
        clearInventory(player);
        ensurePlayerIsAlive(player);
        resetHealthAndHunger(player);
        resetExperience(player);
        resetBurningAndEffects(player);
        setInvulnerabilityBriefly(player);
    }
    
    public static void clearPlayerVisualIdentity(Player player) {
        player.setDisplayName(null);
        player.setPlayerListName(null);
    }

    public static void clearInventory(Player player) {
        player.getInventory().clear();
    }

    public static void healPlayer(Player player) {
        ensurePlayerIsAlive(player);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFireTicks(0);
    }

    public static void ensurePlayerIsAlive(Player player) {
        if (player.isDead()) {
            player.spigot().respawn();
        }
    }

    public static void resetHealthAndHunger(Player player) {
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(5.0F);
    }

    public static void resetExperience(Player player) {
        player.setExp(0.0F);
        player.setLevel(0);
    }

    public static void resetBurningAndEffects(Player player) {
        player.setFireTicks(0);
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public static void setInvulnerabilityBriefly(Player player) {
        player.setNoDamageTicks(80);
    }
}