package ru.logonik.pluginBase.util;

import org.bukkit.ChatColor;

import java.util.Objects;

import static ru.logonik.pluginBase.util.UtilBukkit.colorize;

public class StringUtil {
    public static String sanitize(String string) {
        Objects.requireNonNull(string);
        return string.trim().replaceAll(" ", "_").toLowerCase();
    }

    public static boolean notSanitized(String string) {
        Objects.requireNonNull(string);
        return string.contains(" ") || string.trim().isEmpty();
    }

    public static void requiresNotNullAndBlank(String str) {
        if(str == null || str.trim().isEmpty()) throw new IllegalArgumentException("String is empty or blank");
    }

    public static boolean isNameIncorrectForWorldName(String name) {
        if (name == null || name.isEmpty()) {
            return true;
        }
        return !name.matches("^[a-zA-Z0-9_-]+$");
    }

    public static String cleanForWorldName(String input) {
        input = input.toLowerCase();

        return input.replaceAll("[^a-z0-9/._-]", "");
    }

    public static boolean isNullOrBlank(String string) {
        return string == null || string.trim().isEmpty();
    }

    public static String colorizeWithChatColorReset(String string) {
        String colorize = colorize(string);
        return colorize.endsWith(ChatColor.RESET.toString()) ? colorize : colorize + ChatColor.RESET;
    }
}
