package com.fancyinnovations.fancyworlds.worlds;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.FWorldSettings;
import com.fancyinnovations.fancyworlds.utils.WorldFileUtils;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class FWorldImpl implements FWorld {

    private final UUID id;
    private long seed;
    private final World.Environment environment;
    private final String generator;
    private final boolean generateStructures;
    private final String name;
    private FWorldSettings settings;
    private World bukkitWorld;

    public FWorldImpl(
            @Nullable UUID id,
            @NotNull String name,
            @Nullable Long seed,
            @Nullable World.Environment environment,
            @Nullable String generator,
            @Nullable Boolean generateStructures,
            @Nullable FWorldSettings settings
    ) {
        this.id = id != null ? id : UUID.randomUUID();
        this.name = name;
        this.seed = seed != null ? seed : ThreadLocalRandom.current().nextLong();
        this.environment = environment != null ? environment : World.Environment.NORMAL;
        this.generator = generator != null ? generator : "default";
        this.generateStructures = generateStructures != null ? generateStructures : true;
        this.settings = settings != null ? settings : new FWorldSettingsImpl();
    }

    public FWorldImpl(@NotNull String name) {
        this(null, name, null, null, null, null, null);
    }

    @Override
    public UUID getID() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void rename(String newName) {
        throw new UnsupportedOperationException("Renaming worlds is not supported");
    }

    @Override
    public long getSeed() {
        return seed;
    }

    @Override
    public World.Environment getEnvironment() {
        return environment;
    }

    @Override
    public String getGenerator() {
        return generator;
    }

    @Override
    public boolean canGenerateStructures() {
        return generateStructures;
    }

    @Override
    public FWorldSettings getSettings() {
        return settings;
    }

    @Override
    public void setSettings(FWorldSettings settings) {
        this.settings = settings;
    }

    @Override
    public boolean isWorldLoaded() {
        return bukkitWorld != null;
    }

    @Override
    public boolean isWorldOnDisk() {
        return WorldFileUtils.isWorldOnDisk(name);
    }

    @Override
    public World getBukkitWorld() {
        return bukkitWorld;
    }

    public void setBukkitWorld(World bukkitWorld) {
        this.bukkitWorld = bukkitWorld;
        if (bukkitWorld != null) {
            this.seed = bukkitWorld.getSeed();
        }
    }

    public WorldCreator toWorldCreator() {
        WorldCreator creator = new WorldCreator(name);
        creator.seed(seed);
        creator.environment(environment);
        if (!generator.equalsIgnoreCase("default")) {
            creator.generator(generator);
        }
        creator.generateStructures(generateStructures);

        if (generator.equalsIgnoreCase("flat")) {
            creator.type(WorldType.FLAT);
        } else if (generator.equalsIgnoreCase("amplified")) {
            creator.type(WorldType.AMPLIFIED);
        } else if (generator.equalsIgnoreCase("large_biomes")) {
            creator.type(WorldType.LARGE_BIOMES);
        } else {
            creator.type(WorldType.NORMAL);
        }

        return creator;
    }
}
