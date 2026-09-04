package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.util.PluginUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class PlayerListener implements Listener {

    private final @NotNull FancyHolograms plugin;

    private final Map<UUID, Integer> loadingResourcePacks; // UUID -> number of resource-packs currently loading for the player

    public PlayerListener(@NotNull final FancyHolograms plugin) {
        this.plugin = plugin;
        this.loadingResourcePacks = new ConcurrentHashMap<>();
    }

    // For 1.20.2 and higher this method returns actual pack identifier, while for older versions, the identifier is a dummy UUID full of zeroes.
    // Versions prior 1.20.2 supports sending and receiving only one resource-pack and a dummy, constant identifier can be used as a key.
    private static @NotNull UUID getResourcePackID(final @NotNull PlayerResourcePackStatusEvent event) {
        try {
            event.getClass().getMethod("getID");
            return event.getID();
        } catch (final @NotNull NoSuchMethodException e) {
            return new UUID(0, 0);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(@NotNull final PlayerQuitEvent event) {
        PluginUtils.clearProtocolCache(event.getPlayer().getUniqueId());

        FancyHolograms.get().getHologramThread().submit(() -> {
            for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
                hologram.forceHideHologram(event.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(@NotNull final PlayerJoinEvent event) {
        FancyHolograms.get().getHologramThread().submit(() -> {
            for (final var hologram : this.plugin.getHologramsManager().getHolograms()) {
                hologram.forceHideHologram(event.getPlayer());
                hologram.forceUpdateShownStateFor(event.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onTeleport(@NotNull final PlayerTeleportEvent event) {
        FancyHolograms.get().getHologramThread().submit(() -> {
            for (final Hologram hologram : this.plugin.getHologramsManager().getHolograms()) {
                hologram.forceUpdateShownStateFor(event.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldChange(@NotNull final PlayerChangedWorldEvent event) {
        FancyHolograms.get().getHologramThread().submit(() -> {
            for (final Hologram hologram : this.plugin.getHologramsManager().getHolograms()) {
                hologram.forceUpdateShownStateFor(event.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onResourcePackStatus(@NotNull final PlayerResourcePackStatusEvent event) {
        // Skipping event calls before player has fully loaded to the server.
        // This should fix NPE due to vanillaPlayer.connection being null when sending resource-packs in the configuration stage.
        if (!event.getPlayer().isOnline())
            return;

        UUID uuid = event.getPlayer().getUniqueId();

        if (event.getStatus() == Status.ACCEPTED) {
            loadingResourcePacks.put(uuid, loadingResourcePacks.getOrDefault(uuid, 0) + 1);
        } else {
            if (loadingResourcePacks.containsKey(uuid)) {
                loadingResourcePacks.put(uuid, loadingResourcePacks.get(uuid) - 1);
            }
        }

        if (loadingResourcePacks.getOrDefault(uuid, 0) <= 0) {
            loadingResourcePacks.remove(uuid);

            FancyHolograms.get().getHologramThread().submit(() -> {
                for (final Hologram hologram : this.plugin.getHologramsManager().getHolograms()) {
                    hologram.refreshHologram(event.getPlayer());
                }
            });
        }
    }

}
