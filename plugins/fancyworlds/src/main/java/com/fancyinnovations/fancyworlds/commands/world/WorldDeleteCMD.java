package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import com.fancyinnovations.fancyworlds.worlds.service.WorldOperations;
import de.oliver.fancylib.translations.message.SimpleMessage;
import org.bukkit.Bukkit;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.IOException;

public class WorldDeleteCMD extends FancyContext {

    public static final WorldDeleteCMD INSTANCE = new WorldDeleteCMD();

    @Command("world delete")
    @Description("Deletes a world.")
    @CommandPermission("fancyworlds.commands.world.delete")
    public void delete(
            final BukkitCommandActor actor,
            final FWorld world
    ) {
        if (world.isWorldLoaded()) {
            translator.translate("commands.world.delete.world_is_loaded")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        SimpleMessage question = (SimpleMessage) translator.translate("commands.world.delete.confirmation")
                .replace("worldName", world.getName());

        if (actor.isPlayer()) {
            new ConfirmationDialog(question.getMessage())
                    .withTitle("Confirm deletion")
                    .withOnConfirm(() -> Bukkit.getScheduler().runTask(plugin, () -> deleteImpl(actor, world)))
                    .withOnCancel(() -> translator.translate("commands.world.delete.cancelled")
                            .withPrefix().replace("worldName", world.getName()).send(actor.sender()))
                    .ask(actor.asPlayer());
        } else {
            deleteImpl(actor, world);
        }
    }

    private void deleteImpl(
            final BukkitCommandActor actor,
            final FWorld world
    ) {
        if (world.isWorldLoaded()) {
            translator.translate("commands.world.delete.world_is_loaded")
                    .withPrefix().replace("worldName", world.getName()).send(actor.sender());
            return;
        }

        try {
            WorldOperations.delete(world);
        } catch (IOException e) {
            translator.translate("commands.world.delete.failed")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.world.delete.success")
                .withPrefix()
                .replace("worldName", world.getName())
                .send(actor.sender());
    }
}
