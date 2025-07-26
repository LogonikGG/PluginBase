package ru.logonik.pluginBase;

public interface Scheduler {
    void runRepeatingAsync(Runnable task, long delayMillis, long periodMillis);
    void runRepeatingSync(Runnable task, long delayMillis, long periodMillis);
    void runAsync(Runnable task);
    void runSync(Runnable task);
}
