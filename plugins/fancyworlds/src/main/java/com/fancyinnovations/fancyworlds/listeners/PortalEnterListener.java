package com.fancyinnovations.fancyworlds.listeners;

import com.fancyinnovations.fancyworlds.api.portals.FPortal;
import com.fancyinnovations.fancyworlds.api.portals.PortalService;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PortalEnterListener extends FancyContext implements Listener {

    private final Set<UUID> teleportingPlayers = ConcurrentHashMap.newKeySet();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!event.hasChangedBlock()) {
            return;
        }

        Player player = event.getPlayer();
        if (teleportingPlayers.contains(player.getUniqueId())) {
            return;
        }

        PortalService portalService = PortalService.get();
        FPortal enteredPortal = getPortalAt(portalService, event.getTo());
        if (enteredPortal == null) {
            return;
        }

        FPortal previousPortal = getPortalAt(portalService, event.getFrom());
        if (enteredPortal == previousPortal) {
            return;
        }

        FWorld destinationWorld = plugin.getWorldService().getWorldByName(enteredPortal.getDestinationWorldName());
        if (destinationWorld == null || !destinationWorld.isWorldLoaded()) {
            translator.translate("commands.portal.use.destination_unavailable")
                    .withPrefix()
                    .replace("portalName", enteredPortal.getName())
                    .replace("destinationWorld", enteredPortal.getDestinationWorldName())
                    .send(player);
            return;
        }

        teleportingPlayers.add(player.getUniqueId());
        player.teleportAsync(destinationWorld.getBukkitWorld().getSpawnLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN)
                .whenComplete((success, error) -> {
                    teleportingPlayers.remove(player.getUniqueId());
                    if (error == null && Boolean.TRUE.equals(success)) {
                        return;
                    }

                    if (error != null) {
                        logger.warn(
                                "Failed to teleport player %s through portal %s".formatted(player.getName(), enteredPortal.getName()),
                                ThrowableProperty.of(error)
                        );
                    }
                    translator.translate("commands.portal.use.failed")
                            .withPrefix()
                            .replace("portalName", enteredPortal.getName())
                            .replace("destinationWorld", enteredPortal.getDestinationWorldName())
                            .send(player);
                });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        teleportingPlayers.remove(event.getPlayer().getUniqueId());
    }

    private FPortal getPortalAt(PortalService service, Location location) {
        return service.getPortalAt(
                location.getWorld().getName(),
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );
    }
}
