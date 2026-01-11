package de.oliver.fancyvisuals.titles;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface TitleRepository {

    TitleConfig DEFAULT_TITLES = new TitleConfig(
            5000,
            List.of(
                    new TitleMessage(
                            "<gradient:#00c8ff:#7c00ff>FancyVisuals</gradient>",
                            "<gray>Welcome, <white>%player_name%</white></gray>",
                            10,
                            40,
                            10
                    )
            )
    );

    @NotNull TitleConfig getTitlesForPlayer(@NotNull Player player);
}
