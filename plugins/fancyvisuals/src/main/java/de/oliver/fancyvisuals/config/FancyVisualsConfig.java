package de.oliver.fancyvisuals.config;

import com.fancyinnovations.config.Config;
import com.fancyinnovations.config.ConfigField;
import de.oliver.fancyvisuals.FancyVisuals;

public class FancyVisualsConfig {

    public static final String WORKER_THREADS_PATH = "settings.worker_threads";
    public static final String LOG_LEVEL_PATH = "settings.logging.level";

    public static final String TABLIST_ENABLED_PATH = "tablist.enabled";
    public static final String TABLIST_HEADER_FOOTER_ENABLED_PATH = "tablist.header_footer.enabled";
    public static final String TABLIST_ENTRIES_ENABLED_PATH = "tablist.entries.enabled";
    public static final String TABLIST_CUSTOM_ENTRIES_ENABLED_PATH = "tablist.custom_entries.enabled";
    public static final String TABLIST_UPDATE_INTERVAL_MS_PATH = "tablist.update_interval_ms";

    public static final String ACTIONBAR_ENABLED_PATH = "actionbar.enabled";
    public static final String ACTIONBAR_UPDATE_INTERVAL_MS_PATH = "actionbar.update_interval_ms";

    public static final String BOSSBARS_ENABLED_PATH = "bossbars.enabled";
    public static final String BOSSBARS_UPDATE_INTERVAL_MS_PATH = "bossbars.update_interval_ms";

    public static final String TITLES_ENABLED_PATH = "titles.enabled";
    public static final String TITLES_UPDATE_INTERVAL_MS_PATH = "titles.update_interval_ms";

    public static final String CHAT_ENABLED_PATH = "chat.enabled";

    private static final String CONFIG_FILE_PATH = "plugins/FancyVisuals/config.yml";

    private Config config;

    public void load() {
        if (config == null) {
            config = new Config(FancyVisuals.getFancyLogger(), CONFIG_FILE_PATH);

            config.addField(new ConfigField<>(
                    WORKER_THREADS_PATH,
                    "Amount of worker threads used for FancyVisuals background tasks.",
                    false,
                    4,
                    false,
                    Integer.class
            ));
            config.addField(new ConfigField<>(
                    LOG_LEVEL_PATH,
                    "The log level for the plugin (DEBUG, INFO, WARN, ERROR).",
                    false,
                    "INFO",
                    false,
                    String.class
            ));

            config.addField(new ConfigField<>(
                    TABLIST_ENABLED_PATH,
                    "Enable the tablist module.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
            config.addField(new ConfigField<>(
                    TABLIST_HEADER_FOOTER_ENABLED_PATH,
                    "Enable tablist header & footer updates.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
            config.addField(new ConfigField<>(
                    TABLIST_ENTRIES_ENABLED_PATH,
                    "Enable player tablist entry formatting.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
            config.addField(new ConfigField<>(
                    TABLIST_CUSTOM_ENTRIES_ENABLED_PATH,
                    "Enable custom tablist entries.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
            config.addField(new ConfigField<>(
                    TABLIST_UPDATE_INTERVAL_MS_PATH,
                    "Interval in milliseconds to refresh tablist content.",
                    false,
                    1000,
                    false,
                    Integer.class
            ));

            config.addField(new ConfigField<>(
                    ACTIONBAR_ENABLED_PATH,
                    "Enable action bar updates.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
            config.addField(new ConfigField<>(
                    ACTIONBAR_UPDATE_INTERVAL_MS_PATH,
                    "Interval in milliseconds to refresh action bar content.",
                    false,
                    1000,
                    false,
                    Integer.class
            ));

            config.addField(new ConfigField<>(
                    BOSSBARS_ENABLED_PATH,
                    "Enable bossbar updates.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
            config.addField(new ConfigField<>(
                    BOSSBARS_UPDATE_INTERVAL_MS_PATH,
                    "Interval in milliseconds to refresh bossbar content.",
                    false,
                    1000,
                    false,
                    Integer.class
            ));

            config.addField(new ConfigField<>(
                    TITLES_ENABLED_PATH,
                    "Enable title and subtitle announcements.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
            config.addField(new ConfigField<>(
                    TITLES_UPDATE_INTERVAL_MS_PATH,
                    "Interval in milliseconds to refresh title announcements.",
                    false,
                    5000,
                    false,
                    Integer.class
            ));

            config.addField(new ConfigField<>(
                    CHAT_ENABLED_PATH,
                    "Enable chat formatting.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
        }

        config.reload();
    }

    public int getAmountWorkerThreads() {
        return config.get(WORKER_THREADS_PATH);
    }

    public String getLogLevel() {
        return config.get(LOG_LEVEL_PATH);
    }

    public boolean isTablistEnabled() {
        return config.get(TABLIST_ENABLED_PATH);
    }

    public boolean isTablistHeaderFooterEnabled() {
        return config.get(TABLIST_HEADER_FOOTER_ENABLED_PATH);
    }

    public boolean isTablistEntriesEnabled() {
        return config.get(TABLIST_ENTRIES_ENABLED_PATH);
    }

    public boolean isTablistCustomEntriesEnabled() {
        return config.get(TABLIST_CUSTOM_ENTRIES_ENABLED_PATH);
    }

    public int getTablistUpdateIntervalMs() {
        return config.get(TABLIST_UPDATE_INTERVAL_MS_PATH);
    }

    public boolean isActionBarEnabled() {
        return config.get(ACTIONBAR_ENABLED_PATH);
    }

    public int getActionBarUpdateIntervalMs() {
        return config.get(ACTIONBAR_UPDATE_INTERVAL_MS_PATH);
    }

    public boolean isBossbarsEnabled() {
        return config.get(BOSSBARS_ENABLED_PATH);
    }

    public int getBossbarsUpdateIntervalMs() {
        return config.get(BOSSBARS_UPDATE_INTERVAL_MS_PATH);
    }

    public boolean isTitlesEnabled() {
        return config.get(TITLES_ENABLED_PATH);
    }

    public int getTitlesUpdateIntervalMs() {
        return config.get(TITLES_UPDATE_INTERVAL_MS_PATH);
    }

    public boolean isChatEnabled() {
        return config.get(CHAT_ENABLED_PATH);
    }
}
