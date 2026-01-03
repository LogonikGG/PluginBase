package ru.logonik.pluginBase.util;

import dev.triumphteam.gui.guis.BaseGui;
import dev.triumphteam.gui.guis.GuiItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class GuiUtils {

    public static void fillUntilNextRow(BaseGui gui, int items, GuiItem guiItem) {
        int rowComplete = items % 9;
        for (int i = rowComplete; i < 9; i++) {
            gui.addItem(guiItem);
        }
    }

    public static int stackNormalize(int value) {
        return Integer.min(64, Integer.max(1, value));
    }

    public static List<Component> formatConvertPlayersUUIDAsLore(Collection<UUID> players) {
        return formatInfoAsLore(convertPlayersToNames(players.stream().map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toSet())));
    }

    public static List<Component> formatPlayersAsLore(Collection<Player> players) {
        return formatInfoAsLore(convertPlayersToNames(players));
    }

    public static List<Component> formatInfoAsLore(Collection<String> collection) {
        ArrayList<String> names = new ArrayList<>(collection);
        List<Component> lore = new ArrayList<>();

        if (names.isEmpty()) {
            return lore;
        }

        int size = names.size();

        if (size <= 5) {
            for (String name : names) {
                lore.add(Component.text("• ", NamedTextColor.DARK_GRAY)
                        .append(Component.text(name, NamedTextColor.WHITE)));
            }
        } else if (size <= 10) {
            // От 6 до 10
            for (int i = 0; i < names.size(); i += 3) {
                List<String> sub = names.subList(i, Math.min(i + 3, names.size()));
                Component line = Component.join(
                        JoinConfiguration.separator(Component.text(", ", NamedTextColor.GRAY)),
                        sub.stream().map(name -> Component.text(name, NamedTextColor.WHITE)).collect(Collectors.toList())
                );
                lore.add(line);
            }
        } else {
            // Больше 10 — показываем первых 9 и "… и ещё N"
            for (int i = 0; i < 9; i += 3) {
                List<String> sub = names.subList(i, Math.min(i + 3, 9));
                Component line = Component.join(
                        JoinConfiguration.separator(Component.text(", ", NamedTextColor.GRAY)),
                        sub.stream().map(name -> Component.text(name, NamedTextColor.WHITE)).collect(Collectors.toList())
                );
                lore.add(line);
            }

            int remaining = size - 9;
            lore.add(Component.text("… и ещё " + remaining, NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, true));
        }

        return lore;
    }

    public static List<Component> formatInfoAsLore(
            Collection<String> collection,
            int thresholdSingle,
            int maxDisplay,
            int itemsPerLine
    ) {
        List<String> names = new ArrayList<>(collection);
        List<Component> lore = new ArrayList<>();

        if (names.isEmpty()) return lore;

        int size = names.size();

        if (size <= thresholdSingle) {
            for (String name : names) {
                lore.add(Component.text("• ", NamedTextColor.DARK_GRAY)
                        .append(Component.text(name, NamedTextColor.WHITE)));
            }
        } else {
            int displayCount = Math.min(maxDisplay, size);

            for (int i = 0; i < displayCount; i += itemsPerLine) {
                List<String> sub = names.subList(i, Math.min(i + itemsPerLine, displayCount));
                Component line = Component.join(
                        JoinConfiguration.separator(Component.text(", ", NamedTextColor.GRAY)),
                        sub.stream().map(name -> Component.text(name, NamedTextColor.WHITE)).collect(Collectors.toList())
                );
                lore.add(line);
            }

            int remaining = size - displayCount;
            if (remaining > 0) {
                lore.add(Component.text("… и ещё " + remaining, NamedTextColor.GRAY)
                        .decoration(TextDecoration.ITALIC, true));
            }
        }

        return lore;
    }

    private static List<String> convertPlayersToNames(Collection<Player> players) {
        return players.stream()
                .map(Player::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.toList());
    }
}
