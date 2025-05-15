package com.fancyinnovations.fancydialogs;

import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

public class FancyDialogsPlugin extends JavaPlugin {

    private static FancyDialogsPlugin INSTANCE;

    public FancyDialogsPlugin() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public static FancyDialogsPlugin getInstance() {
        return INSTANCE;
    }

}
