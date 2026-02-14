package ru.logonik.pluginBase.sessionplayers;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class InPluginPlayerTransfer implements PlayerTransferHandler {

    private final HashMap<UUID, SessionManager> players = new HashMap<>();

    @Override
    public boolean forbiddenTransfer(UUID player, SessionManager newSessionManager) {
        SessionManager alreadySession = players.get(player);
        return alreadySession != null && !Objects.equals(alreadySession, newSessionManager) && !alreadySession.canBeForceLeft(player);
    }

    @Override
    public void processJoin(UUID player, SessionManager newSession) {
        SessionManager alreadySession = players.get(player);
        if(Objects.equals(alreadySession, newSession)) {
            return;
        }
        if(alreadySession != null) {
            safeForceLeave(alreadySession, player);
        }
        players.put(player, newSession);
    }

    @Override
    public void processLeave(UUID player, SessionManager requireSession) {
        SessionManager alreadySession = players.get(player);
        if(alreadySession == null) return;
        if(!Objects.equals(alreadySession, requireSession)) {
            safeForceLeave(alreadySession, player);
        }
        players.remove(player);
    }

    private static void safeForceLeave(SessionManager alreadySession, UUID playerId) {
        if(!alreadySession.canBeForceLeft(playerId)) {
            throw new IllegalStateException("Cannot force leave player from session");
        }
        alreadySession.forceLeave(playerId);
    }

}
