package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.Formats;
import com.google.common.primitives.Doubles;
import de.oliver.fancylib.translations.Translator;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.function.Function;

public final class MoveToCMD {

    public static final MoveToCMD INSTANCE = new MoveToCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private MoveToCMD() {
    }

    @Command("hologram-new edit <hologram> move_to <x> <y> <z>")
    @Description("Teleports the hologram to coordinates")
    @CommandPermission("fancyholograms.commands.hologram.edit.move_to")
    public void moveTo(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull String x,
            final @NotNull String y,
            final @NotNull String z,
            final @Optional String yaw,
            final @Optional String pitch
    ) {
        Player player = actor.requirePlayer();

        final var parsedX = calculateCoordinate(x, hologram.getData().getLocation(), player.getLocation(), Location::x);
        final var parsedY = calculateCoordinate(y, hologram.getData().getLocation(), player.getLocation(), Location::y);
        final var parsedZ = calculateCoordinate(z, hologram.getData().getLocation(), player.getLocation(), Location::z);

        if (parsedX == null || parsedY == null || parsedZ == null) {
            translator.translate("commands.hologram.edit.move_to.invalid_position")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        final var location = new Location(player.getWorld(), parsedX, parsedY, parsedZ,
                hologram.getData().getLocation().getYaw(),
                hologram.getData().getLocation().getPitch());

        if (yaw != null) {
            final var parsedYaw = calculateCoordinate(yaw, hologram.getData().getLocation(), player.getLocation(), loc -> loc.getYaw() + 180f);
            if (parsedYaw == null) {
                translator.translate("commands.hologram.edit.move_to.invalid_yaw")
                        .withPrefix()
                        .send(actor.sender());
                return;
            }
            location.setYaw(parsedYaw.floatValue() - 180f);
        }

        if (pitch != null) {
            final var parsedPitch = calculateCoordinate(pitch, hologram.getData().getLocation(), player.getLocation(), Location::getPitch);
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

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.position.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("x", Formats.COORDINATES_DECIMAL.format(location.x()))
                .replace("y", Formats.COORDINATES_DECIMAL.format(location.y()))
                .replace("z", Formats.COORDINATES_DECIMAL.format(location.z()))
                .send(actor.sender());
    }

    public static @Nullable Double calculateCoordinate(@NotNull final String text, @Nullable final Location originLocation, @NotNull final Location callerLocation, @NotNull final Function<Location, Number> extractor) {
        final var number = Doubles.tryParse(StringUtils.stripStart(text, "~"));
        final var target = text.startsWith("~~") ? callerLocation : text.startsWith("~") ? originLocation : null;

        if (number == null) {
            return target == null ? null : extractor.apply(target).doubleValue();
        }

        if (target == null) {
            return number;
        }

        return number + extractor.apply(target).doubleValue();
    }
}
