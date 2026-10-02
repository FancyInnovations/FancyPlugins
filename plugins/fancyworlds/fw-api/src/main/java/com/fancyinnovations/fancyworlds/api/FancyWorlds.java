package com.fancyinnovations.fancyworlds.api;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.api.worlds.WorldStorage;
import com.fancyinnovations.fancyworlds.api.portals.PortalService;
import com.fancyinnovations.fancyworlds.api.portals.PortalStorage;
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

    PortalStorage getPortalStorage();

    PortalService getPortalService();

}
