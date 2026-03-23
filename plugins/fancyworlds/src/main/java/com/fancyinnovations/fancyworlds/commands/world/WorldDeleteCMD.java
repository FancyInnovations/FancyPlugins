package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import de.oliver.fancylib.translations.message.SimpleMessage;
import org.bukkit.Bukkit;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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

        new ConfirmationDialog(question.getMessage())
                .withTitle("Confirm deletion")
                .withOnConfirm(() -> Bukkit.getScheduler().runTask(plugin, () -> deleteImpl(actor, world)))
                .withOnCancel(
                        () -> translator.translate("commands.world.delete.cancelled")
                                .withPrefix()
                                .replace("worldName", world.getName())
                                .send(actor.sender())
                )
                .ask(actor.asPlayer());
    }

    private void deleteImpl(
            final BukkitCommandActor actor,
            final FWorld world
    ) {
        plugin.getWorldService().unregisterWorld(world);

        File worldDir = Bukkit.getWorldContainer().toPath().resolve(world.getName()).toFile();
        try {
            Files.walk(worldDir.toPath())
                    .map(Path::toFile)
                    .sorted((o1, o2) -> -o1.compareTo(o2)) // Delete children before parents
                    .forEach(File::delete);
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
