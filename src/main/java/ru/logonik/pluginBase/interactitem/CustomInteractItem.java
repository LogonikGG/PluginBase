package ru.logonik.pluginBase.interactitem;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.logonik.pluginBase.nbt.ItemStackNbt;

public abstract class CustomInteractItem {
    protected final String itemValue;
    
    public CustomInteractItem(String itemValue) {
        this.itemValue = itemValue;
    }
    
    public String getItemValue() {
        return itemValue;
    }
    
    public abstract ItemStack createItem();

    public ItemStack createItem(Object ctx) {
        return createItem();
    }

    public void onAction(PlayerInteractEvent e) {

    }

    public void onAction(PlayerInteractEntityEvent e) {

    }

    public void onAction(Player player) {

    }

    public boolean isThisItem(ItemStack item) {
        return item != null && ItemStackNbt.hasKey(item, itemValue);
    }

    public void cleanup() {

    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomInteractItem that = (CustomInteractItem) o;
        return itemValue.equals(that.itemValue);
    }
    
    @Override
    public int hashCode() {
        return itemValue.hashCode();
    }
}