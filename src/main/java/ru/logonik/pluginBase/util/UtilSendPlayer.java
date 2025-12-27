package ru.logonik.pluginBase.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class UtilSendPlayer {
    public static void suggestCommand(Player player, String text, String command) {
        TextComponent message = new TextComponent(text);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command));
        player.spigot().sendMessage(message);
    }

    public static void runCommand(Player player, String text, String command) {
        TextComponent message = new TextComponent(text);
        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        player.spigot().sendMessage(message);
    }
}
