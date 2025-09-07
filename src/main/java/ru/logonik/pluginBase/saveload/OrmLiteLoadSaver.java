package ru.logonik.pluginBase.saveload;

import com.j256.ormlite.dao.Dao;
import ru.logonik.pluginBase.execptions.SaveLoadException;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrmLiteLoadSaver<ID, O> implements LoadSaver<ID, O> {

    private final Dao<O, ID> dao;

    /// DaoManager.createDao(connectionSource, Class<O>)
    public OrmLiteLoadSaver(Dao<O, ID> dao) {
        this.dao = dao;
    }

    @Override
    public O load(ID id) throws SaveLoadException {
        try {
            return dao.queryForId(id);
        } catch (SQLException e) {
            throw new SaveLoadException("Failed to load object with id=" + id, e);
        }
    }

    @Override
    public Map<ID, O> loadAll() throws SaveLoadException {
        try {
            List<O> list = dao.queryForAll();
            Map<ID, O> result = new HashMap<>();
            for (O obj : list) {
                // ORMLite даёт getId только через GenericDao или если объект помечен аннотацией @DatabaseField(id = true)
                ID id = dao.extractId(obj);
                result.put(id, obj);
            }
            return result;
        } catch (SQLException e) {
            throw new SaveLoadException("Failed to load all objects", e);
        }
    }

    @Override
    public void save(ID id, O object) throws SaveLoadException {
        try {
            dao.createOrUpdate(object);
        } catch (SQLException e) {
            throw new SaveLoadException("Failed to save object with id=" + id, e);
        }
    }

    @Override
    public void delete(ID id) throws SaveLoadException {
        try {
            dao.deleteById(id);
        } catch (SQLException e) {
            throw new SaveLoadException("Failed to delete object with id=" + id, e);
        }
    }

    @Override
    public void saveAll(Map<ID, O> objects) throws SaveLoadException {
        try {
            for (O object : objects.values()) {
                dao.createOrUpdate(object);
            }
        } catch (SQLException e) {
            throw new SaveLoadException("Failed to save all objects", e);
        }
    }
}
