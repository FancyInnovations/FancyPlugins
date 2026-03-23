package com.fancyinnovations.fancyworlds.main;

import com.fancyinnovations.fancyworlds.api.FancyWorlds;
import com.fancyinnovations.fancyworlds.api.FancyWorldsConfig;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.api.worlds.WorldStorage;
import com.fancyinnovations.fancyworlds.commands.fancyworlds.FWConfigCMD;
import com.fancyinnovations.fancyworlds.commands.fancyworlds.FWVersionCMD;
import com.fancyinnovations.fancyworlds.commands.types.FWorldCommandType;
import com.fancyinnovations.fancyworlds.commands.types.GameruleCommandType;
import com.fancyinnovations.fancyworlds.commands.world.*;
import com.fancyinnovations.fancyworlds.config.FancyWorldsConfigImpl;
import com.fancyinnovations.fancyworlds.listeners.WorldLoadListener;
import com.fancyinnovations.fancyworlds.listeners.WorldUnloadListener;
import com.fancyinnovations.fancyworlds.metrics.FWMetrics;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.fancyinnovations.fancyworlds.worlds.service.WorldServiceImpl;
import com.fancyinnovations.fancyworlds.worlds.storage.json.JsonWorldStorage;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.logging.PluginMiddleware;
import de.oliver.fancylib.translations.Language;
import de.oliver.fancylib.translations.TextConfig;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.versionFetcher.FancySpacesVersionFetcher;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class FancyWorldsPlugin extends JavaPlugin implements FancyWorlds {

    private static FancyWorldsPlugin INSTANCE;
    private final ExtendedFancyLogger fancyLogger;

    private FancyWorldsConfigImpl fancyWorldsConfig;
    private VersionFetcher versionFetcher;
    private VersionConfig versionConfig;
    private Translator translator;
    private FWMetrics metrics;

    private WorldStorage worldStorage;
    private WorldService worldService;

    public FancyWorldsPlugin() {
        INSTANCE = this;

        Appender consoleAppender = new ConsoleAppender("[{loggerName}] ({threadName}) {logLevel}: {message}");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        File logsFile = new File("plugins/FancyWorlds/logs/FW-logs-" + date + ".txt");
        if (!logsFile.exists()) {
            try {
                logsFile.getParentFile().mkdirs();
                logsFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JsonAppender jsonAppender = new JsonAppender(false, false, true, logsFile.getPath());
        this.fancyLogger = new ExtendedFancyLogger(
                "FancyWorlds",
                LogLevel.INFO,
                List.of(consoleAppender, jsonAppender),
                List.of(new PluginMiddleware(this))
        );
    }

    public static FancyWorldsPlugin get() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        fancyLogger.info("Loading FancyWorlds version %s...".formatted(getDescription().getVersion()));

        // Config
        fancyWorldsConfig = new FancyWorldsConfigImpl();
        fancyWorldsConfig.init();
        fancyWorldsConfig.reload();

        LogLevel logLevel;
        try {
            logLevel = LogLevel.valueOf(fancyWorldsConfig.getLogLevel());
        } catch (IllegalArgumentException e) {
            logLevel = LogLevel.INFO;
        }
        fancyLogger.setCurrentLevel(logLevel);

        // Version checking
        versionFetcher = new FancySpacesVersionFetcher("FancyWorlds");
        versionConfig = new VersionConfig(this, versionFetcher);
        versionConfig.load();

        // Translator
        registerTranslator();

        // Metrics
        metrics = new FWMetrics();

        // Services
        worldStorage = new JsonWorldStorage();
        worldService = new WorldServiceImpl(worldStorage);

        fancyLogger.info("Successfully loaded FancyWorlds version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        fancyLogger.info("Enabling FancyWorlds version %s...".formatted(getDescription().getVersion()));

        if (!fancyWorldsConfig.areVersionNotificationsMuted()) {
            checkForNewerVersion();
        }
        if (versionConfig.isDevelopmentBuild()) {
            fancyLogger.warn("""
                    
                    --------------------------------------------------
                    You are using a development build of FancyWorlds.
                    Please be aware that there might be bugs in this version.
                    If you find any bugs, please report them on our discord server (https://discord.gg/ZUgYCEJUEx).
                    Read more about the risks of using a development build here: https://fancyinnovations.com/docs/general/development-guidelines/versioning#build
                    --------------------------------------------------
                    """);
        }

        Bukkit.getScheduler().runTaskLaterAsynchronously(INSTANCE, () -> {
            if (!Bukkit.getPluginManager().isPluginEnabled("FancyDialogs")) {
                fancyLogger.error("""
                        
                        --------------------------------------------------
                        The FancyDialogs plugin is required for FancyWorlds to work properly.
                        Please install the FancyDialogs plugin and restart the server.
                        You can download the plugin here: https://modrinth.com/plugin/fancydialogs/versions?c=release
                        --------------------------------------------------
                        """);
                Bukkit.getPluginManager().disablePlugin(this);
                return;
            }
        }, 20L * 20); // 20s

        for (FWorld world : worldService.getAllWorlds()) {
            if (world.isWorldLoaded()) {
                continue;
            }

            fancyLogger.info("Loading world %s...".formatted(world.getName()));

            FWorldImpl impl = (FWorldImpl) world;
            World bukkitWorld = impl.toWorldCreator().createWorld();
            impl.setBukkitWorld(bukkitWorld);
        }

        registerCommands();

        registerListeners();

        metrics.register();
        metrics.checkIfPluginVersionUpdated();

        fancyLogger.info("Successfully enabled FancyWorlds version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        fancyLogger.info("Disabling FancyWorlds version %s...".formatted(getDescription().getVersion()));

        fancyLogger.info("Successfully disabled FancyWorlds version %s".formatted(getDescription().getVersion()));
    }

    private void registerCommands() {
        Lamp.Builder<BukkitCommandActor> lampBuilder = BukkitLamp
                .builder(this);

        // parameter types
        lampBuilder.parameterTypes(builder -> {
            builder.addParameterType(FWorld.class, FWorldCommandType.INSTANCE);
            builder.addParameterType(GameRule.class, GameruleCommandType.INSTANCE);
        });

        // exception handlers
        lampBuilder.exceptionHandler(FWorldCommandType.INSTANCE);
        lampBuilder.exceptionHandler(GameruleCommandType.INSTANCE);

        Lamp<BukkitCommandActor> lamp = lampBuilder.build();

        // fancyworlds commands
        lamp.register(FWVersionCMD.INSTANCE);
        lamp.register(FWConfigCMD.INSTANCE);

        // world commands
        lamp.register(WorldHelpCMD.INSTANCE);
        lamp.register(WorldListCMD.INSTANCE);
        lamp.register(WorldLinkCMD.INSTANCE);
        lamp.register(WorldUnlinkCMD.INSTANCE);
        lamp.register(WorldCreateCMD.INSTANCE);
        lamp.register(WorldTeleportCMD.INSTANCE);
        lamp.register(WorldLoadCMD.INSTANCE);
        lamp.register(WorldDeleteCMD.INSTANCE);
        lamp.register(WorldUnloadCMD.INSTANCE);
        lamp.register(WorldGamerulesCMD.INSTANCE);
        lamp.register(WorldTimeCMD.INSTANCE);
        lamp.register(WorldSetSpawnCMD.INSTANCE);
        lamp.register(WorldDifficultyCMD.INSTANCE);

        // Other
        lamp.register(WorldSeedCMD.INSTANCE);
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new WorldLoadListener(), this);
        Bukkit.getPluginManager().registerEvents(new WorldUnloadListener(), this);
    }

    public void registerTranslator() {
        translator = new Translator(
                new TextConfig(
                        "#ffcc24", // color to highlight important information
                        "gray", // text color for regular messages
                        "#81E366",
                        "#E3CA66",
                        "#E36666",
                        "<color:#ba8813>[</color><gradient:#ffae00:#fffb00:#ffae00>FancyWorlds</gradient><color:#ba8813>]</color> <gray>"
                )
        );

        translator.loadLanguages(getDataFolder().getAbsolutePath());
        Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageName().equals(fancyWorldsConfig.getLanguage()))
                .findFirst()
                .orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);
    }

    private void checkForNewerVersion() {
        final var current = new ComparableVersion(versionConfig.getVersion());

        supplyAsync(getVersionFetcher()::fetchNewestVersion).thenApply(Objects::requireNonNull).whenComplete((newest, error) -> {
            if (error != null || newest.compareTo(current) <= 0) {
                return; // could not get the newest version or already on latest
            }

            fancyLogger.warn("""
                    
                    -------------------------------------------------------
                    You are not using the latest version of the FancyWorlds plugin.
                    Please update to the newest version (%s).
                    %s
                    -------------------------------------------------------
                    """.formatted(newest, getVersionFetcher().getDownloadUrl()));
        });
    }

    @Override
    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    @Override
    public FancyWorldsConfig getFancyWorldsConfig() {
        return fancyWorldsConfig;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public VersionConfig getVersionConfig() {
        return versionConfig;
    }

    public Translator getTranslator() {
        return translator;
    }

    @Override
    public WorldStorage getWorldStorage() {
        return worldStorage;
    }

    @Override
    public WorldService getWorldService() {
        return worldService;
    }
}
