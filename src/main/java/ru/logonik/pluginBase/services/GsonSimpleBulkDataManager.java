package ru.logonik.pluginBase.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import ru.logonik.pluginBase.saveload.PerFileGsonLoadSaver;

import java.nio.file.Path;
import java.util.function.Function;

public class GsonSimpleBulkDataManager<K, D> extends BulkDataManager<K, D, D> {

    public GsonSimpleBulkDataManager(Path path, TypeToken<D> token, PerFileGsonLoadSaver.FileNameMapper<K> mapper) {
        super(new PerFileGsonLoadSaver<>(path, new Gson(), token, mapper), Function.identity(), Function.identity(), data -> null);
    }

    public GsonSimpleBulkDataManager() {
    }
}
