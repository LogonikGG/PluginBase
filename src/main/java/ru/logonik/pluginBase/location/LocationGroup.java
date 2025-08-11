package ru.logonik.pluginBase.location;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class LocationGroup {
    private final List<Location> locations = new ArrayList<>();

    public void add(Location location) {
        if (location == null) throw new IllegalArgumentException("Location cannot be null");
        locations.add(location.clone());
    }

    public boolean remove(Location location) {
        return locations.remove(location);
    }

    public List<Location> getAll() {
        return Collections.unmodifiableList(locations);
    }

    public Location getFirst() {
        return locations.isEmpty() ? null : locations.get(0);
    }

    public Location getRandom() {
        if (locations.isEmpty()) return null;
        return locations.get(ThreadLocalRandom.current().nextInt(locations.size()));
    }

    public boolean isEmpty() {
        return locations.isEmpty();
    }
}
