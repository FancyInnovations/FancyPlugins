package com.fancyinnovations.fancyworlds.api.portals;

import com.fancyinnovations.fancyworlds.api.FancyWorlds;

import java.util.Collection;

public interface PortalService {

    static PortalService get() {
        return FancyWorlds.get().getPortalService();
    }

    void registerPortal(FPortal portal);

    void updatePortal(FPortal portal);

    void unregisterPortal(FPortal portal);

    FPortal getPortalByID(String id);

    FPortal getPortalByName(String name);

    FPortal getPortalAt(String worldName, int x, int y, int z);

    Collection<FPortal> getPortalsInWorld(String worldName);

    Collection<FPortal> getAllPortals();
}
