package ru.logonik.pluginBase.util;

import java.util.HashMap;
import java.util.Map;

public class UtilTransliterator {

    private static final Map<Character, String> translitMap = new HashMap<>();

    static {
        translitMap.put('а', "a");
        translitMap.put('б', "b");
        translitMap.put('в', "v");
        translitMap.put('г', "g");
        translitMap.put('д', "d");
        translitMap.put('е', "e");
        translitMap.put('ё', "yo");
        translitMap.put('ж', "zh");
        translitMap.put('з', "z");
        translitMap.put('и', "i");
        translitMap.put('й', "y");
        translitMap.put('к', "k");
        translitMap.put('л', "l");
        translitMap.put('м', "m");
        translitMap.put('н', "n");
        translitMap.put('о', "o");
        translitMap.put('п', "p");
        translitMap.put('р', "r");
        translitMap.put('с', "s");
        translitMap.put('т', "t");
        translitMap.put('у', "u");
        translitMap.put('ф', "f");
        translitMap.put('х', "kh");
        translitMap.put('ц', "ts");
        translitMap.put('ч', "ch");
        translitMap.put('ш', "sh");
        translitMap.put('щ', "shch");
        translitMap.put('ъ', "b");
        translitMap.put('ы', "y");
        translitMap.put('ь', "b");
        translitMap.put('э', "e");
        translitMap.put('ю', "yu");
        translitMap.put('я', "ya");
    }

    public static String transliterate(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toLowerCase().toCharArray()) {
            if (translitMap.containsKey(c)) {
                result.append(translitMap.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}