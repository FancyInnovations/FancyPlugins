package com.fancyinnovations.fancyholograms.listeners;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.events.HologramSpawnEvent;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.PluginUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.geysermc.floodgate.api.FloodgateApi;

public class BedrockPlayerListener implements Listener {

    @EventHandler
    public void onHologramShow(final HologramSpawnEvent event) {
        if (!FancyHologramsPlugin.get().getFHConfiguration().isHologramsForBedrockPlayersEnabled() && PluginUtils.isFloodgateEnabled()) {
            boolean isBedrockPlayer = FloodgateApi.getInstance().isFloodgatePlayer(event.getPlayer().getUniqueId());
            if (isBedrockPlayer) {
                FancyHolograms.get().getFancyLogger().debug("Cancelled hologram spawn for Bedrock player: " + event.getPlayer().getName());
                event.setCancelled(true);
            }
        }
    }

}
