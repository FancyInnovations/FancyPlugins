package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import de.oliver.fancylib.translations.message.SimpleMessage;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Switch;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldUnloadCMD extends FancyContext {

    public static final WorldUnloadCMD INSTANCE = new WorldUnloadCMD();

    @Command("world unload")
    @Description("Unloads a world.")
    @CommandPermission("fancyworlds.commands.world.unload")
    public void unload(
            final BukkitCommandActor actor,
            final FWorld world,
            @Switch @Optional Boolean force
    ) {
        if (!world.isWorldLoaded()) {
            translator.translate("commands.world.unload.not_loaded")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        if (world.getBukkitWorld().getPlayerCount() > 0) {
            if (force == null || !force) {
                translator.translate("commands.world.unload.players_in_world")
                        .withPrefix()
                        .replace("worldName", world.getName())
                        .send(actor.sender());
                return;
            }
        }

        if (actor.isPlayer() && (force != null && force)) {
            SimpleMessage question = (SimpleMessage) translator.translate("commands.world.unload.confirmation_force")
                    .replace("worldName", world.getName());

            new ConfirmationDialog(question.getMessage())
                    .withTitle("Confirm unload")
                    .withOnConfirm(() -> Bukkit.getScheduler().runTask(plugin, () -> unloadImpl(actor, world, true)))
                    .withOnCancel(
                            () -> translator.translate("commands.world.unload.cancelled")
                                    .withPrefix()
                                    .replace("worldName", world.getName())
                                    .send(actor.sender())
                    )
                    .ask(actor.asPlayer());
        } else {
            unloadImpl(actor, world, force != null && force);
        }
    }

    private void unloadImpl(
            final BukkitCommandActor actor,
            final FWorld world,
            final boolean force
    ) {
        if (world.getBukkitWorld().getPlayerCount() > 0 && force) {
            World fallbackWorld = Bukkit.getWorlds().getFirst();

            translator.translate("commands.world.unload.teleporting_players")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .replace("fallbackWorldName", fallbackWorld.getName())
                    .replace("playerCount", String.valueOf(world.getBukkitWorld().getPlayerCount()))
                    .send(actor.sender());

            for (Player player : world.getBukkitWorld().getPlayers()) {
                player.teleport(fallbackWorld.getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
            }
        }

        if (Bukkit.unloadWorld(world.getBukkitWorld(), true)) {
            ((FWorldImpl) world).setBukkitWorld(null);

            translator.translate("commands.world.unload.success")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
        } else {
            translator.translate("commands.world.unload.failed")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
        }
    }
}
