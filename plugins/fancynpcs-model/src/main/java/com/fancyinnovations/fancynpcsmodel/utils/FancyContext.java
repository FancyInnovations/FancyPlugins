package com.fancyinnovations.fancynpcsmodel.utils;

import com.fancyinnovations.fancynpcsmodel.config.FancyNpcsModelConfigImpl;
import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.translations.Translator;

public abstract class FancyContext {

    protected final FancyNpcsModelPlugin plugin;
    protected final FancyNpcsModelConfigImpl config;
    protected final ExtendedFancyLogger logger;
    protected final Translator translator;

    public FancyContext() {
        this.plugin = FancyNpcsModelPlugin.get();
        this.config = FancyNpcsModelPlugin.get().getFancyNpcsModelConfig();
        this.logger = FancyNpcsModelPlugin.get().getFancyLogger();
        this.translator = FancyNpcsModelPlugin.get().getTranslator();
    }

}
