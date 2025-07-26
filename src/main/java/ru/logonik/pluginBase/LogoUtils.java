package ru.logonik.pluginBase;

public class LogoUtils {
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }

    public static String formatTimeAgoFromSeconds(long timestampSeconds) {
        long now = System.currentTimeMillis();
        long diff = now - (timestampSeconds * 1000L);

        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (seconds < 60) return "только что";
        if (minutes < 60) return minutes + " " + plural(minutes, "минуту", "минуты", "минут") + " назад";
        if (hours < 24) return hours + " " + plural(hours, "час", "часа", "часов") + " назад";
        return days + " " + plural(days, "день", "дня", "дней") + " назад";
    }


    private static String plural(long count, String one, String few, String many) {
        if (count % 10 == 1 && count % 100 != 11) return one;
        if (count % 10 >= 2 && count % 10 <= 4 && (count % 100 < 10 || count % 100 >= 20)) return few;
        return many;
    }
}
