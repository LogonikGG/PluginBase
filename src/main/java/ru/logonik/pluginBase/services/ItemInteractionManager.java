package ru.logonik.pluginBase.services;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import ru.logonik.pluginBase.nbt.ItemStackNbt;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class ItemInteractionManager implements Listener {

    private static final String KEY = "item-interaction-manager";
    private final Map<UUID, Consumer<PlayerInteractEntityEvent>> handlers = new ConcurrentHashMap<>();

    public UUID registerItem(ItemStack item, Consumer<PlayerInteractEntityEvent> action) {
        UUID id = UUID.randomUUID();

        ItemStackNbt.setString(item, KEY, id.toString());
        handlers.put(id, action);

        return id;
    }

    public void unregister(UUID id) {
        handlers.remove(id);
    }

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() == Material.AIR) return;

        if (!ItemStackNbt.hasKey(item, KEY)) return;

        String raw = ItemStackNbt.getString(item, KEY);
        if (raw == null) return;

        try {
            UUID id = UUID.fromString(raw);
            Consumer<PlayerInteractEntityEvent> action = handlers.get(id);
            if (action != null) {
                action.accept(event);
            }
        } catch (IllegalArgumentException ignored) {
        }
    }
}
