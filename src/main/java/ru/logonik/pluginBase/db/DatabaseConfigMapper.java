package ru.logonik.pluginBase.db;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Утилитный класс для маппинга настроек базы данных из конфигурации плагина Bukkit/Spigot
 * в объект {@link DatabaseConfig}.
 * <p>
 * Пример конфигурации (config.yml):
 * <pre>{@code
 * database:
 *   driverClass: com.mysql.cj.jdbc.Driver
 *   driverJar: plugins/lib/mysql-connector-java-8.0.30.jar
 *   url: jdbc:mysql://localhost:3306/my_database
 *   username: myuser
 *   password: secret
 * }</pre>
 */
public class DatabaseConfigMapper {

    private DatabaseConfigMapper() {
    }

    /**
     * Загружает настройки базы данных из стандартного {@code config.yml}
     * текущего плагина.
     * <p>
     * Ищет секцию {@code database} и преобразует её в {@link DatabaseConfig}.
     *
     * @param configuration экземпляр конфига. К примеру {@link JavaPlugin#getConfig()}
     * @return объект {@link DatabaseConfig}, или {@code null}, если секция не найдена
     */
    public static DatabaseConfig loadFromConfigDefault(YamlConfiguration configuration) {
        return loadFromSection(configuration, "database");
    }

    /**
     * Загружает настройки базы данных из указанной секции в конфигурации.
     *
     * @param config      конфигурация {@link FileConfiguration}
     * @param sectionName имя секции (например, {@code "database"})
     * @return объект {@link DatabaseConfig}, или {@code null}, если секция не найдена
     */
    public static DatabaseConfig loadFromSection(FileConfiguration config, String sectionName) {
        return loadFromSection(config.getConfigurationSection(sectionName));
    }

    /**
     * Преобразует {@link ConfigurationSection} в объект {@link DatabaseConfig}.
     * <p>
     * Ожидаемые поля:
     * <ul>
     *   <li>{@code driverClass} – имя класса драйвера JDBC</li>
     *   <li>{@code driverJar} – путь к jar-драйверу (необязательно, если драйвер уже доступен)</li>
     *   <li>{@code url} – JDBC URL для подключения</li>
     *   <li>{@code username} – имя пользователя</li>
     *   <li>{@code password} – пароль</li>
     * </ul>
     *
     * @param section секция конфигурации
     * @return объект {@link DatabaseConfig}, или {@code null}, если секция равна {@code null}
     */
    private static DatabaseConfig loadFromSection(ConfigurationSection section) {
        if (section == null) {
            return null;
        } else {
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setDriverClass(section.getString("driverClass"));
            dbConfig.setDriverJar(section.getString("driverJar"));
            dbConfig.setUrl(section.getString("url"));
            dbConfig.setUsername(section.getString("username"));
            dbConfig.setPassword(section.getString("password"));
            return dbConfig;
        }
    }
}
