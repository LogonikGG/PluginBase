package ru.logonik.pluginBase.saveload;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import ru.logonik.pluginBase.execptions.SaveLoadException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PerFileGsonLoadSaver<ID, O> implements LoadSaver<ID, O> {

    private final Path baseFolder;
    private final Gson gson;
    private final TypeToken<O> objectType;
    private final FileNameMapper<ID> fileNameMapper;

    public PerFileGsonLoadSaver(Path baseFolder, Gson gson, TypeToken<O> objectType, FileNameMapper<ID> fileNameMapper) {
        this.baseFolder = baseFolder;
        this.gson = gson;
        this.objectType = objectType;
        this.fileNameMapper = fileNameMapper;
    }

    @Override
    public O load(ID id) throws SaveLoadException {
        Path filePath = baseFolder.resolve(fileNameMapper.getFileName(id));
        try {
            if (!Files.exists(filePath)) {
                return null;
            }
            String json = Files.readString(filePath, StandardCharsets.UTF_8);
            return gson.fromJson(json, objectType);
        } catch (IOException | JsonSyntaxException e) {
            throw new SaveLoadException("Failed to load object from " + filePath, e);
        }
    }

    @Override
    public Map<ID, O> loadAll() throws SaveLoadException {
        Map<ID, O> result = new HashMap<>();
        try {
            Files.createDirectories(baseFolder);
        } catch (IOException e) {
            throw new SaveLoadException(e);
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseFolder, "*.json")) {
            for (Path file : stream) {
                String fileName = file.getFileName().toString();
                ID id = fileNameMapper.getIdFromFileName(fileName);
                String json = Files.readString(file, StandardCharsets.UTF_8);
                O object = gson.fromJson(json, objectType);
                result.put(id, object);
            }
        } catch (IOException | JsonSyntaxException e) {
            throw new SaveLoadException("Failed to load all objects", e);
        }
        return result;
    }

    @Override
    public void save(ID id, O object) throws SaveLoadException {
        Path filePath = baseFolder.resolve(fileNameMapper.getFileName(id));
        try {
            Files.createDirectories(baseFolder);
            String json = gson.toJson(object);
            Files.writeString(filePath, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new SaveLoadException("Failed to save object to " + filePath, e);
        }
    }

    @Override
    public void delete(ID id) throws SaveLoadException {
        Path filePath = baseFolder.resolve(fileNameMapper.getFileName(id));
        try {
            if(!Files.exists(filePath)) return;
            Files.createDirectories(baseFolder);
            Files.delete(filePath);
        } catch (IOException e) {
            throw new SaveLoadException("Failed to delete object to " + filePath, e);
        }
    }

    @Override
    public void deleteAll() throws SaveLoadException {
        try {
            if (!Files.exists(baseFolder)) {
                return;
            }

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(baseFolder, "*.json")) {
                for (Path file : stream) {
                    Files.delete(file);
                }
            }

        } catch (IOException e) {
            throw new SaveLoadException("Failed to delete all objects from " + baseFolder, e);
        }
    }

    @Override
    public void saveAll(Map<ID, O> objects) throws SaveLoadException {
        for (Map.Entry<ID, O> entry : objects.entrySet()) {
            save(entry.getKey(), entry.getValue());
        }
    }

    public interface FileNameMapper<ID> {
        String getFileName(ID id);

        ID getIdFromFileName(String fileName);
    }

    public static class StringFileNameMapper implements FileNameMapper<String> {

        @Override
        public String getFileName(String id) {
            return id + ".json";
        }

        @Override
        public String getIdFromFileName(String fileName) {
            if (fileName.endsWith(".json")) {
                return fileName.substring(0, fileName.length() - 5);
            }
            return fileName;
        }
    }

    public static class UUIDFileNameMapper implements FileNameMapper<UUID> {

        @Override
        public String getFileName(UUID id) {
            return id.toString() + ".json";
        }

        @Override
        public UUID getIdFromFileName(String fileName) {
            if (fileName.endsWith(".json")) {
                fileName = fileName.substring(0, fileName.length() - 5);
            }
            return UUID.fromString(fileName);
        }
    }


    public static class IntegerFileNameMapper implements FileNameMapper<Integer> {

        @Override
        public String getFileName(Integer id) {
            return id + ".json";
        }

        @Override
        public Integer getIdFromFileName(String fileName) {
            if (fileName.endsWith(".json")) {
                fileName = fileName.substring(0, fileName.length() - 5);
            }
            return Integer.parseInt(fileName);
        }
    }

}
