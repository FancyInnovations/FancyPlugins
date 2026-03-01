package de.oliver.fancyvisuals.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;

import java.util.List;

public final class TextRenderer {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private TextRenderer() {
    }

    public static Component render(@Nullable String text, @Nullable Player player) {
        if (text == null || text.isEmpty()) {
            return Component.empty();
        }

        if (Bukkit.isStopping()) {
            return MINI_MESSAGE.deserialize(text);
        }

        return ModernChatColorHandler.translate(text, player);
    }

    public static Component renderLines(@Nullable List<String> lines, @Nullable Player player) {
        if (lines == null || lines.isEmpty()) {
            return Component.empty();
        }

        String joined = String.join("\n", lines);
        return render(joined, player);
    }
}
