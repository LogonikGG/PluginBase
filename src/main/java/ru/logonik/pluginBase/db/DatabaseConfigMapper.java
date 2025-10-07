package ru.logonik.pluginBase.db;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class DatabaseConfigMapper {

    public static DatabaseConfig loadFromConfigDefault(JavaPlugin plugin) {
        return loadFromSection(plugin.getConfig(), "database");
    }

    public static DatabaseConfig loadFromConfig(FileConfiguration config, String basePath) {
        DatabaseConfig dbConfig = new DatabaseConfig();

        dbConfig.setDriverClass(config.getString(basePath + ".driverClass"));
        dbConfig.setDriverJar(config.getString(basePath + ".driverJar"));
        dbConfig.setUrl(config.getString(basePath + ".url"));
        dbConfig.setUsername(config.getString(basePath + ".username"));
        dbConfig.setPassword(config.getString(basePath + ".password"));

        return dbConfig;
    }

    public static DatabaseConfig loadFromSection(FileConfiguration config, String sectionName) {
        ConfigurationSection section = config.getConfigurationSection(sectionName);
        if (section != null) {
            DatabaseConfig dbConfig = new DatabaseConfig();

            dbConfig.setDriverClass(section.getString("driverClass"));
            dbConfig.setDriverJar(section.getString("driverJar"));
            dbConfig.setUrl(section.getString("url"));
            dbConfig.setUsername(section.getString("username"));
            dbConfig.setPassword(section.getString("password"));

            return dbConfig;
        } else {
            return loadFromConfig(config, sectionName);
        }
    }
}
