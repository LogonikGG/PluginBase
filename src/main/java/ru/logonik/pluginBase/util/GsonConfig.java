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
import java.nio.file.StandardCopyOption;

public class GsonConfig<T> {

    private final Plugin plugin;
    private final String fileName;
    private final Class<T> configClass;
    private final Gson gson;
    private final boolean createBackup;
    private final int maxBackups;

    private T configData;

    public GsonConfig(Plugin plugin, String fileName, Class<T> configClass) {
        this(plugin, fileName, configClass, false, 3);
    }

    public GsonConfig(Plugin plugin, String fileName, Class<T> configClass, boolean createBackup) {
        this(plugin, fileName, configClass, createBackup, 3);
    }

    public GsonConfig(Plugin plugin, String fileName, Class<T> configClass, boolean createBackup, int maxBackups) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.configClass = configClass;
        this.createBackup = createBackup;
        this.maxBackups = maxBackups;
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();
    }

    public GsonConfig(Plugin plugin, String fileName, Class<T> configClass, Gson gson) {
        this(plugin, fileName, configClass, gson, false, 3);
    }

    public GsonConfig(Plugin plugin, String fileName, Class<T> configClass, Gson gson, boolean createBackup, int maxBackups) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.configClass = configClass;
        this.gson = gson;
        this.createBackup = createBackup;
        this.maxBackups = maxBackups;
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

    public boolean save() {
        if (configData == null) {
            configData = createDefaultConfig();
        }

        try {
            if (!plugin.getDataFolder().exists()) {
                plugin.getDataFolder().mkdirs();
            }

            Path configPath = plugin.getDataFolder().toPath().resolve(fileName);

            if (createBackup && Files.exists(configPath)) {
                createBackup(configPath);
            }

            Path tempPath = configPath.resolveSibling(fileName + ".tmp");

            try (Writer writer = Files.newBufferedWriter(tempPath, StandardCharsets.UTF_8)) {
                gson.toJson(configData, writer);
            }

            Files.move(tempPath, configPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            plugin.getLogger().info("Successfully saved " + fileName);
            return true;

        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save " + fileName + ": " + e.getMessage());
            return false;
        }
    }

    private void createBackup(Path originalPath) throws IOException {
        for (int i = maxBackups; i > 0; i--) {
            Path backupPath = originalPath.resolveSibling(fileName + ".backup" + (i == 1 ? "" : "." + i));
            Path nextBackupPath = originalPath.resolveSibling(fileName + ".backup" + (i + 1));

            if (Files.exists(backupPath)) {
                if (i == maxBackups) {
                    Files.delete(backupPath);
                } else {
                    Files.move(backupPath, nextBackupPath, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }

        Path backupPath = originalPath.resolveSibling(fileName + ".backup");
        Files.copy(originalPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
    }

    private boolean restoreFromBackup() {
        if (!createBackup) return false;

        Path originalPath = plugin.getDataFolder().toPath().resolve(fileName);
        Path latestBackup = originalPath.resolveSibling(fileName + ".backup");

        if (Files.exists(latestBackup)) {
            try {
                Files.copy(latestBackup, originalPath, StandardCopyOption.REPLACE_EXISTING);
                plugin.getLogger().info("Restored " + fileName + " from backup");
                return load();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to restore from backup: " + e.getMessage());
            }
        }

        return false;
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

    protected T createDefaultConfig() {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            plugin.getLogger().warning("Cannot create default config for " + configClass.getName());
            return null;
        }
    }
}