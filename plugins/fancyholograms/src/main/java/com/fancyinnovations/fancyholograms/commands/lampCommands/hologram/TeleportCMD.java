package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class TeleportCMD extends FancyContext {

    public static final TeleportCMD INSTANCE = new TeleportCMD();

    private TeleportCMD() {
    }

    @Command("hologram teleport <hologram>")
    @Description("Teleports you to a hologram")
    @CommandPermission("fancyholograms.commands.hologram.teleport")
    public void teleport(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        Player player = actor.requirePlayer();
        Location location = hologram.getData().getLocation();

        if (location == null || location.getWorld() == null) {
            translator.translate("commands.hologram.teleport.world_not_loaded")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        player.teleportAsync(location).thenAccept(success -> {
            if (success) {
                translator.translate("commands.hologram.teleport.success")
                        .withPrefix()
                        .replace("hologram", hologram.getData().getName())
                        .send(player);
            } else {
                translator.translate("commands.hologram.teleport.failed")
                        .withPrefix()
                        .replace("hologram", hologram.getData().getName())
                        .send(player);
            }
        });
    }
}
