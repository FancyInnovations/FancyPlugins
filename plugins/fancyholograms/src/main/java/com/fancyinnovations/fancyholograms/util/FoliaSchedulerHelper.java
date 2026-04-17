package com.fancyinnovations.fancyholograms.util;

import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import org.bukkit.entity.Player;

public class FoliaSchedulerHelper {

    /**
     * Runs the runnable on the player's scheduler if on Folia
     * otherwise it runs the runnable on the current thread
     */
    public static void playerScheduler(Player player, Runnable runnable) {
        if (ServerSoftware.isFolia()) {
            player.getScheduler().run(FancyHologramsPlugin.get(), (_) -> runnable.run(), null);
            return;
        }

        runnable.run();
    }

}
