package com.fancyinnovations.fancynpcs;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.NpcData;
import com.fancyinnovations.fancynpcs.api.NpcManager;
import com.fancyinnovations.fancynpcs.api.NpcStorage;
import com.fancyinnovations.fancynpcs.api.events.NpcsLoadedEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class NpcManagerImpl implements NpcManager {

    private final JavaPlugin plugin;
    private final ExtendedFancyLogger logger;
    private final Function<NpcData, Npc> npcAdapter;
    private final Map<String, Npc> npcs; // npc id -> npc
    private NpcStorage storage;
    private boolean isLoaded;

    public NpcManagerImpl(JavaPlugin plugin, Function<NpcData, Npc> npcAdapter, NpcStorage storage) {
        this.plugin = plugin;
        this.logger = FancyNpcs.getInstance().getFancyLogger();
        this.npcAdapter = npcAdapter;
        this.storage = storage;
        this.npcs = new ConcurrentHashMap<>();
        this.isLoaded = false;
    }

    /**
     * Sets the storage implementation.
     *
     * @param storage The new storage implementation.
     */
    public void setStorage(NpcStorage storage) {
        this.storage = storage;
    }

    public void registerNpc(Npc npc) {
        if (!FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && getAllNpcs().stream().anyMatch(npc1 -> npc1.getData().getName().equals(npc.getData().getName()))) {
            throw new IllegalStateException("An NPC with this name already exists");
        } else {
            // Set default file path if not already set (one file per NPC, named after the NPC)
            if (npc.getData().getFilePath() == null || npc.getData().getFilePath().isEmpty()) {
                npc.getData().setFilePath(npc.getData().getName());
            }
            npcs.put(npc.getData().getId(), npc);
        }
    }

    public void removeNpc(Npc npc) {
        npcs.remove(npc.getData().getId());
        storage.delete(npc);
    }

    @ApiStatus.Internal
    @Override
    public Npc getNpc(int entityId) {
        for (Npc npc : getAllNpcs()) {
            if (npc.getEntityId() == entityId) {
                return npc;
            }
        }

        return null;
    }

    @Override
    public Npc getNpc(String name) {
        for (Npc npc : getAllNpcs()) {
            if (npc.getData().getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }

        return null;
    }

    @Override
    public Npc getNpcById(String id) {
        for (Npc npc : getAllNpcs()) {
            if (npc.getData().getId().equals(id)) {
                return npc;
            }
        }

        return null;
    }

    @Override
    public Npc getNpc(String name, UUID creator) {
        for (Npc npc : getAllNpcs()) {
            if (npc.getData().getCreator().equals(creator) && npc.getData().getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }

        return null;
    }

    public Collection<Npc> getAllNpcs() {
        return new ArrayList<>(npcs.values());
    }

    public void saveNpcs(boolean force) {
        if (!isLoaded) {
            return;
        }

        // Filter NPCs that should be saved
        Collection<Npc> npcsToSave = getAllNpcs().stream()
                .filter(Npc::isSaveToFile)
                .toList();

        storage.saveBatch(npcsToSave, force);
    }

    public void loadNpcs() {
        npcs.clear();

        Collection<Npc> loadedNpcs = storage.loadAll();
        for (Npc npc : loadedNpcs) {
            registerNpc(npc);
        }

        this.setLoaded();
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    private void setLoaded() {
        isLoaded = true;
        new NpcsLoadedEvent().callEvent();
    }

    public void reloadNpcs() {
        Collection<Npc> npcCopy = new ArrayList<>(getAllNpcs());
        npcs.clear();
        for (Npc npc : npcCopy) {
            npc.removeForAll();
        }

        loadNpcs();
    }
}