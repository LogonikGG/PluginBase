package ru.logonik.pluginBase.db;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseConfigMapper {

    public static DatabaseConfig loadFromConfigDefault(JavaPlugin plugin) {
        return loadFromSection(plugin.getConfig(), "database");
    }

    public static DatabaseConfig loadFromSection(FileConfiguration config, String sectionName) {
        return loadFromSection(config.getConfigurationSection(sectionName));
    }

    private static DatabaseConfig loadFromSection(ConfigurationSection section) {
        if (section == null) return null;
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setDriverClass(section.getString("driverClass"));
        dbConfig.setDriverJar(section.getString("driverJar"));
        dbConfig.setUrl(section.getString("url"));
        dbConfig.setUsername(section.getString("username"));
        dbConfig.setPassword(section.getString("password"));
        return dbConfig;
    }
}
