package com.fancyinnovations.fancynpcs.api;

import com.fancyinnovations.config.featureflags.FeatureFlagConfig;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.translations.Translator;
import com.fancyinnovations.fancynpcs.api.actions.ActionManager;
import com.fancyinnovations.fancynpcs.api.skins.SkinManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

public interface FancyNpcsPlugin {

    static FancyNpcsPlugin get() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        if (pluginManager.isPluginEnabled("FancyNpcs")) {
            return (FancyNpcsPlugin) pluginManager.getPlugin("FancyNpcs");
        }

        throw new NullPointerException("Plugin is not enabled");
    }

    JavaPlugin getPlugin();

    ExtendedFancyLogger getFancyLogger();

    ScheduledExecutorService getNpcThread();

    /**
     * Creates a new thread with the given name and runnable.
     * Warning: Do not use this method, it is for internal use only.
     */
    @ApiStatus.Internal
    Thread newThread(String name, Runnable runnable);

    FancyScheduler getScheduler();

    Function<NpcData, Npc> getNpcAdapter();

    FancyNpcsConfig getFancyNpcConfig();

    FeatureFlagConfig getFeatureFlagConfig();

    NpcManager getNpcManager();

    AttributeManager getAttributeManager();

    ActionManager getActionManager();

    SkinManager getSkinManager();

    Translator getTranslator();

    /**
     * Gets the current NPC storage implementation.
     *
     * @return The NPC storage.
     */
    @NotNull
    NpcStorage getNpcStorage();

    /**
     * Sets a new NPC storage implementation.
     *
     * @param storage The new NPC storage.
     * @param reload  Whether to reload NPCs from the new storage.
     */
    void setNpcStorage(@NotNull NpcStorage storage, boolean reload);

    /**
     * Gets the NPC registry for managing NPC registration.
     *
     * @return The NPC registry.
     */
    @NotNull
    NpcRegistry getRegistry();

    /**
     * Gets the NPC controller for managing NPC visibility.
     *
     * @return The NPC controller.
     */
    @NotNull
    NpcController getController();
}
