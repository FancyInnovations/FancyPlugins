package com.fancyinnovations.fancydialogs.config;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import de.oliver.fancylib.featureFlags.FeatureFlag;
import de.oliver.fancylib.featureFlags.FeatureFlagConfig;

public class FDFeatureFlags {

    public static final FeatureFlag DEBUG_MODE = new FeatureFlag("debug-mode", "Enable debug mode", false);

    public static void load() {
        FeatureFlagConfig config = new FeatureFlagConfig(FancyDialogsPlugin.get());
        config.addFeatureFlag(DEBUG_MODE);
        config.load();
    }

}
