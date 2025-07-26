package ru.logonik.pluginBase.sessionplayers;


import java.util.UUID;

public interface PlayerTransferHandler<R extends SessionManager> {
    boolean forbiddenTransfer(UUID player, R sessionManager);
    void processJoin(UUID player, R sessionManager);
    void processLeave(UUID player, R sessionManager);

}
