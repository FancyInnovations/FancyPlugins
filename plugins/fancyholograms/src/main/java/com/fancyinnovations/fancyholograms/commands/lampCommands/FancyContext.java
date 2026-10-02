package com.fancyinnovations.fancyholograms.commands.lampCommands;

import com.fancyinnovations.fancyholograms.config.FHConfiguration;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.translations.Translator;

/**
 * Shared plugin services for Lamp commands.
 */
public abstract class FancyContext {

    protected final FancyHologramsPlugin plugin;
    protected final FHConfiguration config;
    protected final ExtendedFancyLogger logger;
    protected final Translator translator;

    protected FancyContext() {
        this.plugin = FancyHologramsPlugin.get();
        this.config = plugin.getFHConfiguration();
        this.logger = plugin.getFancyLogger();
        this.translator = plugin.getTranslator();
    }
}
