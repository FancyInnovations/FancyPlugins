package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldTeleportCMD extends FancyContext {

    public static final WorldTeleportCMD INSTANCE = new WorldTeleportCMD();

    @Command({"world teleport", "world tp"})
    @Description("Teleports you or someone else to the specified world")
    @CommandPermission("fancyworlds.commands.world.teleport")
    public void version(
            final BukkitCommandActor actor,
            FWorld world,
            @Optional Player target,
            @Optional Location destination
    ) {
        if (!world.isWorldLoaded()) {
            translator.translate("common.world_not_loaded")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        Player finalTarget = target != null ? target : actor.requirePlayer();

        if (world.getName().equalsIgnoreCase(finalTarget.getWorld().getName())) {
            translator.translate("commands.world.teleport.already_in_world")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        Location finalDestination = destination != null ? destination : world.getBukkitWorld().getSpawnLocation();

        finalTarget.teleportAsync(finalDestination, PlayerTeleportEvent.TeleportCause.COMMAND).thenRun(() -> {
            translator.translate("commands.world.teleport.success")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .replace("playerName", finalTarget.getName())
                    .send(actor.sender());
        }).exceptionally(ex -> {
            logger.warn("Failed to teleport player " + finalTarget.getName() + " to world " + world.getName(), ThrowableProperty.of(ex));

            translator.translate("commands.world.teleport.failed")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .replace("playerName", finalTarget.getName())
                    .send(actor.sender());
            return null;
        });
    }
}
