package com.fancyinnovations.fancynpcs.utils;

import de.oliver.fancyanalytics.sdk.events.Event;
import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.skins.SkinData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class OldSkinCacheMigrator {

    private static final File OLD_SKIN_CACHE = new File("plugins/FancyNPCs/.skinCache.yml");

    public static void migrate() {
        if (!OLD_SKIN_CACHE.exists()) {
            return;
        }

        YamlConfiguration config = YamlConfiguration.loadConfiguration(OLD_SKIN_CACHE);
        ConfigurationSection skinsSection = config.getConfigurationSection("skins");
        if (skinsSection == null) {
            return;
        }

        int amount = 0;
        for (String key : skinsSection.getKeys(false)) {
            String id = skinsSection.getString(key + ".identifier");
            String value = skinsSection.getString(key + ".value");
            String signature = skinsSection.getString(key + ".signature");
            SkinData skinData = new SkinData(id, SkinData.SkinVariant.AUTO, value, signature);
            FancyNpcs.getInstance().getSkinManagerImpl().cacheSkin(skinData);
            amount++;
        }

        OLD_SKIN_CACHE.delete();

        FancyNpcs.getInstance().getLogger().info("Migrated " + amount + " skins from old cache.");
        FancyNpcs.getInstance().getFancyAnalytics().sendEvent(new Event("SkinCacheMigrated", new HashMap<>()).withProperty("amount", String.valueOf(amount)));
    }

}
