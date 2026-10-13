package com.fancyinnovations.fancynpcs.registry;

import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.NpcRegistry;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class NpcRegistryImpl implements NpcRegistry {

    private final Map<String, Npc> npcsByName;
    private final Map<Integer, Npc> npcsByEntityId;

    public NpcRegistryImpl() {
        this.npcsByName = new ConcurrentHashMap<>();
        this.npcsByEntityId = new ConcurrentHashMap<>();
    }

    @Override
    public boolean register(Npc npc) {
        if (npc == null || npc.getData() == null || npc.getData().getName() == null) {
            return false;
        }

        String name = npc.getData().getName();

        // Use putIfAbsent to ensure thread-safe registration
        Npc existing = npcsByName.putIfAbsent(name, npc);

        if (existing == null) {
            // Successfully registered by name, now register by entity ID
            npcsByEntityId.put(npc.getEntityId(), npc);
            return true;
        }

        // Registration failed because an NPC with this name already exists
        return false;
    }

    @Override
    public boolean unregister(Npc npc) {
        if (npc == null || npc.getData() == null || npc.getData().getName() == null) {
            return false;
        }

        String name = npc.getData().getName();

        // Remove from both maps
        boolean removedByName = npcsByName.remove(name, npc);
        npcsByEntityId.remove(npc.getEntityId());

        return removedByName;
    }

    @Override
    public boolean contains(String name) {
        if (name == null) {
            return false;
        }
        return npcsByName.containsKey(name);
    }

    @Override
    public Optional<Npc> get(String name) {
        if (name == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(npcsByName.get(name));
    }

    @Override
    public Npc mustGet(String name) {
        if (!contains(name)) {
            throw new IllegalArgumentException("NPC with name " + name + " does not exist!");
        }

        return npcsByName.get(name);
    }

    @Override
    public Optional<Npc> getByEntityId(int entityId) {
        return Optional.ofNullable(npcsByEntityId.get(entityId));
    }

    @Override
    public Collection<Npc> getAll() {
        return Collections.unmodifiableCollection(npcsByName.values());
    }

    @Override
    public Collection<Npc> getAllPersistent() {
        return getAll()
                .stream()
                .filter(Npc::isSaveToFile)
                .toList();
    }

    @Override
    public void clear() {
        npcsByName.clear();
        npcsByEntityId.clear();
    }
}
