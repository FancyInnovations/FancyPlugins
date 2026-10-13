package com.fancyinnovations.fancynpcs.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Interface for NPC storage implementations.
 * Allows for different storage backends (flat file, database, etc.)
 */
public interface NpcStorage {

    /**
     * Saves a collection of NPCs.
     *
     * @param npcs     The NPCs to save.
     * @param override Whether to override existing NPCs (force save all).
     */
    void saveBatch(@NotNull Collection<Npc> npcs, boolean override);

    /**
     * Saves a single NPC.
     *
     * @param npc The NPC to save.
     */
    void save(@NotNull Npc npc);

    /**
     * Deletes an NPC from storage.
     *
     * @param npc The NPC to delete.
     */
    void delete(@NotNull Npc npc);

    /**
     * Loads all NPCs from storage.
     *
     * @return A collection of all loaded NPCs.
     */
    @NotNull
    Collection<Npc> loadAll();

    /**
     * Loads all NPCs from a specific world.
     *
     * @param world The world to load the NPCs from.
     * @return A collection of all loaded NPCs in that world.
     */
    @NotNull
    Collection<Npc> loadAll(@Nullable String world);
}
