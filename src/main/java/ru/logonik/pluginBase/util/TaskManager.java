package ru.logonik.pluginBase.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TaskManager {

    private final Plugin plugin;
    private final Map<Object, List<TaskInfo>> tasks = new ConcurrentHashMap<>();

    public TaskManager(Plugin plugin) {
        this.plugin = Objects.requireNonNull(plugin, "Plugin cannot be null");
    }

    public TaskManager() {
        this.plugin = JavaPlugin.getProvidingPlugin(TaskManager.class);
    }

    public TaskInfo runTaskLater(Object key, long delayTicks, Runnable runnable) {
        return scheduleTask(key, runnable, delayTicks, -1);
    }

    public TaskInfo runTaskLaterAsync(Object key, long delayTicks, Runnable runnable) {
        return scheduleTask(key, runnable, delayTicks, -1, true);
    }

    public TaskInfo runTaskTimer(Object key, long delayTicks, long periodTicks, Runnable runnable) {
        return scheduleTask(key, runnable, delayTicks, periodTicks);
    }

    public TaskInfo runTaskTimerAsync(Object key, long delayTicks, long periodTicks, Runnable runnable) {
        return scheduleTask(key, runnable, delayTicks, periodTicks, true);
    }

    public TaskInfo runTask(Object key, Runnable runnable) {
        return scheduleTask(key, runnable, 0, -1);
    }

    public TaskInfo runTaskAsync(Object key, Runnable runnable) {
        return scheduleTask(key, runnable, 0, -1, true);
    }

    private TaskInfo scheduleTask(Object key, Runnable runnable, long delay, long period) {
        return scheduleTask(key, runnable, delay, period, false);
    }

    private TaskInfo scheduleTask(Object key, Runnable runnable, long delay, long period, boolean async) {
        Objects.requireNonNull(key, "Task key cannot be null");
        Objects.requireNonNull(runnable, "Runnable cannot be null");

        BukkitTask task;
        if (period > 0) {
            task = async
                    ? Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delay, period)
                    : Bukkit.getScheduler().runTaskTimer(plugin, runnable, delay, period);
        } else if (delay > 0) {
            task = async
                    ? Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay)
                    : Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        } else {
            task = async
                    ? Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable)
                    : Bukkit.getScheduler().runTask(plugin, runnable);
        }

        TaskInfo taskInfo = new TaskInfo(task, async);
        tasks.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(taskInfo);
        return taskInfo;
    }

    public void startAutoCleanup(int cleanupIntervalTicks) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                plugin,
                this::cleanupCompletedTasks,
                cleanupIntervalTicks,
                cleanupIntervalTicks
        );
    }

    public boolean cancelTask(Object key, int taskId) {
        List<TaskInfo> taskList = tasks.get(key);
        if (taskList != null) {
            Iterator<TaskInfo> iterator = taskList.iterator();
            while (iterator.hasNext()) {
                TaskInfo taskInfo = iterator.next();
                if (taskInfo.getTaskId() == taskId) {
                    taskInfo.task.cancel();
                    iterator.remove();
                    if (taskList.isEmpty()) {
                        tasks.remove(key);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public boolean cancelTask(Object key) {
        List<TaskInfo> taskList = tasks.remove(key);
        if (taskList != null && !taskList.isEmpty()) {
            for (TaskInfo taskInfo : taskList) {
                taskInfo.task.cancel();
            }
            return true;
        }
        return false;
    }

    public int cancelAllTasks(Object key) {
        List<TaskInfo> taskList = tasks.remove(key);
        if (taskList != null && !taskList.isEmpty()) {
            int count = taskList.size();
            for (TaskInfo taskInfo : taskList) {
                taskInfo.task.cancel();
            }
            return count;
        }
        return 0;
    }

    public void cancelAllTasks() {
        for (List<TaskInfo> taskList : tasks.values()) {
            for (TaskInfo taskInfo : taskList) {
                taskInfo.task.cancel();
            }
        }
        tasks.clear();
    }

    public boolean hasTask(Object key) {
        List<TaskInfo> taskList = tasks.get(key);
        if (taskList != null) {
            for (TaskInfo taskInfo : taskList) {
                if (Bukkit.getScheduler().isCurrentlyRunning(taskInfo.task.getTaskId())
                        || Bukkit.getScheduler().isQueued(taskInfo.task.getTaskId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasTask(Object key, int taskId) {
        List<TaskInfo> taskList = tasks.get(key);
        if (taskList != null) {
            for (TaskInfo taskInfo : taskList) {
                if (taskInfo.getTaskId() == taskId) {
                    return Bukkit.getScheduler().isCurrentlyRunning(taskInfo.task.getTaskId())
                            || Bukkit.getScheduler().isQueued(taskInfo.task.getTaskId());
                }
            }
        }
        return false;
    }

    @Nullable
    public TaskInfo getTaskInfo(Object key, int taskId) {
        List<TaskInfo> taskList = tasks.get(key);
        if (taskList != null) {
            for (TaskInfo taskInfo : taskList) {
                if (taskInfo.getTaskId() == taskId) {
                    return taskInfo;
                }
            }
        }
        return null;
    }

    @Nullable
    public List<TaskInfo> getTaskInfos(Object key) {
        List<TaskInfo> taskList = tasks.get(key);
        return taskList != null ? Collections.unmodifiableList(taskList) : null;
    }

    public List<TaskInfo> getAllTaskInfos() {
        List<TaskInfo> allTasks = new ArrayList<>();
        for (List<TaskInfo> taskList : tasks.values()) {
            allTasks.addAll(taskList);
        }
        return Collections.unmodifiableList(allTasks);
    }

    public Set<Object> getActiveTaskKeys() {
        return Collections.unmodifiableSet(tasks.keySet());
    }

    public int getActiveTaskCount() {
        int total = 0;
        for (List<TaskInfo> taskList : tasks.values()) {
            total += taskList.size();
        }
        return total;
    }

    public int getActiveTaskCount(Object key) {
        List<TaskInfo> taskList = tasks.get(key);
        return taskList != null ? taskList.size() : 0;
    }

    public void cleanupCompletedTasks() {
        for (Map.Entry<Object, List<TaskInfo>> entry : tasks.entrySet()) {
            Iterator<TaskInfo> iterator = entry.getValue().iterator();
            while (iterator.hasNext()) {
                TaskInfo taskInfo = iterator.next();
                int taskId = taskInfo.getTaskId();
                if (!Bukkit.getScheduler().isCurrentlyRunning(taskId)
                        && !Bukkit.getScheduler().isQueued(taskId)) {
                    iterator.remove();
                }
            }
            if (entry.getValue().isEmpty()) {
                tasks.remove(entry.getKey());
            }
        }
    }

    public static class TaskInfo {
        private final BukkitTask task;
        private final boolean async;

        private TaskInfo(BukkitTask task, boolean async) {
            this.task = task;
            this.async = async;
        }

        public BukkitTask getTask() {
            return task;
        }

        public boolean isAsync() {
            return async;
        }

        public int getTaskId() {
            return task.getTaskId();
        }
    }
}