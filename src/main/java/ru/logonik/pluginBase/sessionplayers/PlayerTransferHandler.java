package ru.logonik.pluginBase.sessionplayers;


import java.util.UUID;

public interface PlayerTransferHandler {
    boolean forbiddenTransfer(UUID player, SessionManager sessionManager);
    void processJoin(UUID player, SessionManager sessionManager);
    void processLeave(UUID player, SessionManager sessionManager);

}
