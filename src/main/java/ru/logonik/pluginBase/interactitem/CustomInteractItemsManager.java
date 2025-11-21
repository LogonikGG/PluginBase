package ru.logonik.pluginBase.interactitem;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import ru.logonik.pluginBase.nbt.ItemStackNbt;

import java.util.HashMap;
import java.util.List;

public class CustomInteractItemsManager<Item extends CustomInteractItem> implements Listener {
    protected final String itemKey;

    protected final HashMap<String, Item> registeredItems = new HashMap<>();

    public CustomInteractItemsManager(String itemKey) {
        this.itemKey = itemKey;
    }

    public void registerItem(Item item) {
        if (registeredItems.containsKey(item.getItemValue())) {
            throw new IllegalArgumentException("Item with key '" + item.getItemValue() + "' is already registered");
        }
        registeredItems.put(item.getItemValue(), item);
    }

    public boolean unregisterItem(Item item) {
        boolean removed = registeredItems.remove(item.getItemValue()) != null;
        if (removed) {
            item.cleanup();
        }
        return removed;
    }

    public boolean unregisterItem(String itemValue) {
        Item item = registeredItems.remove(itemValue);
        if (item != null) {
            item.cleanup();
            return true;
        }
        return false;
    }

    public Item getItem(String itemValue) {
        return registeredItems.get(itemValue);
    }

    public List<Item> getRegisteredItems() {
        return List.copyOf(registeredItems.values());
    }

    public boolean isItemRegistered(String itemValue) {
        return registeredItems.containsKey(itemValue);
    }

    public ItemStack createItem(String itemValue) {
        Item item = getItem(itemValue);
        if(item == null) return null;
        ItemStack itemStack = item.createItem();
        return ItemStackNbt.setString(itemStack, itemKey, item.getItemValue());
    }

    public boolean isAnyCustomItem(ItemStack itemStack) {
        return findItemByStack(itemStack) != null;
    }

    public String getItemValue(ItemStack itemStack) {
        Item interactItem = findItemByStack(itemStack);
        return interactItem == null ? null : interactItem.getItemValue();
    }

    public Item findItemByStack(ItemStack itemStack) {
        if(itemStack == null) return null;
        String value = ItemStackNbt.getString(itemStack, itemKey);
        if(value == null) return null;
        return registeredItems.get(value);
    }

    public void cleanupAll() {
        for (Item item : registeredItems.values()) {
            item.cleanup();
        }
        registeredItems.clear();
    }

    public int getRegisteredItemsCount() {
        return registeredItems.size();
    }
}