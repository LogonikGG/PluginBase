package ru.logonik.pluginBase.util;

import dev.triumphteam.gui.components.GuiAction;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class GuiItemUtil {

    /**
     * Устанавливает новое имя для предмета GuiItem.
     */
    public static void setName(GuiItem guiItem, Component name) {
        ItemStack stack = guiItem.getItemStack();
        stack.editMeta(meta -> meta.displayName(name));
    }

    /**
     * Устанавливает новый lore.
     */
    public static void setLore(GuiItem guiItem, List<Component> lore) {
        ItemStack stack = guiItem.getItemStack();
        stack.editMeta(meta -> meta.lore(lore));
    }

    /**
     * Полностью заменяет предмет внутри GuiItem.
     */
    public static void setItem(GuiItem guiItem, ItemStack newItem) {
        guiItem.setItemStack(newItem);
    }

    /**
     * Назначает новое действие.
     */
    public static void setAction(GuiItem guiItem, GuiAction<InventoryClickEvent> action) {
        guiItem.setAction(action);
    }

    /**
     * Обновляет и имя, и lore.
     */
    public static void setNameAndLore(GuiItem guiItem, Component name, List<Component> lore) {
        ItemStack stack = guiItem.getItemStack();
        stack.editMeta(meta -> {
            meta.displayName(name);
            meta.lore(lore);
        });
    }

    /**
     * Меняет только имя и действие.
     */
    public static void setNameAndAction(GuiItem guiItem, Component name, GuiAction<InventoryClickEvent> action) {
        setName(guiItem, name);
        setAction(guiItem, action);
    }

    /**
     * Меняет только lore и действие.
     */
    public static void setLoreAndAction(GuiItem guiItem, List<Component> lore, GuiAction<InventoryClickEvent> action) {
        setLore(guiItem, lore);
        setAction(guiItem, action);
    }

    /**
     * Меняет всё: имя, lore и действие.
     */
    public static void setAll(GuiItem guiItem, Component name, List<Component> lore, GuiAction<InventoryClickEvent> action) {
        setNameAndLore(guiItem, name, lore);
        setAction(guiItem, action);
    }

    /**
     * Добавляет строчку к текущему лору.
     */
    public static void appendLore(GuiItem guiItem, Component extraLine) {
        ItemStack stack = guiItem.getItemStack();
        stack.editMeta(meta -> {
            List<Component> lore = meta.lore();
            if (lore == null) lore = new java.util.ArrayList<>();
            lore.add(extraLine);
            meta.lore(lore);
        });
    }

    /**
     * Заменяет предмет + действие (например, для быстрой полной замены кнопки).
     */
    public static void replaceItemAndAction(GuiItem guiItem, ItemStack newItem, GuiAction<InventoryClickEvent> action) {
        setItem(guiItem, newItem);
        setAction(guiItem, action);
    }
}
