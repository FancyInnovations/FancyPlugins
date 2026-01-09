package com.fancyinnovations.fancynpcs.api;

import java.util.Collection;
import java.util.Optional;

/**
 * An interface for managing the registration and retrieval of NPCs.
 * Provides methods to register, unregister, and query NPCs by their name or entity ID.
 */
public interface NpcRegistry {

    /**
     * Registers an NPC in the registry.
     *
     * @param npc the NPC to be registered
     * @return {@code true} if the registration was successful, otherwise {@code false}
     */
    boolean register(Npc npc);

    /**
     * Unregisters the specified NPC from the registry.
     *
     * @param npc the NPC to be unregistered
     * @return {@code true} if the NPC was successfully unregistered, otherwise {@code false}
     */
    boolean unregister(Npc npc);

    /**
     * Checks if an NPC with the specified name exists in the registry.
     *
     * @param name the name of the NPC to check for existence
     * @return {@code true} if an NPC with the specified name exists, otherwise {@code false}
     */
    boolean contains(String name);

    /**
     * Retrieves an NPC by its name from the registry.
     *
     * @param name the name of the NPC to retrieve
     * @return an {@code Optional} containing the NPC if found, or an empty {@code Optional} if no NPC exists with the specified name
     */
    Optional<Npc> get(String name);

    /**
     * Retrieves an NPC by its name from the registry, ensuring that the NPC exists.
     * If no NPC exists with the specified name, this method will throw an exception.
     *
     * @param name the name of the NPC to retrieve
     * @return the NPC associated with the specified name
     * @throws IllegalArgumentException if no NPC exists with the given name
     */
    Npc mustGet(String name);

    /**
     * Retrieves an NPC by its entity ID from the registry.
     *
     * @param entityId the entity ID of the NPC to retrieve
     * @return an {@code Optional} containing the NPC if found, or an empty {@code Optional} if no NPC exists with the specified entity ID
     */
    Optional<Npc> getByEntityId(int entityId);

    /**
     * Retrieves all NPCs currently registered in the registry.
     *
     * @return a collection containing all registered NPCs
     */
    Collection<Npc> getAll();

    /**
     * Retrieves all persistent NPCs currently registered in the registry.
     * Persistent NPCs are those that should be saved to storage.
     *
     * @return a collection containing all persistent NPCs
     */
    Collection<Npc> getAllPersistent();

    /**
     * Removes all NPCs from the registry, effectively clearing its contents.
     */
    void clear();
}
