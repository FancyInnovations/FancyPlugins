package de.oliver.fancyvisuals.config;

import com.fancyinnovations.config.Config;
import com.fancyinnovations.config.ConfigField;
import de.oliver.fancyvisuals.FancyVisuals;

public class NametagConfig {

    public static final String ENABLED_PATH = "nametags.enabled";
    public static final String UPDATE_INTERVAL_MS_PATH = "nametags.update_interval_ms";
    public static final String BUCKET_SIZE_PATH = "nametags.bucket_size";
    public static final String MAX_DISTANCE_PATH = "nametags.max_distance";
    public static final String HIDE_VANILLA_PATH = "nametags.hide_vanilla_nametag";
    public static final String SHOW_OWN_NAMETAG_PATH = "nametags.show_own_nametag";

    private static final String CONFIG_FILE_PATH = "plugins/FancyVisuals/config.yml";

    private Config config;

    public void load() {
        if (config == null) {
            config = new Config(FancyVisuals.getFancyLogger(), CONFIG_FILE_PATH);

            config.addField(new ConfigField<>(
                    ENABLED_PATH,
                    "Enable the nametag module.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
            config.addField(new ConfigField<>(
                    UPDATE_INTERVAL_MS_PATH,
                    "Interval in milliseconds to refresh nametags.",
                    false,
                    250,
                    false,
                    Integer.class
            ));
            config.addField(new ConfigField<>(
                    BUCKET_SIZE_PATH,
                    "Bucket count used to distribute nametag updates.",
                    false,
                    10,
                    false,
                    Integer.class
            ));
            config.addField(new ConfigField<>(
                    MAX_DISTANCE_PATH,
                    "Maximum distance (in blocks) where nametags are visible.",
                    false,
                    24,
                    false,
                    Integer.class
            ));
            config.addField(new ConfigField<>(
                    HIDE_VANILLA_PATH,
                    "Hide the vanilla nametag when FancyVisuals nametags are enabled.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
            config.addField(new ConfigField<>(
                    SHOW_OWN_NAMETAG_PATH,
                    "Whether players should see their own FancyVisuals nametag.",
                    false,
                    true,
                    false,
                    Boolean.class
            ));
        }

        config.reload();
    }

    public boolean isEnabled() {
        return config.get(ENABLED_PATH);
    }

    public int getUpdateIntervalMs() {
        return config.get(UPDATE_INTERVAL_MS_PATH);
    }

    /**
     * Retrieves the size of the distribution bucket configured.
     *
     * @return The size of the distribution bucket.
     */
    public int getDistributionBucketSize() {
        return config.get(BUCKET_SIZE_PATH);
    }

    public int getMaxDistance() {
        return config.get(MAX_DISTANCE_PATH);
    }

    public boolean hideVanillaNametag() {
        return config.get(HIDE_VANILLA_PATH);
    }

    public boolean showOwnNametag() {
        return config.get(SHOW_OWN_NAMETAG_PATH);
    }
}
