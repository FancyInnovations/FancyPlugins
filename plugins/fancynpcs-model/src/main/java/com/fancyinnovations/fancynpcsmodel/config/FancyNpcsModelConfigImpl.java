package com.fancyinnovations.fancynpcsmodel.config;

import com.fancyinnovations.config.Config;
import com.fancyinnovations.config.ConfigField;
import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;

public class FancyNpcsModelConfigImpl {

    public static final String LOG_LEVEL_PATH = "settings.logging.level";
    public static final String MUTE_VERSION_NOTIFICATION_PATH = "settings.logging.version_notification";

    public static final String LANGUAGE_PATH = "settings.language";

    private static final String CONFIG_FILE_PATH = "plugins/FancyNpcsModel/config.yml";

    private Config config;

    public void init() {
        config = new Config(FancyNpcsModelPlugin.get().getFancyLogger(), CONFIG_FILE_PATH);

        config.addField(new ConfigField<>(
                LOG_LEVEL_PATH,
                "The log level for the plugin (DEBUG, INFO, WARN, ERROR).",
                false,
                "INFO",
                false,
                String.class
        ));

        config.addField(new ConfigField<>(
                MUTE_VERSION_NOTIFICATION_PATH,
                "Whether version notifications are muted.",
                false,
                false,
                false,
                Boolean.class
        ));

        config.addField(new ConfigField<>(
                LANGUAGE_PATH,
                "The language for the plugin.",
                false,
                "default",
                false,
                String.class
        ));

    }

    public void reload() {
        config.reload();
    }

    public Config getConfig() {
        return config;
    }

    public String getLogLevel() {
        return config.get(LOG_LEVEL_PATH);
    }

    public boolean areVersionNotificationsMuted() {
        return config.get(MUTE_VERSION_NOTIFICATION_PATH);
    }

    public String getLanguage() {
        return config.get(LANGUAGE_PATH);
    }

}
