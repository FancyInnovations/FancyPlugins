package de.oliver.fancyvisuals.api.nametags;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The {@code NametagStore} interface defines operations for storing, retrieving, and managing {@code Nametag} objects
 * associated with unique identifiers.
 */
public interface NametagStore {

    /**
     * Associates the specified {@code Nametag} with the provided {@code id} in the store.
     *
     * @param id      the unique identifier for the nametag.
     * @param nametag the Nametag object to be associated with the specified id.
     */
    void setNametag(@NotNull String id, @NotNull Nametag nametag);

    /**
     * Retrieves the {@code Nametag} associated with the specified {@code id}.
     *
     * @param id the unique identifier for the nametag.
     * @return the Nametag associated with the given id; may return null if no such nametag is found.
     */
    @Nullable Nametag getNametag(@NotNull String id);

    /**
     * Removes the {@code Nametag} associated with the specified {@code id} from the store.
     *
     * @param id the unique identifier for the nametag to be removed.
     */
    void removeNametag(@NotNull String id);

    /**
     * Retrieves a list of all the Nametags in the store.
     *
     * @return a list of Nametag objects; the list may be empty if no nametags are present in the store.
     */
    @NotNull List<Nametag> getNametags();

}
