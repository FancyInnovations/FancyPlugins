package com.fancyinnovations.fancyworlds.api.portals;

import java.util.Collection;

public interface PortalStorage {

    void storePortal(FPortal portal);

    Collection<FPortal> getAllPortals();

    void deletePortal(String id);
}
