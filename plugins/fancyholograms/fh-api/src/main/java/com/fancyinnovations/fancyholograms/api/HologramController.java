package com.fancyinnovations.fancyholograms.api;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The controller for holograms, responsible for showing and hiding them to players.
 */
public interface HologramController {

    /**
     * Spawns the hologram to the given players if they should see it, and it is not yet shown to them.
     * Hide the hologram from the players that should not see it.
     */
    void refreshHologram(@NotNull final Hologram hologram, @NotNull final Player players);

    /**
     * Spawns the hologram to the given players if they should see it, and it is not yet shown to them.
     * Hide the hologram from the players that should not see it.
     */
    default void refreshHologram(@NotNull final Hologram hologram, @NotNull final Collection<? extends Player> players) {
        for (Player player : players) {
            refreshHologram(hologram, player);
        }
    }

}
