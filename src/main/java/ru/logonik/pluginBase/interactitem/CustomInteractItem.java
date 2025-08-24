package ru.logonik.pluginBase.interactitem;

import org.bukkit.inventory.ItemStack;
import ru.logonik.pluginBase.nbt.ItemStackNbt;

public abstract class CustomInteractItem {
    private final String itemValue;
    
    public CustomInteractItem(String itemValue) {
        this.itemValue = itemValue;
    }
    
    public String getItemValue() {
        return itemValue;
    }
    
    protected abstract ItemStack createItem();
    
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