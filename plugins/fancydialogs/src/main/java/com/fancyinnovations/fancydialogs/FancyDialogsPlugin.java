package com.fancyinnovations.fancydialogs;

import com.fancyinnovations.fancydialogs.actions.ActionRegistryImpl;
import com.fancyinnovations.fancydialogs.analytics.Analytics;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.DialogActionRegistry;
import com.fancyinnovations.fancydialogs.api.FancyDialogs;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.commands.DialogCMD;
import com.fancyinnovations.fancydialogs.commands.FancyDialogsCMD;
import com.fancyinnovations.fancydialogs.commands.QuickActionsCMD;
import com.fancyinnovations.fancydialogs.commands.types.DialogCommandType;
import com.fancyinnovations.fancydialogs.config.FDFeatureFlags;
import com.fancyinnovations.fancydialogs.config.FancyDialogsConfig;
import com.fancyinnovations.fancydialogs.dialog.DialogImpl;
import com.fancyinnovations.fancydialogs.fancynpcs.OpenDialogNpcAction;
import com.fancyinnovations.fancydialogs.joinedplayerscache.JoinedPlayersCache;
import com.fancyinnovations.fancydialogs.listener.DialogButtonClickedListener;
import com.fancyinnovations.fancydialogs.listener.PlayerDeathListener;
import com.fancyinnovations.fancydialogs.listener.PlayerJoinListener;
import com.fancyinnovations.fancydialogs.listener.PlayerQuitListener;
import com.fancyinnovations.fancydialogs.registry.DefaultDialogs;
import com.fancyinnovations.fancydialogs.registry.DialogRegistry;
import com.fancyinnovations.fancydialogs.storage.DialogStorage;
import com.fancyinnovations.fancydialogs.storage.JsonDialogStorage;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
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
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class FancyDialogsPlugin extends JavaPlugin implements FancyDialogs {

    private static FancyDialogsPlugin INSTANCE;
    private final ExtendedFancyLogger fancyLogger;

    private FancyDialogsConfig fdConfig;
    private VersionFetcher versionFetcher;
    private VersionConfig versionConfig;
    private Translator translator;
    private DialogRegistry dialogRegistry;
    private DialogStorage dialogStorage;
    private ActionRegistryImpl actionRegistry;
    private JoinedPlayersCache joinedPlayersCache;

    public FancyDialogsPlugin() {
        INSTANCE = this;

        Appender consoleAppender = new ConsoleAppender("[{loggerName}] ({threadName}) {logLevel}: {message}");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()));
        File logsFile = new File("plugins/FancyDialogs/logs/FD-logs-" + date + ".txt");
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
                "FancyDialogs",
                LogLevel.INFO,
                List.of(consoleAppender, jsonAppender),
                List.of(new PluginMiddleware(this))
        );
    }

    public static FancyDialogsPlugin get() {
        return INSTANCE;
    }

    @Override
    public void onLoad() {
        fdConfig = new FancyDialogsConfig();
        fdConfig.load();

        if (!fdConfig.getLogLevel().equalsIgnoreCase("INFO")) {
            fancyLogger.setCurrentLevel(LogLevel.valueOf(fdConfig.getLogLevel().toUpperCase()));
        }

        FDFeatureFlags.load();

        if (FDFeatureFlags.DEBUG_MODE.isEnabled()) {
            fancyLogger.setCurrentLevel(LogLevel.DEBUG);
            IFancySitula.LOGGER.setCurrentLevel(LogLevel.DEBUG);
        }

        translator = new Translator(
                new TextConfig(
                        "#ffcc24", // color to highlight important information
                        "gray", // text color for regular messages
                        "#81E366",
                        "#E3CA66",
                        "#E36666",
                        "<color:#ba8813>[</color><gradient:#ffae00:#fffb00:#ffae00>FancyDialogs</gradient><color:#ba8813>]</color> <gray>"
                )
        );
        translator.loadLanguages(getDataFolder().getAbsolutePath());
        final Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageName().equals(fdConfig.getLanguage()))
                .findFirst().orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);

        versionFetcher = new MasterVersionFetcher(getName());
        versionConfig = new VersionConfig(this, versionFetcher);
        versionConfig.load();

        dialogStorage = new JsonDialogStorage();
        Collection<DialogData> dialogData = dialogStorage.loadAll();

        List<Dialog> dialogs = new ArrayList<>();
        for (DialogData data : dialogData) {
            Dialog dialog = new DialogImpl(data.id(), data);
            dialogs.add(dialog);
            fancyLogger.debug("Loaded dialog: %s".formatted(data.id()));
        }

        dialogRegistry = new DialogRegistry();
        dialogs.forEach(dialogRegistry::register);

        DefaultDialogs.registerDefaultDialogs();

        actionRegistry = new ActionRegistryImpl();

        joinedPlayersCache = new JoinedPlayersCache();
        joinedPlayersCache.load();

        fancyLogger.info("Successfully loaded FancyDialogs version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
        final ComparableVersion currentVersion = new ComparableVersion(versionConfig.getVersion());
        supplyAsync(versionFetcher::fetchNewestVersion)
                .thenApply(Objects::requireNonNull)
                .whenComplete((newest, error) -> {
                    if (error != null || newest.compareTo(currentVersion) <= 0) {
                        return; // could not get the newest version or already on latest
                    }

                    fancyLogger.warn("You are not using the latest version of the FancyDialogs plugin.");
                    getLogger().warning("""
                            
                            -------------------------------------------------------
                            You are not using the latest version of the FancyDialogs plugin.
                            Please update to the newest version (%s).
                            %s
                            -------------------------------------------------------
                            """.formatted(newest, versionFetcher.getDownloadUrl()));
                });

        if (!ServerSoftware.isPaper()) {
            fancyLogger.warn("""
                    --------------------------------------------------
                    It is recommended to use Paper as server software.
                    Because you are not using paper, the plugin
                    might not work correctly.
                    --------------------------------------------------
                    """);
        }

        String version = Bukkit.getMinecraftVersion();
        if (ServerVersion.getByVersion(version).getProtocolVersion() < ServerVersion.v1_21_6.getProtocolVersion()) {
            fancyLogger.error("""
                    --------------------------------------------------
                    FancyDialogs requires Minecraft version 1.21.6 or higher.
                    Your server is running version %s, which is not supported.
                    Please update your server to the latest version.
                    --------------------------------------------------
                    """.formatted(version));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        registerListeners();

        registerCommands();

        // FancyNpcs actions
        if (Bukkit.getPluginManager().isPluginEnabled("FancyNpcs")) {
            new OpenDialogNpcAction().register();
        }

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        Analytics analytics = new Analytics();
        analytics.start();

        fancyLogger.info("Successfully enabled FancyDialogs version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        for (Dialog dialog : dialogRegistry.getAll()) {
            // Close all viewers of the dialog
            for (UUID viewer : dialog.getViewers()) {
                Player player = Bukkit.getPlayer(viewer);
                if (player != null && player.isOnline()) {
                    dialog.close(player);
                }
            }

            dialogStorage.save(dialog.getData());
        }

        fancyLogger.info("Successfully disabled FancyDialogs version %s".formatted(getDescription().getVersion()));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerQuitListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(), this);
        Bukkit.getPluginManager().registerEvents(new DialogButtonClickedListener(), this);
    }

    private void registerCommands() {
        Lamp.Builder<BukkitCommandActor> lampBuilder = BukkitLamp
                .builder(this);

        lampBuilder.parameterTypes(builder -> {
            builder.addParameterType(Dialog.class, DialogCommandType.INSTANCE);
        });

        lampBuilder.exceptionHandler(DialogCommandType.INSTANCE);

        Lamp<BukkitCommandActor> lamp = lampBuilder.build();

        lamp.register(FancyDialogsCMD.INSTANCE);
        lamp.register(DialogCMD.INSTANCE);
        if (!FDFeatureFlags.DISABLE_QUICK_ACTIONS_DIALOG.isEnabled()) {
            lamp.register(QuickActionsCMD.INSTANCE);
        }
    }

    @Override
    public Dialog createDialog(DialogData data) {
        return new DialogImpl(data.id(), data);
    }

    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    public Translator getTranslator() {
        return translator;
    }

    public FancyDialogsConfig getFancyDialogsConfig() {
        return fdConfig;
    }

    public VersionConfig getVersionConfig() {
        return versionConfig;
    }

    @Override
    public DialogRegistry getDialogRegistry() {
        return dialogRegistry;
    }

    @Override
    public DialogActionRegistry getDialogActionRegistry() {
        return actionRegistry;
    }

    public DialogStorage getDialogStorage() {
        return dialogStorage;
    }

    public ActionRegistryImpl getActionRegistry() {
        return actionRegistry;
    }

    public JoinedPlayersCache getJoinedPlayersCache() {
        return joinedPlayersCache;
    }
}
