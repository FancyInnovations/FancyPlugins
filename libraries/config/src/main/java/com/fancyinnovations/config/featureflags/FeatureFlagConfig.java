package com.fancyinnovations.config.featureflags;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Use Config instead of this class.
 */
@Deprecated
public class FeatureFlagConfig {

    private final File configFile;
    private final List<FeatureFlag> featureFlags;

    public FeatureFlagConfig(Plugin plugin) {
        this.configFile = new File("plugins" + File.separator + plugin.getName() + File.separator + "featureFlags.yml");
        this.featureFlags = new ArrayList<>();
    }

    public void load() {
        if (!configFile.exists()) {
            try {
                new File(configFile.getParent()).mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        for (FeatureFlag featureFlag : featureFlags) {
            if (config.isSet("feature-flags." + featureFlag.getName())) {
                continue;
            }

            config.set("feature-flags." + featureFlag.getName(), false);
            config.setInlineComments("feature-flags." + featureFlag.getName(), List.of(featureFlag.getDescription()));
        }

        for (String flagName : config.getConfigurationSection("feature-flags").getKeys(false)) {
            boolean enabled = config.getBoolean("feature-flags." + flagName, false);
            FeatureFlag flag = getFeatureFlag(flagName);
            if (flag == null) {
                flag = new FeatureFlag(flagName, "", false);
                featureFlags.add(flag);
            }

            flag.setEnabled(enabled);
        }

        try {
            config.save(configFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public FeatureFlag getFeatureFlag(String name) {
        for (FeatureFlag featureFlag : featureFlags) {
            if (featureFlag.getName().equalsIgnoreCase(name.toLowerCase())) {
                return featureFlag;
            }
        }

        return null;
    }

    public void addFeatureFlag(FeatureFlag featureFlag) {
        if (!featureFlags.contains(featureFlag)) {
            featureFlags.add(featureFlag);
        }
    }

}
