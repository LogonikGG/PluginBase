package ru.logonik.pluginBase.models;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.logonik.pluginBase.Logger;

import java.util.Objects;
import java.util.function.Consumer;

public class SecondsTimer {
    private final JavaPlugin plugin;
    private int left;
    private String notifyString;
    private BukkitTask bukkitTask;
    private Runnable atEnd;
    private Consumer<String> notify;
    private boolean inAccusative;
    private boolean stopping;

    public SecondsTimer() {
        this(JavaPlugin.getProvidingPlugin(SecondsTimer.class));
    }

    public SecondsTimer(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setAtEnd(Runnable atEnd) {
        this.atEnd = atEnd;
    }

    public void setNotify(Consumer<String> notify) {
        this.notify = notify;
    }

    public void setNotifyString(String notifyString) {
        this.notifyString = notifyString;
    }

    public void start(int left, Runnable atEnd) {
        if (bukkitTask == null) {
            Objects.requireNonNull(atEnd);
            if (left < 0) throw new IllegalArgumentException("Left time must be 0 or higher");
            this.atEnd = atEnd;
            setLeft(left + 1);
            bukkitTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                this.left--;
                onTimer();
            }, 0L, 20L);
            stopping = false;
        } else {
            throw new IllegalStateException("Timer already goes");
        }
    }

    /**
     * Stops the timer if it is currently running.
     * If the timer is already stopped, this method does nothing.
     */
    public void stop() {
        if (!isStop()) {
            stopBukkitTimer();
            atEnd = null;
            stopping = false;
        }
    }

    /**
     * @deprecated As it is replaced by {@link SecondsTimer#stop()}
     */
    @Deprecated(forRemoval = true)
    public void stopFully() {
        if (!isStop()) {
            stopBukkitTimer();
            atEnd = null;
            stopping = false;
        }
    }

    private void onTimer() {
        StringBuilder sb = new StringBuilder();
        int minutes = left / 60;
        switch (left) {
            case 3600:
            case 3000:
            case 2400:
            case 1800:
            case 1200:
            case 900:
            case 600:
            case 300:
                sb.append(minutes).append(" минут");
                break;
            case 240:
            case 180:
            case 120:
                sb.append(minutes).append(" минуты");
                break;
            case 60:
                sb.append(minutes).append(inAccusative ? " минуту" : " минута");
                break;
            case 30:
            case 10:
            case 5:
                sb.append(left).append(" секунд");
                break;
            case 4:
            case 3:
            case 2:
                sb.append(left).append(" секунды");
                break;
            case 1:
                sb.append(left).append(inAccusative ? " секунду" : " секунда");
                break;
            case 0:
                stopBukkitTimer();
                try {
                    atEnd.run();
                } catch (Exception e) {
                    Logger.error("Error at handle end of timer", e);
                }
                return;
            default:
                return;
        }
        if (notify != null && notifyString != null) {
            notify.accept(notifyString.replace("{}", sb));
        }
    }

    private void stopBukkitTimer() {
        Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
        bukkitTask = null;
    }

    public String getLeftLocalized() {
        return formatTime(left);
    }

    private String formatTime(int seconds) {
        if (seconds <= 0) return "0 секунд";

        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        if (minutes > 0) {
            return formatMinutes(minutes) + (remainingSeconds > 0 ? " " + formatSeconds(remainingSeconds) : "");
        } else {
            return formatSeconds(remainingSeconds);
        }
    }

    private String formatMinutes(int minutes) {
        if (minutes % 10 == 1 && minutes % 100 != 11) {
            return minutes + " минута";
        } else if (minutes % 10 >= 2 && minutes % 10 <= 4 && (minutes % 100 < 10 || minutes % 100 >= 20)) {
            return minutes + " минуты";
        } else {
            return minutes + " минут";
        }
    }

    private String formatSeconds(int seconds) {
        if (seconds % 10 == 1 && seconds % 100 != 11) {
            return seconds + " секунда";
        } else if (seconds % 10 >= 2 && seconds % 10 <= 4 && (seconds % 100 < 10 || seconds % 100 >= 20)) {
            return seconds + " секунды";
        } else {
            return seconds + " секунд";
        }
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        if (left < 1) {
            throw new IllegalArgumentException("left cannot be less than 1");
        }
        this.left = left;
    }

    public boolean isStop() {
        return bukkitTask == null || bukkitTask.isCancelled();
    }

    public boolean isInAccusative() {
        return inAccusative;
    }

    public void setInAccusative(boolean inAccusative) {
        this.inAccusative = inAccusative;
    }
}
