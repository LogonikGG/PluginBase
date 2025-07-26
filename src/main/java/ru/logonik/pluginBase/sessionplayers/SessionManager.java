package ru.logonik.pluginBase.sessionplayers;

import java.util.UUID;

public interface SessionManager {
    boolean inUse(UUID player);

    boolean canBeForceLeft(UUID player);

    void forceLeave(UUID player);
}
