package ru.logonik.pluginBase.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class GsonConfig<T> {

    private final Plugin plugin;
    private final String fileName;
    private final Class<T> configClass;
    private final Gson gson;

    private T configData;

    public GsonConfig(Plugin plugin, String fileName, Class<T> configClass) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.configClass = configClass;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    public GsonConfig(Plugin plugin, String fileName, Class<T> configClass, Gson gson) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.configClass = configClass;
        this.gson = gson;
    }

    public boolean load() {
        Path configPath = plugin.getDataFolder().toPath().resolve(fileName);

        if (!Files.exists(configPath)) {
            plugin.getLogger().warning("Config file " + fileName + " not found, creating default");
            return save();
        }

        try (Reader reader = Files.newBufferedReader(configPath, StandardCharsets.UTF_8)) {
            configData = gson.fromJson(reader, configClass);

            if (configData == null) {
                plugin.getLogger().warning("Failed to parse " + fileName + ", creating default");
                configData = createDefaultConfig();
                save();
            }

            plugin.getLogger().info("Successfully loaded " + fileName);
            return true;

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to load " + fileName + ": " + e.getMessage());
            configData = createDefaultConfig();
            return false;
        }
    }

    /**
     * Сохранить конфиг на диск
     *
     * @return true если сохранение успешно
     */
    public boolean save() {
        if (configData == null) {
            configData = createDefaultConfig();
        }

        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            Path configPath = plugin.getDataFolder().toPath().resolve(fileName);

            try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                gson.toJson(configData, writer);
            }

            plugin.getLogger().info("Successfully saved " + fileName);
            return true;

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save " + fileName + ": " + e.getMessage());
            return false;
        }
    }

    public T get() {
        if (configData == null) {
            configData = createDefaultConfig();
        }
        return configData;
    }

    public void set(T newData) {
        this.configData = newData;
        save();
    }

    public boolean reload() {
        return load();
    }

    private T createDefaultConfig() {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            plugin.getLogger().warning("Cannot create default config for " + configClass.getName());
            return null;
        }
    }
}