package de.oliver.fancylib;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancylib.gui.inventoryClick.InventoryClickListener;
import de.oliver.fancylib.gui.inventoryClick.impl.CancelInventoryItemClick;
import de.oliver.fancylib.gui.inventoryClick.impl.ChangePageInventoryItemClick;
import de.oliver.fancylib.itemClick.PlayerInteractListener;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FoliaScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;

public class FancyLib {

    private static FancyLib instance;
    private static final ExtendedFancyLogger logger = new ExtendedFancyLogger("FancyLib");

    private final JavaPlugin plugin;
    private final FancyScheduler scheduler;

    public FancyLib(JavaPlugin plugin) {
        instance = this;
        this.plugin = plugin;
        this.scheduler = ServerSoftware.isFolia()
                ? new FoliaScheduler(plugin)
                : new BukkitScheduler(plugin);
    }

    public static FancyLib getInstance() {
        return instance;
    }

    public static ExtendedFancyLogger getLogger() {
        return logger;
    }

    /**
     * Registers the listeners for the inventory click and player interact events.
     */
    public void registerListeners() {
        CancelInventoryItemClick.INSTANCE.register();
        ChangePageInventoryItemClick.INSTANCE.register();

        Bukkit.getPluginManager().registerEvents(new InventoryClickListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new PlayerInteractListener(), plugin);
    }

    @ApiStatus.Internal
    public FancyScheduler getScheduler() {
        return scheduler;
    }

    public JavaPlugin getPlugin() {
        return plugin;
    }
}
