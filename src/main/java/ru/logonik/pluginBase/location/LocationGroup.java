package ru.logonik.pluginBase.location;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class LocationGroup {
    private final String keyName;
    private final List<Location> locations = new ArrayList<>();
    private final AtomicInteger nextIndex = new AtomicInteger(0);

    public LocationGroup(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyName() {
        return keyName;
    }

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

    public Location getNext() {
        if (locations.isEmpty()) return null;
        int index = nextIndex.getAndUpdate(i -> (i + 1) % locations.size());
        return locations.get(index);
    }

    public void resetSequence() {
        nextIndex.set(0);
    }
}
