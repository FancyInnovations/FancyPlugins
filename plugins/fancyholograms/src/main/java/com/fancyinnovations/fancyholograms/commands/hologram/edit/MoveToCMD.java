package com.fancyinnovations.fancyholograms.commands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.FancyContext;
import com.fancyinnovations.fancyholograms.commands.oldCommands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.suggestions.*;
import com.fancyinnovations.fancyholograms.util.CoordinateUtils;
import com.fancyinnovations.fancyholograms.util.Formats;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class MoveToCMD extends FancyContext {

    public static final MoveToCMD INSTANCE = new MoveToCMD();

    private MoveToCMD() {
    }

    @Command("hologram edit <hologram> move_to <x> <y> <z>")
    @Description("Teleports the hologram to coordinates")
    @CommandPermission("fancyholograms.commands.hologram.edit.move_to")
    public void moveTo(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(HologramCoordinateXSuggestion.class) String x,
            final @NotNull @SuggestWith(HologramCoordinateYSuggestion.class) String y,
            final @NotNull @SuggestWith(HologramCoordinateZSuggestion.class) String z,
            final @Optional @SuggestWith(HologramYawSuggestion.class) String yaw,
            final @Optional @SuggestWith(HologramPitchSuggestion.class) String pitch
    ) {
        Player player = actor.requirePlayer();

        if (hologram.getData().getLinkedNpcName() != null) {
            translator.translate("commands.hologram.edit.link_with_npc.already_linked")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        final var parsedX = CoordinateUtils.calculateCoordinate(x, hologram.getData().getLocation(), player.getLocation(), Location::x);
        final var parsedY = CoordinateUtils.calculateCoordinate(y, hologram.getData().getLocation(), player.getLocation(), Location::y);
        final var parsedZ = CoordinateUtils.calculateCoordinate(z, hologram.getData().getLocation(), player.getLocation(), Location::z);

        if (parsedX == null || parsedY == null || parsedZ == null) {
            translator.translate("commands.hologram.edit.move_to.invalid_position")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        Location originLocation = hologram.getData().getLocation();
        final var location = new Location(
                originLocation != null && originLocation.getWorld() != null ? originLocation.getWorld() : player.getWorld(),
                parsedX, parsedY, parsedZ,
                originLocation != null ? originLocation.getYaw() : 0,
                originLocation != null ? originLocation.getPitch() : 0);

        if (yaw != null) {
            final var parsedYaw = CoordinateUtils.calculateCoordinate(yaw, hologram.getData().getLocation(), player.getLocation(), loc -> loc.getYaw() + 180f);
            if (parsedYaw == null) {
                translator.translate("commands.hologram.edit.move_to.invalid_yaw")
                        .withPrefix()
                        .send(actor.sender());
                return;
            }
            location.setYaw(parsedYaw.floatValue() - 180f);
        }

        if (pitch != null) {
            final var parsedPitch = CoordinateUtils.calculateCoordinate(pitch, hologram.getData().getLocation(), player.getLocation(), Location::getPitch);
            if (parsedPitch == null) {
                translator.translate("commands.hologram.edit.move_to.invalid_pitch")
                        .withPrefix()
                        .send(actor.sender());
                return;
            }
            location.setPitch(parsedPitch.floatValue());
        }

        final var copied = hologram.getData().copy(hologram.getData().getName());
        copied.setLocation(location);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.POSITION)) {
            return;
        }

        hologram.getData().setLocation(copied.getLocation());

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.position.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("world", location.getWorld() != null ? location.getWorld().getName() : "")
                .replace("x", Formats.COORDINATES_DECIMAL.format(location.x()))
                .replace("y", Formats.COORDINATES_DECIMAL.format(location.y()))
                .replace("z", Formats.COORDINATES_DECIMAL.format(location.z()))
                .send(actor.sender());
    }
}
