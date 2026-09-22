package com.fancyinnovations.fancyworlds.portals.service;

import com.fancyinnovations.fancyworlds.api.portals.FPortal;
import com.fancyinnovations.fancyworlds.api.portals.PortalService;
import com.fancyinnovations.fancyworlds.api.portals.PortalStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PortalServiceImpl implements PortalService {

    private final PortalStorage storage;
    private final Map<UUID, FPortal> cacheByID;
    private final Map<String, FPortal> cacheByName;
    private final Map<String, Set<FPortal>> cacheByWorld;

    public PortalServiceImpl(PortalStorage storage) {
        this.storage = storage;
        this.cacheByID = new ConcurrentHashMap<>();
        this.cacheByName = new ConcurrentHashMap<>();
        this.cacheByWorld = new ConcurrentHashMap<>();

        for (FPortal portal : storage.getAllPortals()) {
            cache(portal);
        }
    }

    @Override
    public void registerPortal(FPortal portal) {
        FPortal existingPortal = getPortalByName(portal.getName());
        if (existingPortal != null && !existingPortal.getID().equals(portal.getID())) {
            throw new IllegalArgumentException("A portal named '" + portal.getName() + "' is already registered");
        }

        storage.storePortal(portal);
        cache(portal);
    }

    @Override
    public void updatePortal(FPortal portal) {
        storage.storePortal(portal);
        cache(portal);
    }

    @Override
    public void unregisterPortal(FPortal portal) {
        storage.deletePortal(portal.getID().toString());
        cacheByID.remove(portal.getID());
        cacheByName.remove(normalize(portal.getName()), portal);

        Set<FPortal> portalsInWorld = cacheByWorld.get(normalize(portal.getWorldName()));
        if (portalsInWorld != null) {
            portalsInWorld.remove(portal);
            if (portalsInWorld.isEmpty()) {
                cacheByWorld.remove(normalize(portal.getWorldName()), portalsInWorld);
            }
        }
    }

    @Override
    public FPortal getPortalByID(String id) {
        try {
            return cacheByID.get(UUID.fromString(id));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    @Override
    public FPortal getPortalByName(String name) {
        return cacheByName.get(normalize(name));
    }

    @Override
    public FPortal getPortalAt(String worldName, int x, int y, int z) {
        for (FPortal portal : getPortalsInWorld(worldName)) {
            if (portal.contains(worldName, x, y, z)) {
                return portal;
            }
        }
        return null;
    }

    @Override
    public Collection<FPortal> getPortalsInWorld(String worldName) {
        Set<FPortal> portals = cacheByWorld.get(normalize(worldName));
        return portals == null ? Collections.emptyList() : List.copyOf(portals);
    }

    @Override
    public Collection<FPortal> getAllPortals() {
        return List.copyOf(cacheByID.values());
    }

    private void cache(FPortal portal) {
        FPortal previous = cacheByID.put(portal.getID(), portal);
        if (previous != null && previous != portal) {
            cacheByName.remove(normalize(previous.getName()), previous);
            Set<FPortal> previousWorldPortals = cacheByWorld.get(normalize(previous.getWorldName()));
            if (previousWorldPortals != null) {
                previousWorldPortals.remove(previous);
            }
        }

        cacheByName.put(normalize(portal.getName()), portal);
        cacheByWorld.computeIfAbsent(normalize(portal.getWorldName()), ignored -> ConcurrentHashMap.newKeySet())
                .add(portal);
    }

    private String normalize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}
