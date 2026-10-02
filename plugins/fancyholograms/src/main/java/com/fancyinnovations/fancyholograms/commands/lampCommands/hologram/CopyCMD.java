package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.events.HologramCreateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class CopyCMD extends FancyContext {

    public static final CopyCMD INSTANCE = new CopyCMD();

    private CopyCMD() {
    }

    @Command("hologram copy <hologram> <name>")
    @Description("Copies a hologram")
    @CommandPermission("fancyholograms.commands.hologram.copy")
    public void copy(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull String name
    ) {
        if (plugin.getRegistry().get(name).isPresent()) {
            translator.translate("commands.hologram.create.already_exists")
                    .withPrefix()
                    .replace("name", name)
                    .send(actor.sender());
            return;
        }

        if (name.contains(".")) {
            translator.translate("commands.hologram.create.no_dot")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        Player player = actor.requirePlayer();

        final var data = hologram.getData().copy(name);
        Location originalLocation = data.getLocation();
        Location location = player.getLocation();
        location.setPitch(originalLocation.getPitch());
        location.setYaw(originalLocation.getYaw());
        data.setLocation(location);
        data.setFilePath(name);

        final var copy = plugin.getHologramFactory().apply(data);
        copy.getData().setHasChanges(true);

        if (!new HologramCreateEvent(copy, player).callEvent()) {
            translator.translate("commands.hologram.create.canceled")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        plugin.getController().refreshHologram(copy, Bukkit.getOnlinePlayers());
        plugin.getRegistry().register(copy);

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(copy.getData());
        }

        translator.translate("commands.hologram.copy.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("name", name)
                .send(actor.sender());
    }
}
