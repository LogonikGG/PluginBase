package ru.logonik.pluginBase.db;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import ru.logonik.pluginBase.saveload.OrmLiteLoadSaver;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.sql.DriverManager;

public class DatabaseOrmliteManager {

    private final ConnectionSource connectionSource;

    public DatabaseOrmliteManager(DatabaseConfig config) throws Exception {
        ClassLoader loader;
        if (config.getDriverJar() != null && !config.getDriverJar().trim().isEmpty()) {
            File driverJar = new File(config.getDriverJar());
            loader = new URLClassLoader(new URL[]{driverJar.toURI().toURL()}, this.getClass().getClassLoader());
        } else {
            loader = this.getClass().getClassLoader();
        }
        Class<?> driverClass = Class.forName(config.getDriverClass(), true, loader);
        Driver driverInstance = (Driver) driverClass.getDeclaredConstructor().newInstance();
        DriverManager.registerDriver(new DriverShim(driverInstance));

        this.connectionSource = new JdbcConnectionSource(config.getUrl(), config.getUsername(), config.getPassword());
    }

    public <ID, O> OrmLiteLoadSaver<ID, O> getLoadSaver(Class<O> clazz) throws Exception {
        Dao<O, ID> dao = DaoManager.createDao(connectionSource, clazz);
        return new OrmLiteLoadSaver<>(dao);
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }

    public void close() throws Exception {
        if (connectionSource != null) {
            connectionSource.close();
        }
    }

    private static class DriverShim implements Driver {
        private final Driver driver;

        DriverShim(Driver driver) { this.driver = driver; }
        @Override public boolean acceptsURL(String u) { try { return driver.acceptsURL(u); } catch (Exception e) { return false; } }
        @Override public java.sql.Connection connect(String u, java.util.Properties p) { try { return driver.connect(u, p); } catch (Exception e) { return null; } }
        @Override public int getMajorVersion() { return driver.getMajorVersion(); }
        @Override public int getMinorVersion() { return driver.getMinorVersion(); }
        @Override public java.sql.DriverPropertyInfo[] getPropertyInfo(String u, java.util.Properties p) { try { return driver.getPropertyInfo(u, p); } catch (Exception e) { return new java.sql.DriverPropertyInfo[0]; } }
        @Override public boolean jdbcCompliant() { return driver.jdbcCompliant(); }
        @Override public java.util.logging.Logger getParentLogger() { try { return driver.getParentLogger(); } catch (Exception e) { return java.util.logging.Logger.getLogger("default"); } }
    }
}
