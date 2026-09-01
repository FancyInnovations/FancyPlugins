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

public final class TranslateCMD {

    public static final TranslateCMD INSTANCE = new TranslateCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private TranslateCMD() {
    }

    @Command("hologram-new edit <hologram> translate <x>")
    @Description("Changes the translation of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.translate")
    public void translateUniform(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final float x
    ) {
        translate(actor, hologram, x, x, x);
    }

    @Command("hologram-new edit <hologram> translate <x> <y> <z>")
    @Description("Changes the translation of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.translate")
    public void translate(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final float x,
            final float y,
            final float z
    ) {
        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            translator.translate("commands.hologram.edit.translate.not_display")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        Vector3f currentTranslation = displayData.getTranslation();
        if (currentTranslation != null &&
                Float.compare(x, currentTranslation.x()) == 0 &&
                Float.compare(y, currentTranslation.y()) == 0 &&
                Float.compare(z, currentTranslation.z()) == 0) {
            translator.translate("commands.hologram.edit.translate.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
            return;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setTranslation(new Vector3f(x, y, z));

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TRANSLATION)) {
            return;
        }

        displayData.setTranslation(new Vector3f(
                copied.getTranslation().x(),
                copied.getTranslation().y(),
                copied.getTranslation().z()
        ));

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.translate.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("x", String.valueOf(x))
                .replace("y", String.valueOf(y))
                .replace("z", String.valueOf(z))
                .send(actor.sender());
    }
}
