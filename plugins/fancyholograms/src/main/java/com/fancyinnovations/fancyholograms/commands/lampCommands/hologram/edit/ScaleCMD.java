package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import com.fancyinnovations.fancyholograms.commands.oldCommands.HologramCMD;
import com.google.common.primitives.Floats;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class ScaleCMD extends FancyContext {

    public static final ScaleCMD INSTANCE = new ScaleCMD();

    private ScaleCMD() {
    }

    private static @Nullable Float parseScale(@NotNull String input) {
        return Floats.tryParse(input.trim().replace(',', '.'));
    }

    private static boolean isValidScale(@Nullable Float val) {
        return val != null && !Float.isNaN(val) && !Float.isInfinite(val) && val >= 0.01f && val <= 50.0f;
    }

    @Command("hologram edit <hologram> scale <factor>")
    @Description("Changes the scale of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.scale")
    public void scaleUniform(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull String factor
    ) {
        Float f = parseScale(factor);
        if (!isValidScale(f)) {
            translator.translate("commands.hologram.edit.scale.invalid")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        scale(actor, hologram, f, f, f);
    }

    @Command("hologram edit <hologram> scale <x> <y> <z>")
    @Description("Changes the scale of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.scale")
    public void scale(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull String xStr,
            final @NotNull String yStr,
            final @NotNull String zStr
    ) {
        Float x = parseScale(xStr);
        Float y = parseScale(yStr);
        Float z = parseScale(zStr);
        if (!isValidScale(x) || !isValidScale(y) || !isValidScale(z)) {
            translator.translate("commands.hologram.edit.scale.invalid")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        scale(actor, hologram, x, y, z);
    }

    private void scale(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final float x,
            final float y,
            final float z
    ) {
        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            translator.translate("commands.hologram.edit.scale.not_display")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        if (Float.compare(x, displayData.getScale().x()) == 0 &&
                Float.compare(y, displayData.getScale().y()) == 0 &&
                Float.compare(z, displayData.getScale().z()) == 0) {
            translator.translate("commands.hologram.edit.scale.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
            return;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setScale(new Vector3f(x, y, z));

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.SCALE)) {
            return;
        }

        displayData.setScale(new Vector3f(
                copied.getScale().x(),
                copied.getScale().y(),
                copied.getScale().z()
        ));

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.scale.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("x", String.valueOf(x))
                .replace("y", String.valueOf(y))
                .replace("z", String.valueOf(z))
                .send(actor.sender());
    }
}
