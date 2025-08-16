package ru.logonik.pluginBase.player;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import ru.logonik.pluginBase.BukkitUtil;

import java.util.Collection;

public class CharacterPlayerData {
    private double health;
    private int foodLevel;
    private float saturation;
    private int level;
    private float xp;

    private final ItemStack[] armorContent;
    private final ItemStack[] inventoryContent;
    private final Collection<PotionEffect> potionEffects;


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