package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class ScaleCMD {

    public static final ScaleCMD INSTANCE = new ScaleCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private ScaleCMD() {
    }

    @Command("hologram-new edit <hologram> scale <factor>")
    @Description("Changes the scale of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.scale")
    public void scaleUniform(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final float factor
    ) {
        scale(actor, hologram, factor, factor, factor);
    }

    @Command("hologram-new edit <hologram> scale <x> <y> <z>")
    @Description("Changes the scale of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.scale")
    public void scale(
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

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
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
