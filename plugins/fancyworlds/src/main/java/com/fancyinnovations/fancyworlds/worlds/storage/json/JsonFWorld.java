package com.fancyinnovations.fancyworlds.worlds.storage.json;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.google.gson.annotations.SerializedName;
import org.bukkit.World;

import java.util.UUID;

public record JsonFWorld(
        UUID id,
        long seed,
        World.Environment environment,
        String generator,
        @SerializedName("generate_structures") boolean generateStructures,
        String name
) {
    public static JsonFWorld fromFWorld(FWorld fWorld) {
        return new JsonFWorld(
                fWorld.getID(),
                fWorld.getSeed(),
                fWorld.getEnvironment(),
                fWorld.getGenerator(),
                fWorld.canGenerateStructures(),
                fWorld.getName()
        );
    }

    public FWorld toFWorld() {
        return new FWorldImpl(
                id,
                name,
                seed,
                environment,
                generator,
                generateStructures,
                null // Settings are not stored in JSON, so we pass null
        );
    }
}
