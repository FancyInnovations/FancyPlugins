package com.fancyinnovations.fancynpcs.listeners;

import com.destroystokyo.paper.profile.ProfileProperty;
import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.skins.SkinData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // don't spawn the npc for player if he just joined
        FancyNpcs.getInstance().getVisibilityTracker().addJoinDelayPlayer(event.getPlayer().getUniqueId());
        FancyNpcs.getInstance().getScheduler().runTaskLater(null, 20L * 2, () -> FancyNpcs.getInstance().getVisibilityTracker().removeJoinDelayPlayer(event.getPlayer().getUniqueId()));

        if (!FancyNpcs.getInstance().getFancyNpcConfig().isMuteVersionNotification() && event.getPlayer().hasPermission("FancyNpcs.admin")) {
            FancyNpcs.getInstance().getScheduler().runTaskAsynchronously(
                    () -> FancyNpcs.getInstance().getVersionConfig().checkVersionAndDisplay(event.getPlayer(), true)
            );
        }

        for (ProfileProperty property : event.getPlayer().getPlayerProfile().getProperties()) {
            if (!property.getName().equals("textures")) {
                continue;
            }

            SkinData skinData = new SkinData(
                    event.getPlayer().getUniqueId().toString(),
                    SkinData.SkinVariant.AUTO,
                    property.getValue(),
                    property.getSignature()
            );

            FancyNpcs.getInstance().getSkinManagerImpl().getMemCache().addSkin(skinData);
        }
    }
}
