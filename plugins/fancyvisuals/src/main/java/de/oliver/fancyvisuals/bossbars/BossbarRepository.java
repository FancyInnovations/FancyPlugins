package de.oliver.fancyvisuals.bossbars;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface BossbarRepository {

    BossbarSet DEFAULT_BOSSBARS = new BossbarSet(List.of(
            new BossbarConfig(
                    "announcement",
                    "<gradient:#00c8ff:#7c00ff>FancyVisuals</gradient>",
                    1.0,
                    "BLUE",
                    "SOLID",
                    List.of(),
                    true
            )
    ));

    @NotNull BossbarSet getBossbarsForPlayer(@NotNull Player player);
}
