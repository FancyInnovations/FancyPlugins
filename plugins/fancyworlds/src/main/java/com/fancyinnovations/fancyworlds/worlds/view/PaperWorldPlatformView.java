package com.fancyinnovations.fancyworlds.worlds.view;

import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.papermc.paper.FeatureHooks;
import io.papermc.paper.world.PaperWorldLoader;
import net.kyori.adventure.key.Key;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.Main;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.ai.village.VillageSiege;
import net.minecraft.world.entity.npc.CatSpawner;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTraderSpawner;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.levelgen.PatrolSpawner;
import net.minecraft.world.level.levelgen.PhantomSpawner;
import net.minecraft.world.level.levelgen.WorldDimensions;
import net.minecraft.world.level.levelgen.WorldOptions;
import net.minecraft.world.level.storage.LevelDataAndDimensions;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraft.world.level.storage.PrimaryLevelData;
import net.minecraft.world.level.validation.ContentValidationException;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PaperWorldPlatformView implements WorldPlatformView {

    private static final Key OVERWORLD = Key.key("overworld");
    private final FancyWorldsPlugin plugin;

    public PaperWorldPlatformView(FancyWorldsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public CompletableFuture<World> createWorld(FWorldImpl world) {
        if (shouldUseLegacyCreator(world)) {
            return createLegacyWorld(world);
        }
        return plugin.supplyGlobal(() -> createWorldInternal(world));
    }

    @Override
    public CompletableFuture<Boolean> unloadWorld(World world, boolean save) {
        return saveLevelDataAsync(world).thenCompose(ignored -> {
            plugin.getServer().allowPausing(plugin, false);
            return plugin.supplyGlobal(() -> {
                final var dragonBattle = world.getEnderDragonBattle();
                if (!plugin.getServer().unloadWorld(world, save)) {
                    plugin.getServer().allowPausing(plugin, true);
                    return CompletableFuture.completedFuture(false);
                }
                if (dragonBattle != null) {
                    dragonBattle.getBossBar().removeAll();
                }
                plugin.getServer().allowPausing(plugin, true);
                return CompletableFuture.completedFuture(true);
            });
        }).exceptionally(throwable -> {
            plugin.getFancyLogger().warn("Failed to save level data before unloading world " + world.getName());
            return false;
        });
    }

    @Override
    public boolean isPrimaryWorld(World world) {
        return world.key().equals(OVERWORLD);
    }

    protected CompletableFuture<Void> saveAsync(World world, boolean flush) {
        return plugin.supplyGlobal(() -> {
            try {
                final var level = ((CraftWorld) world).getHandle();
                final var oldSave = level.noSave;
                level.noSave = false;
                level.save(null, flush, false);
                level.noSave = oldSave;
                return CompletableFuture.completedFuture(null);
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }
        }).thenCompose(ignored -> saveLevelDataAsync(world));
    }

    protected CompletableFuture<Void> saveLevelDataAsync(World world) {
        final var level = ((CraftWorld) world).getHandle();
        if (level.getDragonFight() != null) {
            level.serverLevelData.setEndDragonFightData(level.getDragonFight().saveData());
        }

        level.serverLevelData.setCustomBossEvents(level.getServer().getCustomBossEvents().save(level.registryAccess()));
        level.levelStorageAccess.saveDataTag(level.getServer().registryAccess(), level.serverLevelData, level.getServer().getPlayerList().getSingleplayerData());

        return level.getChunkSource().getDataStorage().scheduleSave().thenApply(ignored -> null);
    }

    private CompletableFuture<World> createWorldInternal(FWorldImpl fworld) {
        final var server = (CraftServer) plugin.getServer();
        final var console = server.getServer();
        final var directory = plugin.getServer().getWorldContainer().toPath().resolve(fworld.getName());
        final var environment = resolveEnvironment(fworld, directory);
        final ChunkGenerator chunkGenerator = resolveChunkGenerator(fworld);

        final ResourceKey<LevelStem> levelStemKey;
        try {
            levelStemKey = resolveLevelStem(environment);
        } catch (IllegalArgumentException e) {
            return CompletableFuture.failedFuture(e);
        }

        try {
            Preconditions.checkState(console.getAllLevels().iterator().hasNext(), "Cannot create worlds before the main level is created");
            Preconditions.checkArgument(!Files.exists(directory) || Files.isDirectory(directory), "File (%s) exists and isn't a folder", directory);
            Preconditions.checkArgument(server.getWorld(fworld.getName()) == null, "World with name %s already exists", fworld.getName());
            Preconditions.checkState(plugin.getServer().getWorlds().stream()
                            .map(World::getWorldFolder)
                            .map(File::toPath)
                            .noneMatch(directory::equals),
                    "World with directory %s already exists", directory);
        } catch (RuntimeException e) {
            return CompletableFuture.failedFuture(e);
        }

        final LevelStorageSource.LevelStorageAccess levelStorageAccess;
        try {
            levelStorageAccess = LevelStorageSource.createDefault(directory.getParent())
                    .validateAndCreateAccess(directory.getFileName().toString(), levelStemKey);
        } catch (IOException | ContentValidationException e) {
            return CompletableFuture.failedFuture(e);
        }

        final WorldLoader.DataLoadContext context = console.worldLoaderContext;
        RegistryAccess.Frozen registryAccess = context.datapackDimensions();
        Registry<LevelStem> levelStemRegistry = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);

        final var levelData = PaperWorldLoader.getLevelData(levelStorageAccess);
        if (levelData.fatalError()) {
            return CompletableFuture.failedFuture(new IOException("Failed to read level data for world " + fworld.getName()));
        }

        final var dataTag = levelData.dataTag();
        final PrimaryLevelData primaryLevelData;
        if (dataTag != null) {
            final LevelDataAndDimensions levelDataAndDimensions = LevelStorageSource.getLevelDataAndDimensions(
                    dataTag,
                    context.dataConfiguration(),
                    levelStemRegistry,
                    context.datapackWorldgen()
            );
            primaryLevelData = (PrimaryLevelData) levelDataAndDimensions.worldData();
            registryAccess = levelDataAndDimensions.dimensions().dimensionsRegistryAccess();
        } else {
            final LevelSettings levelSettings = new LevelSettings(
                    fworld.getName(),
                    GameType.byId(server.getDefaultGameMode().getValue()),
                    plugin.getServer().isHardcore(),
                    Difficulty.EASY,
                    false,
                    new GameRules(context.dataConfiguration().enabledFeatures()),
                    context.dataConfiguration()
            );

            final WorldOptions worldOptions = new WorldOptions(fworld.getSeed(), fworld.canGenerateStructures(), false);
            final var properties = new DedicatedServerProperties.WorldDimensionData(
                    createGeneratorSettings(fworld.getGenerator()),
                    resolveGeneratorPresetName(fworld.getGenerator())
            );
            final WorldDimensions.Complete complete = properties.create(context.datapackWorldgen()).bake(levelStemRegistry);

            primaryLevelData = new PrimaryLevelData(
                    levelSettings,
                    worldOptions,
                    complete.specialWorldProperty(),
                    complete.lifecycle().add(context.datapackWorldgen().allRegistriesLifecycle())
            );
            registryAccess = complete.dimensionsRegistryAccess();
        }

        levelStemRegistry = registryAccess.lookupOrThrow(Registries.LEVEL_STEM);
        primaryLevelData.customDimensions = levelStemRegistry;
        primaryLevelData.checkName(fworld.getName());
        primaryLevelData.setModdedInfo(console.getServerModName(), console.getModdedStatus().shouldReportAsModified());

        if (console.options.has("forceUpgrade")) {
            Main.forceUpgrade(
                    levelStorageAccess,
                    primaryLevelData,
                    DataFixers.getDataFixer(),
                    console.options.has("eraseCache"),
                    () -> true,
                    registryAccess,
                    console.options.has("recreateRegionFiles")
            );
        }

        final long seed = BiomeManager.obfuscateSeed(primaryLevelData.worldGenOptions().seed());
        final List<CustomSpawner> spawners = environment == World.Environment.NORMAL
                ? ImmutableList.of(
                new PhantomSpawner(),
                new PatrolSpawner(),
                new CatSpawner(),
                new VillageSiege(),
                new WanderingTraderSpawner(primaryLevelData)
        )
                : ImmutableList.of();

        final LevelStem stem = levelStemRegistry.getValueOrThrow(levelStemKey);
        final WorldInfo worldInfo = new org.bukkit.craftbukkit.generator.CraftWorldInfo(
                primaryLevelData,
                levelStorageAccess,
                environment,
                stem.type().value(),
                stem.generator(),
                console.registryAccess()
        );
        final BiomeProvider biomeProvider = chunkGenerator != null ? chunkGenerator.getDefaultBiomeProvider(worldInfo) : null;

        final ResourceKey<net.minecraft.world.level.Level> dimensionKey = resolveDimensionKey(server, fworld.getName(), environment);
        final ServerLevel serverLevel = new ServerLevel(
                console,
                console.executor,
                levelStorageAccess,
                primaryLevelData,
                dimensionKey,
                stem,
                primaryLevelData.isDebugWorld(),
                seed,
                spawners,
                true,
                console.overworld().getRandomSequences(),
                environment,
                chunkGenerator,
                biomeProvider
        );

        if (server.getWorld(fworld.getName()) == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("World " + fworld.getName() + " was not properly memoized"));
        }

        console.addLevel(serverLevel);
        console.initWorld(serverLevel, primaryLevelData, primaryLevelData.worldGenOptions());
        serverLevel.setSpawnSettings(true);
        FeatureHooks.tickEntityManager(serverLevel);
        console.prepareLevel(serverLevel);

        return CompletableFuture.completedFuture(serverLevel.getWorld());
    }

    private boolean shouldUseLegacyCreator(FWorldImpl world) {
        return world.getEnvironment() == World.Environment.CUSTOM;
    }

    private CompletableFuture<World> createLegacyWorld(FWorldImpl world) {
        return plugin.supplyGlobal(() -> {
            try {
                final World created = world.toWorldCreator().createWorld();
                if (created == null) {
                    return CompletableFuture.failedFuture(new IllegalStateException("Failed to create world " + world.getName() + " via WorldCreator"));
                }
                return CompletableFuture.completedFuture(created);
            } catch (Exception e) {
                return CompletableFuture.failedFuture(e);
            }
        });
    }

    private ChunkGenerator resolveChunkGenerator(FWorldImpl world) {
        if (world.getGenerator() == null) {
            return null;
        }
        if (isBuiltInGenerator(world.getGenerator())) {
            return null;
        }
        return WorldCreator.getGeneratorForName(world.getName(), world.getGenerator(), plugin.getServer().getConsoleSender());
    }

    private boolean isBuiltInGenerator(String generator) {
        return switch (generator.toLowerCase(Locale.ROOT)) {
            case "", "default", "normal", "flat", "amplified", "large_biomes" -> true;
            default -> false;
        };
    }

    private World.Environment resolveEnvironment(FWorldImpl world, Path directory) {
        if (Files.isDirectory(directory.resolve("DIM1")) && !Files.isDirectory(directory.resolve("DIM-1"))) {
            return World.Environment.THE_END;
        }
        if (Files.isDirectory(directory.resolve("DIM-1")) && !Files.isDirectory(directory.resolve("DIM1"))) {
            return World.Environment.NETHER;
        }
        return world.getEnvironment();
    }

    private ResourceKey<LevelStem> resolveLevelStem(World.Environment environment) {
        return switch (environment) {
            case NORMAL -> LevelStem.OVERWORLD;
            case NETHER -> LevelStem.NETHER;
            case THE_END -> LevelStem.END;
            default -> throw new IllegalArgumentException("Environment " + environment + " is not supported");
        };
    }

    private ResourceKey<net.minecraft.world.level.Level> resolveDimensionKey(CraftServer server, String worldName, World.Environment environment) {
        final String levelName = server.getServer().getProperties().levelName;

        if (environment == World.Environment.NETHER && worldName.equals(levelName + "_nether")) {
            return net.minecraft.world.level.Level.NETHER;
        }
        if (environment == World.Environment.THE_END && worldName.equals(levelName + "_the_end")) {
            return net.minecraft.world.level.Level.END;
        }

        return ResourceKey.create(
                Registries.DIMENSION,
                Identifier.fromNamespaceAndPath("fancyworlds", createWorldKey(worldName))
        );
    }

    private String createWorldKey(String worldName) {
        String sanitized = worldName.toLowerCase(Locale.ROOT)
                .replace(" ", "_")
                .replaceAll("[^a-z0-9_\\-./]+", "");
        if (sanitized.isBlank()) {
            sanitized = "world";
        }
        if (sanitized.length() > 48) {
            sanitized = sanitized.substring(0, 48);
        }

        final String uniqueSuffix = UUID.nameUUIDFromBytes(worldName.getBytes(StandardCharsets.UTF_8))
                .toString()
                .replace("-", "")
                .substring(0, 12);
        return sanitized + "_" + uniqueSuffix;
    }

    private JsonObject createGeneratorSettings(String generator) {
        final JsonObject root = new JsonObject();
        root.addProperty("biome", "minecraft:plains");
        root.addProperty("lakes", false);
        root.addProperty("features", false);
        root.addProperty("decoration", false);

        final JsonArray layers = new JsonArray();
        layers.add(createLayer("minecraft:bedrock", 1));
        layers.add(createLayer("minecraft:dirt", 2));
        layers.add(createLayer("minecraft:grass_block", 1));
        root.add("layers", layers);

        if ("flat".equalsIgnoreCase(generator)) {
            final JsonArray structures = new JsonArray();
            structures.add("minecraft:villages");
            root.add("structure_overrides", structures);
        }

        return root;
    }

    private JsonObject createLayer(String block, int height) {
        final JsonObject layer = new JsonObject();
        layer.addProperty("block", block);
        layer.addProperty("height", height);
        return layer;
    }

    private String resolveGeneratorPresetName(String generator) {
        if (generator == null) {
            return "normal";
        }

        return switch (generator.toLowerCase(Locale.ROOT)) {
            case "flat" -> "flat";
            case "amplified" -> "amplified";
            case "large_biomes" -> "large_biomes";
            default -> "normal";
        };
    }
}
