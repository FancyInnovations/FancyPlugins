package de.oliver.fancyvisuals.actionbar;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface ActionBarRepository {

    ActionBarConfig DEFAULT_ACTIONBAR = new ActionBarConfig(
            List.of("<gray>Welcome, <white>%player_name%</white></gray>"),
            1000
    );

    @NotNull ActionBarConfig getActionBarForPlayer(@NotNull Player player);
}
