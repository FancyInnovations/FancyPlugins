package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.Formats;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class MoveHereCMD {

    public static final MoveHereCMD INSTANCE = new MoveHereCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private MoveHereCMD() {
    }

    @Command({"hologram-new edit <hologram> move_here", "hologram-new edit <hologram> position"})
    @Description("Teleports the hologram to your position")
    @CommandPermission("fancyholograms.commands.hologram.edit.move_here")
    public void moveHere(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        Player player = actor.requirePlayer();

        if (hologram.getData().getLinkedNpcName() != null) {
            translator.translate("commands.hologram.edit.link_with_npc.already_linked")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        final var copied = hologram.getData().copy(hologram.getData().getName());
        final Location originalLocation = copied.getLocation();
        final Location playerLocation = player.getLocation();
        final Location newLocation = new Location(
                playerLocation.getWorld(),
                playerLocation.x(),
                playerLocation.y(),
                playerLocation.z(),
                originalLocation != null ? originalLocation.getYaw() : 0,
                originalLocation != null ? originalLocation.getPitch() : 0
        );
        copied.setLocation(newLocation);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.POSITION)) {
            return;
        }

        hologram.getData().setLocation(copied.getLocation());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.position.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("x", Formats.COORDINATES_DECIMAL.format(newLocation.x()))
                .replace("y", Formats.COORDINATES_DECIMAL.format(newLocation.y()))
                .replace("z", Formats.COORDINATES_DECIMAL.format(newLocation.z()))
                .send(actor.sender());
    }
}
