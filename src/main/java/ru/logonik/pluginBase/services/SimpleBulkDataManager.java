package ru.logonik.pluginBase.services;

import ru.logonik.pluginBase.execptions.SaveLoadException;
import ru.logonik.pluginBase.saveload.LoadSaver;
import ru.logonik.pluginBase.servicelocator.PluginDisableListener;
import ru.logonik.pluginBase.servicelocator.PluginStartListener;
import ru.logonik.pluginBase.servicelocator.ServicesLocator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBulkDataManager<K, V> implements PluginStartListener, PluginDisableListener {

    private final Map<K, V> dataMap = new ConcurrentHashMap<>();
    protected LoadSaver<K, V> saver;

    public SimpleBulkDataManager(LoadSaver<K, V> saver) {
        this.saver = saver;
    }

    public SimpleBulkDataManager() {
    }

    @Override
    public void start(ServicesLocator servicesLocator) throws Exception {
        loadAll();
    }

    public void loadAll() throws SaveLoadException {
        Map<K, V> allData = saver.loadAll();
        if (allData != null) {
            dataMap.putAll(allData);
        }
    }

    @Override
    public void disable() throws Exception {
        saveAll();
    }

    public void saveAll() throws SaveLoadException {
        saver.saveAll(dataMap);
    }

    public void saveImmediately(K key) throws SaveLoadException {
        V data = dataMap.get(key);
        if (data != null) {
            saver.save(key, data);
        }
    }

    public V get(K key) {
        return dataMap.get(key);
    }

    public V getOrDefault(K key, V defaultValue) {
        return dataMap.getOrDefault(key, defaultValue);
    }

    public void add(K key, V data) throws SaveLoadException {
        if (dataMap.containsKey(key)) {
            throw new IllegalArgumentException("Key already exists in map");
        }
        dataMap.put(key, data);
        saver.save(key, data);
    }

    public V remove(K key) throws SaveLoadException {
        V removed = dataMap.remove(key);
        saver.delete(key);
        return removed;
    }

    public boolean contains(K key) {
        return dataMap.containsKey(key);
    }

    public void clear() {
        dataMap.clear();
    }

    public Map<K, V> getAll() {
        return dataMap;
    }
}