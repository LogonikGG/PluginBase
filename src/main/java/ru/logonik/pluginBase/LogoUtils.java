package ru.logonik.pluginBase;

public class LogoUtils {
    @SuppressWarnings("unchecked")
    public static <E extends Throwable> void sneakyThrow(Throwable e) throws E {
        throw (E) e;
    }

    public static String formatSeconds(long totalSeconds) {
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        StringBuilder result = new StringBuilder();

        if (hours > 0) {
            result.append(hours).append(" ").append(getRussianWord(hours, "час", "часа", "часов")).append(" ");
        }

        if (minutes > 0) {
            result.append(minutes).append(" ").append(getRussianWord(minutes, "минута", "минуты", "минут")).append(" ");
        }

        if (seconds > 0 || totalSeconds == 0) {
            result.append(seconds).append(" ").append(getRussianWord(seconds, "секунда", "секунды", "секунд"));
        }

        return result.toString().trim();
    }

    private static String getRussianWord(long number, String form1, String form2, String form5) {
        number = number % 100;
        if (number >= 11 && number <= 19) {
            return form5;
        }
        long i = number % 10;
        switch ((int) i) {
            case 1: return form1;
            case 2:
            case 3:
            case 4: return form2;
            default: return form5;
        }
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
