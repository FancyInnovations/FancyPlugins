package de.oliver.fancyvisuals.chat;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface ChatFormatRepository {

    ChatFormat DEFAULT_CHAT_FORMAT = new ChatFormat("<gray>%player_name%</gray> <dark_gray>></dark_gray> {message}");

    @NotNull ChatFormat getFormatForPlayer(@NotNull Player player);
}
