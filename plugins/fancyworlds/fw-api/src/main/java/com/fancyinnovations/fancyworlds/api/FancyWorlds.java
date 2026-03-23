package com.fancyinnovations.fancyworlds.api;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.api.worlds.WorldStorage;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public interface FancyWorlds {

    static FancyWorlds get() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("FancyWorlds");
        return (FancyWorlds) plugin;
    }

    ExtendedFancyLogger getFancyLogger();

    FancyWorldsConfig getFancyWorldsConfig();

    Translator getTranslator();

    WorldStorage getWorldStorage();

    WorldService getWorldService();

}
