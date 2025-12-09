package ru.logonik.pluginBase.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.logonik.pluginBase.BukkitUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CharacterPlayerData {
    private double health;
    private int foodLevel;
    private float saturation;
    private int level;
    private float xp;

    private final ItemStack[] armorContent;
    private final ItemStack[] inventoryContent;
    private final Collection<PotionEffect> potionEffects;

    public CharacterPlayerData() {
        this.health = 20.0;
        this.foodLevel = 20;
        this.saturation = 5.0f;
        this.level = 0;
        this.xp = 0.0f;

        this.armorContent = new ItemStack[4];
        this.inventoryContent = new ItemStack[36];
        this.potionEffects = new ArrayList<>();
    }

    public CharacterPlayerData(Player player) {
        health = player.getHealth();
        foodLevel = player.getFoodLevel();
        saturation = player.getSaturation();
        level = player.getLevel();
        xp = player.getExp();
        potionEffects = BukkitUtil.deepCopyPotionEffects(player.getActivePotionEffects());
        armorContent = BukkitUtil.deepCopyItemStackArray(player.getInventory().getArmorContents());
        inventoryContent = BukkitUtil.deepCopyItemStackArray(player.getInventory().getContents());
    }

    public void loadData(Player player) {
        player.setHealth(health);
        player.setFoodLevel(foodLevel);
        player.setSaturation(saturation);
        player.setLevel(level);
        player.setExp(xp);
        Set<PotionEffectType> toRemoveEffects = player.getActivePotionEffects().stream().map(PotionEffect::getType).collect(Collectors.toSet());
        for (PotionEffectType toRemoveEffect : toRemoveEffects) {
            player.removePotionEffect(toRemoveEffect);
        }
        for (PotionEffect potionEffect : potionEffects) {
            player.addPotionEffect(potionEffect);
        }
        player.getInventory().setArmorContents(armorContent);
        player.getInventory().setContents(inventoryContent);
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public void setFoodLevel(int foodLevel) {
        this.foodLevel = foodLevel;
    }

    public float getSaturation() {
        return saturation;
    }

    public void setSaturation(float saturation) {
        this.saturation = saturation;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public float getXp() {
        return xp;
    }

    public void setXp(float xp) {
        this.xp = xp;
    }

    public ItemStack[] getArmorContent() {
        return armorContent;
    }

    public ItemStack[] getInventoryContent() {
        return inventoryContent;
    }
}