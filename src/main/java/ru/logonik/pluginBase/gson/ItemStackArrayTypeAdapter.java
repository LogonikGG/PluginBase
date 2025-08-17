package ru.logonik.pluginBase.gson;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;

public class ItemStackArrayTypeAdapter implements JsonSerializer<ItemStack[]>, JsonDeserializer<ItemStack[]> {

    private final ItemStackTypeAdapter singleAdapter = new ItemStackTypeAdapter();

    @Override
    public JsonElement serialize(ItemStack[] items, Type type, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (ItemStack item : items) {
            array.add(singleAdapter.serialize(item, ItemStack.class, context));
        }
        return array;
    }

    @Override
    public ItemStack[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();
        ItemStack[] items = new ItemStack[array.size()];
        for (int i = 0; i < array.size(); i++) {
            items[i] = singleAdapter.deserialize(array.get(i), ItemStack.class, context);
        }
        return items;
    }
}
