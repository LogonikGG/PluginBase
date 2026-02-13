
## LoadSaver
A simple interface for loading and saving data. Below are examples of two implementations of this interface.
```java
public interface LoadSaver<ID,O> {
    O load(ID object) throws SaveLoadException;
    Map<ID,O> loadAll() throws SaveLoadException;
    void save(ID id, O object) throws SaveLoadException;
    void saveAll(Map<ID,O> objects) throws SaveLoadException;
    void delete(ID id) throws SaveLoadException;
    void deleteAll() throws SaveLoadException;
}
```

### 1 - Initializing Ormlite

* `DatabaseConfig` — Model for storing basic database settings.
* `DatabaseConfigMapper` — Mapper for loading basic settings from a Yaml config.
* `DatabaseOrmliteManager` — Used to create a manager based on `DatabaseConfig`.

Real example:
```java
public class BlocksSaveLoadService implements PluginStartListener, PluginDisableListener {

    private DatabaseOrmliteManager ormliteManager;
    private OrmLiteLoadSaver<Long, RustBlockModel> blockModelLoadSaver;
    private OrmLiteLoadSaver<Long, HealthPoolData> healthPoolDataLoadSaver;
    private OrmLiteLoadSaver<Long, BarrelBlockData> barrelsDataLoadSaver;

    @Override
    public void start(ServicesLocator servicesLocator) throws Exception {
        Plugin plugin = servicesLocator.getServiceOrThrow(Plugin.class);
        FileConfiguration config = plugin.getConfig();
        DatabaseConfig databaseConfig = DatabaseConfigMapper.loadFromConfigDefault((YamlConfiguration) config);
        ormliteManager = new DatabaseOrmliteManager(databaseConfig);
        blockModelLoadSaver = ormliteManager.getLoadSaver(RustBlockModel.class);
        healthPoolDataLoadSaver = ormliteManager.getLoadSaver(HealthPoolData.class);
        barrelsDataLoadSaver = ormliteManager.getLoadSaver(BarrelBlockData.class);

        load();
    }
}
```

### 2 - Initializing PerFileGsonLoadSaver
Real example:
```java
public class PlayerLobbyLocationProviderImpl extends BulkDataManager<String, HubLocation, HubLocation> implements IPlayerLobbyLocationProvider {

    private Random generator;

    public PlayerLobbyLocationProviderImpl(Path saveDirectory) {
        super(new PerFileGsonLoadSaver<>(saveDirectory,
                new GsonBuilder()
                        .registerTypeAdapter(Location.class, new LocationTypeAdapter())
                        .registerTypeAdapter(Component.class, new ComponentTypeAdapter()).create(),
                new TypeToken<>() {
                },
                new PerFileGsonLoadSaver.StringFileNameMapper()), Function.identity(), Function.identity(), (ignore) -> null);
    }
}
```
