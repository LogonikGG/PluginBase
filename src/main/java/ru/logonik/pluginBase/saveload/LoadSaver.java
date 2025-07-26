package ru.logonik.pluginBase.saveload;

import ru.logonik.pluginBase.execptions.SaveLoadException;

import java.util.Map;

public interface LoadSaver<ID,O> {
    O load(ID object) throws SaveLoadException;
    Map<ID,O> loadAll() throws SaveLoadException;
    void save(ID id, O object) throws SaveLoadException;
    void delete(ID id) throws SaveLoadException;
    void saveAll(Map<ID,O> objects) throws SaveLoadException;
}
