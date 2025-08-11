package ru.logonik.pluginBase.location;

import java.util.ArrayList;
import java.util.List;

public class LocationGroupModel {
    private String keyName;
    private List<String> serializedLocations = new ArrayList<>();

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public List<String> getSerializedLocations() {
        return serializedLocations;
    }

    public void setSerializedLocations(List<String> serializedLocations) {
        this.serializedLocations = serializedLocations;
    }
}