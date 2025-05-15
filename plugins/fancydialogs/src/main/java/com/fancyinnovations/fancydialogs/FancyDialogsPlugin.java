package com.fancyinnovations.fancydialogs;

import com.fancyinnovations.fancydialogs.registry.DialogRegistry;
import com.fancyinnovations.fancydialogs.storage.DialogStorage;
import com.fancyinnovations.fancydialogs.storage.JsonDialogStorage;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancyanalytics.logger.appender.Appender;
import de.oliver.fancyanalytics.logger.appender.ConsoleAppender;
import de.oliver.fancyanalytics.logger.appender.JsonAppender;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FancyDialogsPlugin extends JavaPlugin {

    private static FancyDialogsPlugin INSTANCE;
    private final ExtendedFancyLogger fancyLogger;

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

    @Override
    public void onLoad() {
        dialogRegistry = new DialogRegistry();
        dialogStorage = new JsonDialogStorage();

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

        fancyLogger.info("Successfully enabled FancyDialogs version %s".formatted(getDescription().getVersion()));
    }

    @Override
    public void onDisable() {
        fancyLogger.info("Successfully disabled FancyDialogs version %s".formatted(getDescription().getVersion()));
    }

    public static FancyDialogsPlugin get() {
        return INSTANCE;
    }


    public ExtendedFancyLogger getFancyLogger() {
        return fancyLogger;
    }

    public DialogRegistry getDialogRegistry() {
        return dialogRegistry;
    }

    public DialogStorage getDialogStorage() {
        return dialogStorage;
    }
}
