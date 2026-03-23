package com.fancyinnovations.fancyworlds.api.worlds;

import com.fancyinnovations.fancyworlds.api.FancyWorlds;

import java.util.Collection;

public interface WorldService {

    static WorldService get() {
        return FancyWorlds.get().getWorldService();
    }

    void registerWorld(FWorld world);

    void unregisterWorld(FWorld world);

    FWorld getWorldByID(String id);

    FWorld getWorldByName(String name);

    Collection<FWorld> getAllWorlds();

}
