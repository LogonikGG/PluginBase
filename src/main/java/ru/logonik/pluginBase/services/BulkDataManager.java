package ru.logonik.pluginBase.services;

import ru.logonik.pluginBase.execptions.SaveLoadException;
import ru.logonik.pluginBase.saveload.LoadSaver;
import ru.logonik.pluginBase.servicelocator.PluginDisableListener;
import ru.logonik.pluginBase.servicelocator.PluginStartListener;
import ru.logonik.pluginBase.servicelocator.ServicesLocator;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class BulkDataManager<K, D, M> implements PluginStartListener, PluginDisableListener {

    private final Map<K, D> dataMap = new ConcurrentHashMap<>();
    protected LoadSaver<K, M> saver;
    protected Function<D, M> toModel;
    protected Function<M, D> fromModel;
    protected Function<K, D> dataFactory;

    public BulkDataManager(
            LoadSaver<K, M> saver,
            Function<D, M> toModel,
            Function<M, D> fromModel,
            Function<K, D> dataFactory
    ) {
        this.saver = saver;
        this.toModel = toModel;
        this.fromModel = fromModel;
        this.dataFactory = dataFactory;
    }

    public BulkDataManager() {
    }

    @Override
    public void start(ServicesLocator servicesLocator) throws Exception {
        loadAll();
    }

    public void loadAll() throws SaveLoadException {
        Map<K, M> allModels = saver.loadAll();
        if (allModels != null) {
            for (Map.Entry<K, M> entry : allModels.entrySet()) {
                D data = fromModel.apply(entry.getValue());
                dataMap.put(entry.getKey(), data);
            }
        }
    }

    @Override
    public void disable() throws Exception {
        saveAll();
    }

    public void saveAll() throws SaveLoadException {
        Map<K, M> modelsToSave = new ConcurrentHashMap<>();
        for (Map.Entry<K, D> entry : dataMap.entrySet()) {
            modelsToSave.put(entry.getKey(), toModel.apply(entry.getValue()));
        }
        saver.saveAll(modelsToSave);
    }

    public void saveImmediately(K key) throws SaveLoadException {
        D data = dataMap.get(key);
        if (data != null) {
            M model = toModel.apply(data);
            saver.save(key, model);
        }
    }

    public D getOrCompute(K key) {
        return dataMap.computeIfAbsent(key, dataFactory);
    }

    public D getOrDefault(K key) {
        return dataMap.getOrDefault(key, dataFactory.apply(key));
    }

    public D get(K key) {
        return dataMap.get(key);
    }

    public void add(K key, D data) throws SaveLoadException {
        if(dataMap.containsKey(key)) throw new IllegalArgumentException("Key already exist in map");
        dataMap.put(key, data);
        if (data != null) {
            M model = toModel.apply(data);
            saver.save(key, model);
        }
    }

    public D remove(K key) throws SaveLoadException {
        D remove = dataMap.remove(key);
        saver.delete(key);
        return remove;
    }

    public boolean contains(K key) {
        return dataMap.containsKey(key);
    }

    public void clear() {
        dataMap.clear();
    }

    public Map<K, D> getAll() {
        return dataMap;
    }
}