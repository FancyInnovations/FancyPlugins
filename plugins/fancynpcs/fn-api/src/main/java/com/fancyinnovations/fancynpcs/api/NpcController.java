package com.fancyinnovations.fancynpcs.api;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * The controller for NPCs, responsible for showing and hiding them to players.
 */
public interface NpcController {

    /**
     * Shows the NPC to the given players if they should see it, and it is not yet shown to them.
     */
    @ApiStatus.Internal
    void showNpcTo(@NotNull final Npc npc, @NotNull final Player... players);

    /**
     * Hides the NPC from the given players if they should not see it, and it is shown to them.
     */
    @ApiStatus.Internal
    void hideNpcFrom(@NotNull final Npc npc, @NotNull final Player... players);

    /**
     * Returns whether the given player should see the NPC.
     */
    @ApiStatus.Internal
    boolean shouldSeeNpc(@NotNull final Npc npc, @NotNull final Player player);

    /**
     * Updates NPC data such as equipment, attributes, etc. for the given players.
     * Be aware that some data changes require the NPC to be fully respawned.
     */
    @ApiStatus.Internal
    void updateNpcData(@NotNull final Npc npc, @NotNull final Player... players);

    /**
     * Spawns the NPC to the given players if they should see it, and it is not yet shown to them.
     * Hide the NPC from the players that should not see it.
     */
    void refreshNpc(@NotNull final Npc npc, @NotNull final Player... players);

    default void refreshNpc(@NotNull final Npc npc, @NotNull final Collection<? extends Player> players) {
        refreshNpc(npc, players.toArray(new Player[0]));
    }

    /**
     * Refreshes all NPCs for the given player, checking visibility and showing/hiding accordingly.
     * This is useful when a player joins or changes worlds.
     */
    void refreshAllNpcs(@NotNull final Player player);

}
