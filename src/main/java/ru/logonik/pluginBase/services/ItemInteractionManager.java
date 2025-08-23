package ru.logonik.pluginBase.services;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import ru.logonik.pluginBase.nbt.ItemStackNbt;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Сервис управления "подписками" на взаимодействие предметов (ПКМ по энтити).
 * Идея: регистрируем обработчик -> получаем UUID -> присваиваем UUID предметам через NBT.
 * При клике по энтити читаем NBT и вызываем соответствующий обработчик.
 */
public class ItemInteractionManager implements Listener {

    private static final String KEY = "item-interaction-manager";
    private final Map<UUID, HandlerWrapper> handlers = new ConcurrentHashMap<>();

    private static class HandlerWrapper {
        final Consumer<PlayerInteractEntityEvent> handler;
        // remaining == -1 => неограниченно; иначе количество оставшихся вызовов
        final AtomicInteger remaining;

        HandlerWrapper(Consumer<PlayerInteractEntityEvent> handler, int remaining) {
            this.handler = handler;
            this.remaining = new AtomicInteger(remaining);
        }
    }

    // -----------------------
    // Регистрация/назначение
    // -----------------------

    /**
     * Зарегистрировать обработчик и получить UUID. Обработчик живёт пока не вызовут unregister(id) или пока
     * не исчерпается количество вызовов (если задано).
     *
     * @param handler Обработчик события.
     * @return UUID обработчика.
     */
    public UUID registerHandler(Consumer<PlayerInteractEntityEvent> handler) {
        return registerHandler(handler, -1);
    }

    /**
     * Зарегистрировать обработчик с ограничением по числу срабатываний.
     * remainingCalls == -1 => неограниченно.
     */
    public UUID registerHandler(Consumer<PlayerInteractEntityEvent> handler, int remainingCalls) {
        UUID id = UUID.randomUUID();
        handlers.put(id, new HandlerWrapper(handler, remainingCalls < 0 ? -1 : remainingCalls));
        return id;
    }

    /**
     * Зарегистрировать одноразовый обработчик (удалится после первого срабатывания).
     */
    public UUID registerOneShotHandler(Consumer<PlayerInteractEntityEvent> handler) {
        return registerHandler(handler, 1);
    }

    /**
     * Назначить существующий UUID на предмет (вставит NBT).
     * Возвращает тот же предмет (ItemStack может быть мутируемым, но утилита возвращает ItemStack на всякий случай).
     */
    public ItemStack assignHandlerToItem(UUID id, ItemStack item) {
        if (id == null || item == null) return item;
        return ItemStackNbt.setString(item, KEY, id.toString());
    }

    /**
     * Удобный метод: зарегистрировать новый обработчик и тут же назначить его предмету.
     * Возвращает UUID зарегистрированного обработчика.
     */
    public UUID createHandlerAndAssignToItem(ItemStack item, Consumer<PlayerInteractEntityEvent> handler) {
        UUID id = registerHandler(handler);
        assignHandlerToItem(id, item);
        return id;
    }

    // -----------------------
    // Отписка / чистка
    // -----------------------

    /**
     * Удаляет обработчик по UUID (не убирает тег с предметов).
     */
    public void unregister(UUID id) {
        if (id == null) return;
        handlers.remove(id);
    }

    /**
     * Удаляет обработчик и очищает NBT-метку у переданного предмета.
     * Это полезно, когда у тебя есть конкретный ItemStack, с которого нужно снять подписку.
     */
    public void unregisterAndClearFromItem(UUID id, ItemStack item) {
        unregister(id);
        if (item != null) ItemStackNbt.removeTag(item, KEY);
    }

    /**
     * Убрать NBT-ключ с предмета (не меняет зарегистрированные обработчики).
     */
    public ItemStack removeTagFromItem(ItemStack item) {
        if (item == null) return null;
        return ItemStackNbt.removeTag(item, KEY);
    }

    /**
     * Проверяет, есть ли в системе обработчик с таким UUID.
     */
    public boolean hasHandler(UUID id) {
        return id != null && handlers.containsKey(id);
    }

    // -----------------------
    // Слушатель событий
    // -----------------------

    @EventHandler(ignoreCancelled = true)
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        if (player == null) return;

        // проверяем main hand и off hand — удобно поддерживать оба
        ItemStack item = player.getInventory().getItemInMainHand();
        if (isValidAndHasKey(item)) {
            if (tryHandle(item, event)) return;
        }

        ItemStack off = player.getInventory().getItemInOffHand();
        if (isValidAndHasKey(off)) {
            tryHandle(off, event);
        }
    }

    // Проверка валидности предмета
    private boolean isValidAndHasKey(ItemStack item) {
        if (item == null) return false;
        if (item.getType() == null) return false;
        // Material.AIR check внутри getType()==AIR обычно
        if (item.getType().isAir()) return false;
        return ItemStackNbt.hasKey(item, KEY);
    }

    // Попытаться обработать событие по предмету. Вернёт true если обработчик найден и выполнен.
    private boolean tryHandle(ItemStack item, PlayerInteractEntityEvent event) {
        String raw = ItemStackNbt.getString(item, KEY);
        if (raw == null) return false;

        UUID id;
        try {
            id = UUID.fromString(raw);
        } catch (IllegalArgumentException e) {
            return false;
        }

        HandlerWrapper wrapper = handlers.get(id);
        if (wrapper == null) return false;

        // Вызов обработчика
        try {
            wrapper.handler.accept(event);
        } catch (Exception ex) {
            // защитный catch чтобы один упавший обработчик не ломал всё
            ex.printStackTrace();
        }

        // Обработка счётчика срабатываний (если задан)
        if (wrapper.remaining.get() > 0) {
            int left = wrapper.remaining.decrementAndGet();
            if (left <= 0) {
                // удаляем обработчик, он исчерпан
                handlers.remove(id);
            }
        }
        // если remaining == -1 => бесконечно

        return true;
    }
}
