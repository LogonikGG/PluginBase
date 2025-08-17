package ru.logonik.pluginBase.gson;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackArrayTypeAdapter implements JsonSerializer<ItemStack[]>, JsonDeserializer<ItemStack[]> {

    private final ItemStackTypeAdapter singleAdapter;

    private boolean allowNullArray = false;

    public ItemStackArrayTypeAdapter(boolean allowNullArray, boolean allowNullSingle) {
        this.allowNullArray = allowNullArray;
        this.singleAdapter = new ItemStackTypeAdapter(allowNullSingle);
    }

    public ItemStackArrayTypeAdapter(boolean allowNullSingle) {
        this.singleAdapter = new ItemStackTypeAdapter(allowNullSingle);
    }

    @Override
    public JsonElement serialize(ItemStack[] items, Type type, JsonSerializationContext context) {
        if (allowNullArray && items == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (ItemStack item : items) {
            array.add(singleAdapter.serialize(item, ItemStack.class, context));
        }
        return array;
    }

    @Override
    public ItemStack[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (allowNullArray && json.isJsonNull()) return null;
        JsonArray array = json.getAsJsonArray();
        ItemStack[] items = new ItemStack[array.size()];
        for (int i = 0; i < array.size(); i++) {
            items[i] = singleAdapter.deserialize(array.get(i), ItemStack.class, context);
        }
        return items;
    }
}
