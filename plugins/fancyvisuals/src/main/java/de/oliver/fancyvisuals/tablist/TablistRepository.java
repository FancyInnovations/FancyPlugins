package de.oliver.fancyvisuals.tablist;

import de.oliver.fancyvisuals.tablist.data.CustomTablistEntries;
import de.oliver.fancyvisuals.tablist.data.TablistHeaderFooter;
import de.oliver.fancyvisuals.tablist.data.TablistPlayerFormat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TablistRepository {

    TablistHeaderFooter DEFAULT_HEADER_FOOTER = new TablistHeaderFooter(
            "<gradient:#00c8ff:#7c00ff>FancyVisuals</gradient>",
            "<gray>Online: <white>%server_online%</white>"
    );

    TablistPlayerFormat DEFAULT_PLAYER_FORMAT = new TablistPlayerFormat(
            "",
            "%player_name%",
            "",
            100
    );

    CustomTablistEntries DEFAULT_CUSTOM_ENTRIES = new CustomTablistEntries(List.of());

    @NotNull TablistHeaderFooter getHeaderFooterForPlayer(@NotNull Player player);

    @NotNull TablistPlayerFormat getPlayerFormatForPlayer(@NotNull Player player);

    @NotNull CustomTablistEntries getCustomEntriesForPlayer(@NotNull Player player);
}
