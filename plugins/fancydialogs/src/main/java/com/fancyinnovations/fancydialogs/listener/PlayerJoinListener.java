package com.fancyinnovations.fancydialogs.listener;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        boolean isNewPlayer = !event.getPlayer().hasPlayedBefore();
        if (isNewPlayer) {
            String welcomeDialogID = FancyDialogsPlugin.get().getFancyDialogsConfig().getWelcomeDialogID();
            Dialog dialog = FancyDialogsPlugin.get().getDialogRegistry().get(welcomeDialogID);
            if (dialog != null) {
                dialog.open(event.getPlayer());
            } else {
                FancyDialogsPlugin.get().getLogger().warning("Welcome dialog with ID " + welcomeDialogID + " not found.");
            }
        }
    }

}
