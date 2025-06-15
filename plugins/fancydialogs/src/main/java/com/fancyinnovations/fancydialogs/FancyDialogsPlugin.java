package com.fancyinnovations.fancydialogs;

import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.commands.TutorialCMD;
import com.fancyinnovations.fancydialogs.config.FDFeatureFlags;
import com.fancyinnovations.fancydialogs.config.FancyDialogsConfig;
import com.fancyinnovations.fancydialogs.dialog.DialogImpl;
import com.fancyinnovations.fancydialogs.listener.PlayerJoinListener;
import com.fancyinnovations.fancydialogs.registry.DefaultDialogs;
import com.fancyinnovations.fancydialogs.registry.DialogRegistry;
import com.fancyinnovations.fancydialogs.storage.DialogStorage;
import com.fancyinnovations.fancydialogs.storage.JsonDialogStorage;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.translations.Language;
import de.oliver.fancylib.translations.TextConfig;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancysitula.api.utils.ServerVersion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import revxrsal.commands.Lamp;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class FancyDialogsPlugin extends JavaPlugin {

    private static FancyDialogsPlugin INSTANCE;
    private final ExtendedFancyLogger fancyLogger;

    private FancyDialogsConfig fdConfig;
    private Translator translator;
    private DialogRegistry dialogRegistry;
    private DialogStorage dialogStorage;

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
        this.fancyLogger = new ExtendedFancyLogger("FancyDialogs", LogLevel.INFO, List.of(consoleAppender, jsonAppender), new ArrayList<>());
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
        }

        translator = new Translator(new TextConfig("#32e347", "#35ad1d", "#81E366", "#E3CA66", "#E36666", ""));
        translator.loadLanguages(getDataFolder().getAbsolutePath());
        final Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageName().equals(fdConfig.getLanguage()))
                .findFirst().orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);

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

        fancyLogger.info("Successfully loaded FancyDialogs version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onEnable() {
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
        if (ServerVersion.getByVersion(version).getProtocolVersion() < 771) {
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

        Lamp<BukkitCommandActor> lamp = BukkitLamp
                .builder(this)
                .build();
        lamp.register(TutorialCMD.INSTANCE);

        fancyLogger.info("Successfully enabled FancyDialogs version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        for (Dialog dialog : dialogRegistry.getAll()) {
            dialogStorage.save(dialog.getData());
        }

        fancyLogger.info("Successfully disabled FancyDialogs version %s".formatted(getDescription().getVersion()));
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), this);
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

    public DialogRegistry getDialogRegistry() {
        return dialogRegistry;
    }

    public DialogStorage getDialogStorage() {
        return dialogStorage;
    }
}
