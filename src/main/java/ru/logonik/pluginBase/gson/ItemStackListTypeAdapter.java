package ru.logonik.pluginBase.gson;

import com.google.gson.*;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ItemStackListTypeAdapter implements JsonSerializer<List<ItemStack>>, JsonDeserializer<List<ItemStack>> {

    private final ItemStackTypeAdapter singleAdapter;

    private boolean allowNullList = false;

    public ItemStackListTypeAdapter(boolean allowNullList, boolean allowNullSingle) {
        this.allowNullList = allowNullList;
        this.singleAdapter = new ItemStackTypeAdapter(allowNullSingle);
    }

    public ItemStackListTypeAdapter(boolean allowNullSingle) {
        this.singleAdapter = new ItemStackTypeAdapter(allowNullSingle);
    }

    @Override
    public JsonElement serialize(List<ItemStack> items, Type type, JsonSerializationContext context) {
        if (allowNullList && items == null) return JsonNull.INSTANCE;
        JsonArray array = new JsonArray();
        for (ItemStack item : items) {
            array.add(singleAdapter.serialize(item, ItemStack.class, context));
        }
        return array;
    }

    @Override
    public List<ItemStack> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (allowNullList && json.isJsonNull()) return null;
        JsonArray array = json.getAsJsonArray();
        List<ItemStack> items = new ArrayList<>();
        for (JsonElement el : array) {
            items.add(singleAdapter.deserialize(el, ItemStack.class, context));
        }
        return items;
    }
}
