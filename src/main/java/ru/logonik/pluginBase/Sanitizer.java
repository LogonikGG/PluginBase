package ru.logonik.pluginBase;

public class Sanitizer {

    private static final String VALID_REGEX = "^[a-zA-Z_-]+$";


    public static String sanitize(String input) {
        if (input == null) return "";
        return input.replaceAll(VALID_REGEX, "");
    }

    public static boolean isValid(String input) {
        if (input == null) return false;
        return input.matches(VALID_REGEX);
    }

    public static void throwIfNotValid(String input) {
        if (!input.matches(VALID_REGEX)) {
            throw new IllegalArgumentException("Input is not valid. Expected pattern: " + VALID_REGEX);
        }
    }

    public static boolean isEmptyAfterSanitize(String input) {
        return sanitize(input).isEmpty();
    }
}
