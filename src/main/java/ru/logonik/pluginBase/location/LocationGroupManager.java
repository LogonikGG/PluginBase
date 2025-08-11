package ru.logonik.pluginBase.location;

import org.bukkit.Location;
import ru.logonik.pluginBase.Sanitizer;
import ru.logonik.pluginBase.execptions.SaveLoadException;
import ru.logonik.pluginBase.saveload.LoadSaver;
import ru.logonik.pluginBase.services.BulkDataManager;

import java.util.stream.Collectors;

public class LocationGroupManager extends BulkDataManager<String, LocationGroup, LocationGroupModel> {

    public LocationGroupManager(LoadSaver<String, LocationGroupModel> saver) {
        super(
            saver,
            group -> {
                LocationGroupModel model = new LocationGroupModel();
                model.serializedLocations = group.getAll().stream()
                        .map(LocationMapper::serializeLocation)
                        .collect(Collectors.toList());
                return model;
            },
            model -> {
                LocationGroup group = new LocationGroup();
                for (String serialized : model.serializedLocations) {
                    Location loc = LocationMapper.deserializeLocation(serialized);
                    if (loc != null) group.add(loc);
                }
                return group;
            },
            key -> new LocationGroup()
        );
    }

    @Override
    public LocationGroup getOrCompute(String key) {
        Sanitizer.throwIfNotValid(key);
        return super.getOrCompute(key);
    }

    @Override
    public void add(String key, LocationGroup data) throws SaveLoadException {
        Sanitizer.throwIfNotValid(key);
        super.add(key, data);
    }

    public void addLocationToGroup(String key, Location location) throws SaveLoadException {
        LocationGroup group = getOrCompute(key);
        group.add(location);
        saveImmediately(key);
    }

    public void removeLocationFromGroup(String key, Location location) throws SaveLoadException {
        LocationGroup group = get(key);
        if (group != null && group.remove(location)) {
            saveImmediately(key);
        }
    }
}
