package com.fancyinnovations.fancyworlds.api.worlds;

import java.util.Collection;

public interface WorldStorage {

    void storeWorld(FWorld world);

    Collection<FWorld> getAllWorlds();

    void deleteWorld(String id);

}
