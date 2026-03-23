package com.fancyinnovations.fancyworlds.utils;

import com.fancyinnovations.fancyworlds.config.FancyWorldsConfigImpl;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.translations.Translator;

public abstract class FancyContext {

    protected final FancyWorldsPlugin plugin;
    protected final ExtendedFancyLogger logger;
    protected final FancyWorldsConfigImpl config;
    protected final Translator translator;

    public FancyContext() {
        this.plugin = FancyWorldsPlugin.get();
        this.logger = FancyWorldsPlugin.get().getFancyLogger();
        this.config = (FancyWorldsConfigImpl) FancyWorldsPlugin.get().getFancyWorldsConfig();
        this.translator = FancyWorldsPlugin.get().getTranslator();
    }

}
