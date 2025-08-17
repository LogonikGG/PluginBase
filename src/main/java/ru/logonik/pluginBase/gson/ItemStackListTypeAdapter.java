package ru.logonik.pluginBase.gson;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemStackListTypeAdapter implements JsonSerializer<List<ItemStack>>, JsonDeserializer<List<ItemStack>> {

    private final ItemStackTypeAdapter singleAdapter = new ItemStackTypeAdapter();

    @Override
    public JsonElement serialize(List<ItemStack> items, Type type, JsonSerializationContext context) {
        JsonArray array = new JsonArray();
        for (ItemStack item : items) {
            array.add(singleAdapter.serialize(item, ItemStack.class, context));
        }
        return array;
    }

    @Override
    public List<ItemStack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonArray array = json.getAsJsonArray();
        List<ItemStack> items = new ArrayList<>();
        for (JsonElement el : array) {
            items.add(singleAdapter.deserialize(el, ItemStack.class, context));
        }
        return items;
    }
}
