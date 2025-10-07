package ru.logonik.pluginBase.db;

/**
 * Конфигурация подключения к базе данных.
 * Поддерживает динамическую подгрузку JDBC-драйвера.
 */
public class DatabaseConfig {

    /**
     * Полное имя класса JDBC-драйвера.
     * Например: org.postgresql.Driver, org.sqlite.JDBC
     */
    private String driverClass;

    /**
     * Путь к jar-файлу драйвера. Может быть относительным к папке плагина.
     * Например: plugins/MyPlugin/lib/postgresql-42.6.0.jar
     */
    private String driverJar;

    /**
     * JDBC URL для подключения.
     * Пример: jdbc:postgresql://localhost:5432/mydb?sslmode=require
     */
    private String url;

    /**
     * Имя пользователя для подключения к БД.
     */
    private String username;

    /**
     * Пароль для подключения к БД.
     */
    private String password;

    // Геттеры и сеттеры

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public String getDriverJar() {
        return driverJar;
    }

    public void setDriverJar(String driverJar) {
        this.driverJar = driverJar;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
