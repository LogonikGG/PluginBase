package ru.logonik.pluginBase.gson;

import com.google.gson.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class ItemStackTypeAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    private boolean allowNull = false;

    public ItemStackTypeAdapter() {
    }

    public ItemStackTypeAdapter(boolean allowNull) {
        this.allowNull = allowNull;
    }

    @Override
    public JsonElement serialize(ItemStack itemStack, Type type, JsonSerializationContext context) {
        if (allowNull && itemStack == null) return JsonNull.INSTANCE;
        // I know I'm genius =D
        YamlConfiguration config = new YamlConfiguration();
        config.options().width(Integer.MAX_VALUE);
        config.set("item", itemStack);
        String yamlString = config.saveToString();
        return new JsonPrimitive(Base64.getEncoder().encodeToString(yamlString.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (allowNull && json.isJsonNull()) return null;
        try {
            String yamlString = new String(Base64.getDecoder().decode(json.getAsString()), StandardCharsets.UTF_8);
            YamlConfiguration config = new YamlConfiguration();
            config.options().width(Integer.MAX_VALUE);
            config.loadFromString(yamlString);
            return config.getItemStack("item");
        } catch (Exception e) {
            throw new JsonParseException("Failed to deserialize ItemStack from YAML-JSON", e);
        }
    }
}