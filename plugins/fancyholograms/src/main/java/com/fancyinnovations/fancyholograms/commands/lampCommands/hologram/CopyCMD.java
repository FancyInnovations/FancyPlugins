package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.events.HologramCreateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class CopyCMD {

    public static final CopyCMD INSTANCE = new CopyCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private CopyCMD() {
    }

    @Command("hologram-new copy <hologram> <name>")
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

        final var copy = FancyHologramsPlugin.get().getHologramFactory().apply(data);
        copy.getData().setHasChanges(true);

        if (!new HologramCreateEvent(copy, player).callEvent()) {
            translator.translate("commands.hologram.create.canceled")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        FancyHologramsPlugin.get().getController().refreshHologram(copy, Bukkit.getOnlinePlayers());
        FancyHologramsPlugin.get().getRegistry().register(copy);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(copy.getData());
        }

        translator.translate("commands.hologram.copy.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("name", name)
                .send(actor.sender());
    }
}
