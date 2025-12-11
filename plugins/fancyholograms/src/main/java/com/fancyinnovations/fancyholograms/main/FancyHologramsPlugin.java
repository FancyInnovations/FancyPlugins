package com.fancyinnovations.fancyholograms.main;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.HologramConfiguration;
import com.fancyinnovations.fancyholograms.api.HologramController;
import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitRegistry;
import com.fancyinnovations.fancyholograms.commands.FancyHologramsCMD;
import com.fancyinnovations.fancyholograms.commands.FancyHologramsTestCMD;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.fancyholograms.ConfigCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.MoveDownCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.MoveUpCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.SwapLinesCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.GlowingCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.TraitCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.types.GlowingColorCommandType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.types.HologramCommandType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.types.TraitCommandType;
import com.fancyinnovations.fancyholograms.config.FHConfiguration;
import com.fancyinnovations.fancyholograms.controller.HologramControllerImpl;
import com.fancyinnovations.fancyholograms.converter.FHConversionRegistry;
import com.fancyinnovations.fancyholograms.hologram.HologramImpl;
import com.fancyinnovations.fancyholograms.listeners.*;
import com.fancyinnovations.fancyholograms.metrics.FHMetrics;
import com.fancyinnovations.fancyholograms.registry.HologramRegistryImpl;
import com.fancyinnovations.fancyholograms.storage.HologramStorage;
import com.fancyinnovations.fancyholograms.storage.StorageMigrator;
import com.fancyinnovations.fancyholograms.storage.json.JsonStorage;
import com.fancyinnovations.fancyholograms.trait.HologramTraitRegistryImpl;
import com.fancyinnovations.fancyholograms.trait.builtin.DebugTrait;
import com.fancyinnovations.fancyholograms.trait.builtin.FileContentTrait;
import com.fancyinnovations.fancyholograms.trait.builtin.InteractionTrait;
import com.fancyinnovations.fancyholograms.trait.builtin.MultiplePagesTrait;
import com.fancyinnovations.fancyholograms.util.PluginUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.logging.PluginMiddleware;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.translations.Language;
import de.oliver.fancylib.translations.TextConfig;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.versionFetcher.MasterVersionFetcher;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import de.oliver.fancysitula.api.IFancySitula;
import de.oliver.fancysitula.api.utils.ServerVersion;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public final class FancyHologramsPlugin extends JavaPlugin implements FancyHolograms {

    private static @Nullable FancyHologramsPlugin INSTANCE;

    private final ExtendedFancyLogger fancyLogger;

    private final FHMetrics metrics;

    private final VersionFetcher versionFetcher;
    private final VersionConfig versionConfig;

    private final ScheduledExecutorService hologramThread;
    private final ExecutorService storageThread;

    private final FHConfiguration configuration;
    private final Translator translator;

    private Function<HologramData, Hologram> hologramFactory;

    private HologramStorage storage;
    private HologramRegistryImpl registry;
    private HologramControllerImpl controller;
    private HologramTraitRegistryImpl traitRegistry;

    public FancyHologramsPlugin() {
        INSTANCE = this;

        Appender consoleAppender = new ConsoleAppender("[{loggerName}] ({threadName}) {logLevel}: {message}");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        File logsFile = new File("plugins/FancyHolograms/logs/FH-logs-" + date + ".txt");
        if (!logsFile.exists()) {
            try {
                logsFile.getParentFile().mkdirs();
                logsFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JsonAppender jsonAppender = new JsonAppender(false, false, true, logsFile.getPath());
        fancyLogger = new ExtendedFancyLogger(
                "FancyHolograms",
                LogLevel.INFO,
                List.of(consoleAppender, jsonAppender),
                List.of(new PluginMiddleware(this))
        );

        versionFetcher = new MasterVersionFetcher("FancyHolograms");
        versionConfig = new VersionConfig(this, versionFetcher);

        metrics = new FHMetrics();

        hologramThread = Executors.newSingleThreadScheduledExecutor(
                new ThreadFactoryBuilder()
                        .setNameFormat("FancyHolograms-Hologram")
                        .build()
        );

        storageThread = Executors.newSingleThreadExecutor(
                new ThreadFactoryBuilder()
                        .setDaemon(true)
                        .setPriority(Thread.MIN_PRIORITY + 1)
                        .setNameFormat("FancyHolograms-Storage")
                        .build()
        );

        configuration = new FHConfiguration();

        translator = new Translator(new TextConfig("#32e347", "#35ad1d", "#81E366", "#E3CA66", "#E36666", ""));
    }

    public static @NotNull FancyHologramsPlugin get() {
        return Objects.requireNonNull(INSTANCE, "plugin is not initialized");
    }

    public static boolean canGet() {
        return INSTANCE != null;
    }

    @Override
    public void onLoad() {
        configuration.init();

        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(configuration.getLogLevel());
        } catch (IllegalArgumentException e) {
            logLevel = LogLevel.INFO;
        }
        fancyLogger.setCurrentLevel(logLevel);
        IFancySitula.LOGGER.setCurrentLevel(logLevel);

        storage = new JsonStorage();
        registry = new HologramRegistryImpl();
        controller = new HologramControllerImpl();
        traitRegistry = new HologramTraitRegistryImpl();

        if (!ServerSoftware.isPaper()) {
            fancyLogger.warn("""
                    --------------------------------------------------
                    It is recommended to use Paper as server software.
                    Because you are not using paper, the plugin
                    might not work correctly.
                    --------------------------------------------------
                    """);
        }

        if (ServerVersion.isVersionSupported(Bukkit.getMinecraftVersion())) {
            hologramFactory = HologramImpl::new;
        } else {
            fancyLogger.warn("""
                    --------------------------------------------------
                    Unsupported minecraft server version.
                    Please update the server to one of (%s).
                    Disabling the FancyHolograms plugin.
                    --------------------------------------------------
                    """.formatted(String.join(" / ", ServerVersion.getSupportedVersions())));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        translator.loadLanguages(getDataFolder().getAbsolutePath());
        final Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageName().equals(configuration.getLanguage()))
                .findFirst().orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);

        fancyLogger.info("Successfully loaded FancyHolograms version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        new FancyLib(INSTANCE);

        registerCommands();
        if (configuration.useLampCommands()) {
            registerLampCommands();
        }

        registerListeners();

        versionConfig.load();
        if (!configuration.areVersionNotificationsMuted()) {
            checkForNewerVersion();
        }
        if (versionConfig.isDevelopmentBuild()) {
            fancyLogger.warn("""
                    
                    --------------------------------------------------
                    You are using a development build of FancyHolograms.
                    Please be aware that there might be bugs in this version.
                    If you find any bugs, please report them on our discord server (https://discord.gg/ZUgYCEJUEx).
                    Read more about the risks of using a development build here: https://docs.fancyinnovations.com/development-guidelines/versioning/#build
                    --------------------------------------------------
                    """);
        }

        metrics.register();
        metrics.registerLegacy();
        metrics.checkIfPluginVersionUpdated();

        traitRegistry.register(DebugTrait.class);
        traitRegistry.register(MultiplePagesTrait.class);
        traitRegistry.register(FileContentTrait.class);
        traitRegistry.register(InteractionTrait.class);

        new StorageMigrator().migrate();

        Collection<HologramData> data = storage.loadAll();
        for (HologramData d : data) {
            Hologram hologram = hologramFactory.apply(d);
            registry.register(hologram);
        }

        controller.initRefreshTask();
        controller.initUpdateTask();

        if (configuration.isAutosaveEnabled()) {
            getHologramThread().scheduleWithFixedDelay(
                    this::savePersistentHolograms,
                    configuration.getAutosaveInterval(),
                    120L, TimeUnit.SECONDS
            );
        }

        FHConversionRegistry.registerBuiltInConverters();

        fancyLogger.info("Successfully enabled FancyHolograms version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        savePersistentHolograms();

        hologramThread.shutdown();
        storageThread.shutdown();

        fancyLogger.info("Successfully disabled FancyHolograms version %s".formatted(getDescription().getVersion()));

        INSTANCE = null;
    }

    private void registerCommands() {
        Collection<Command> commands = Arrays.asList(new HologramCMD(this), new FancyHologramsCMD(this));

        if (configuration.isRegisterCommands()) {
            commands.forEach(command -> getServer().getCommandMap().register("fancyholograms", command));
        } else {
            commands.stream().filter(Command::isRegistered).forEach(command ->
                    command.unregister(getServer().getCommandMap()));
        }

        if (false) {
            FancyHologramsTestCMD fancyHologramsTestCMD = new FancyHologramsTestCMD(this);
            getServer().getCommandMap().register("fancyholograms", fancyHologramsTestCMD);
        }
    }

    private void registerLampCommands() {
        Lamp.Builder<BukkitCommandActor> lampBuilder = BukkitLamp
                .builder(this);

        lampBuilder.parameterTypes(builder -> {
            builder.addParameterType(Hologram.class, HologramCommandType.INSTANCE);
            builder.addParameterType(HologramTraitRegistry.TraitInfo.class, TraitCommandType.INSTANCE);
            builder.addParameterType(de.oliver.fancylib.colors.GlowingColor.class, GlowingColorCommandType.INSTANCE);
        });

        lampBuilder
                .exceptionHandler(HologramCommandType.INSTANCE)
                .exceptionHandler(TraitCommandType.INSTANCE)
                .exceptionHandler(GlowingColorCommandType.INSTANCE);

        Lamp<BukkitCommandActor> lamp = lampBuilder.build();

        // fancyholograms commands
        lamp.register(ConfigCMD.INSTANCE);

        // hologram commands
        lamp.register(TraitCMD.INSTANCE);
        lamp.register(MoveUpCMD.INSTANCE);
        lamp.register(MoveDownCMD.INSTANCE);
        lamp.register(SwapLinesCMD.INSTANCE);
        lamp.register(GlowingCMD.INSTANCE);
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldLoadedListener(), this);

        if (Set.of("1.21.4", "1.21.5", "1.21.6").contains(Bukkit.getMinecraftVersion())) {
            getServer().getPluginManager().registerEvents(new PlayerLoadedListener(), this);
        }

        if (PluginUtils.isFancyNpcsEnabled()) {
            getServer().getPluginManager().registerEvents(new NpcListener(this), this);
        }

        if (configuration.isHologramsForBedrockPlayersEnabled() && PluginUtils.isFloodgateEnabled()) {
            getServer().getPluginManager().registerEvents(new BedrockPlayerListener(), this);
        }
    }

    private void checkForNewerVersion() {
        final var current = new ComparableVersion(versionConfig.getVersion());

        supplyAsync(getVersionFetcher()::fetchNewestVersion).thenApply(Objects::requireNonNull).whenComplete((newest, error) -> {
            if (error != null || newest.compareTo(current) <= 0) {
                return; // could not get the newest version or already on latest
            }

            fancyLogger.warn("""
                    
                    -------------------------------------------------------
                    You are not using the latest version of the FancyHolograms plugin.
                    Please update to the newest version (%s).
                    %s
                    -------------------------------------------------------
                    """.formatted(newest, getVersionFetcher().getDownloadUrl()));
        });
    }

    public void savePersistentHolograms() {
        List<HologramData> toSave = registry.getAllPersistent()
                .stream()
                .map(Hologram::getData)
                .toList();

        storage.saveBatch(toSave);
    }

    @Override
    public JavaPlugin getPlugin() {
        return INSTANCE;
    }

    @Override
    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    public FHMetrics getMetrics() {
        return metrics;
    }

    public @NotNull VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public @NotNull VersionConfig getVersionConfig() {
        return versionConfig;
    }

    @Override
    public HologramController getController() {
        return controller;
    }

    public HologramControllerImpl getControllerImpl() {
        return controller;
    }


    @Override
    public HologramRegistry getRegistry() {
        return registry;
    }

    @Override
    public HologramTraitRegistryImpl getTraitRegistry() {
        return traitRegistry;
    }

    @Override
    public HologramConfiguration getHologramConfiguration() {
        return configuration;
    }

    @Override
    public Function<HologramData, Hologram> getHologramFactory() {
        return hologramFactory;
    }

    public HologramStorage getStorage() {
        return storage;
    }

    public ScheduledExecutorService getHologramThread() {
        return hologramThread;
    }

    public ExecutorService getStorageThread() {
        return this.storageThread;
    }

    public FHConfiguration getFHConfiguration() {
        return configuration;
    }

    public Translator getTranslator() {
        return translator;
    }
}
