package de.oliver.fancynpcs.commands.lampCommands;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.FancyNpcsConfigImpl;

/**
 * Shared plugin services for Lamp commands.
 */
public abstract class FancyContext {

    protected final FancyNpcs plugin;
    protected final FancyNpcsConfigImpl config;
    protected final ExtendedFancyLogger logger;
    protected final Translator translator;

    protected FancyContext() {
        this.plugin = FancyNpcs.getInstance();
        this.config = plugin.getFancyNpcConfig();
        this.logger = plugin.getFancyLogger();
        this.translator = plugin.getTranslator();
    }
}
