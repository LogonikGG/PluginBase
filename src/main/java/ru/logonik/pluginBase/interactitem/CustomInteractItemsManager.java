package ru.logonik.pluginBase.interactitem;

import org.bukkit.inventory.ItemStack;
import ru.logonik.pluginBase.nbt.ItemStackNbt;

import java.util.HashMap;
import java.util.List;

public class CustomInteractItemsManager {
    protected final String itemKey;

    protected final HashMap<String, CustomInteractItem> registeredItems = new HashMap<>();

    public CustomInteractItemsManager(String itemKey) {
        this.itemKey = itemKey;
    }

    public void registerItem(CustomInteractItem item) {
        if (registeredItems.containsKey(item.getItemValue())) {
            throw new IllegalArgumentException("Item with key '" + item.getItemValue() + "' is already registered");
        }
        registeredItems.put(item.getItemValue(), item);
    }

    public boolean unregisterItem(CustomInteractItem item) {
        boolean removed = registeredItems.remove(item.getItemValue()) != null;
        if (removed) {
            item.cleanup();
        }
        return removed;
    }

    public boolean unregisterItem(String itemValue) {
        CustomInteractItem item = registeredItems.remove(itemValue);
        if (item != null) {
            item.cleanup();
            return true;
        }
        return false;
    }

    public CustomInteractItem getItem(String itemValue) {
        return registeredItems.get(itemValue);
    }

    public List<CustomInteractItem> getRegisteredItems() {
        return List.copyOf(registeredItems.values());
    }

    public boolean isItemRegistered(String itemValue) {
        return registeredItems.containsKey(itemValue);
    }

    public ItemStack createItem(String itemKey) {
        CustomInteractItem item = getItem(itemKey);
        if(item == null) return null;
        ItemStack itemStack = item.createItem();
        return ItemStackNbt.setString(itemStack, itemKey, item.getItemValue());
    }

    public boolean isAnyCustomItem(ItemStack itemStack) {
        return findItemByStack(itemStack) != null;
    }

    public CustomInteractItem findItemByStack(ItemStack itemStack) {
        String value = ItemStackNbt.getString(itemStack, itemKey);
        if(value == null) return null;
        return registeredItems.get(value);
    }

    public String getItemValue(ItemStack itemStack) {
        CustomInteractItem interactItem = findItemByStack(itemStack);
        return interactItem == null ? null : interactItem.getItemValue();
    }

    public void cleanupAll() {
        for (CustomInteractItem item : registeredItems.values()) {
            item.cleanup();
        }
        registeredItems.clear();
    }

    public int getRegisteredItemsCount() {
        return registeredItems.size();
    }
}