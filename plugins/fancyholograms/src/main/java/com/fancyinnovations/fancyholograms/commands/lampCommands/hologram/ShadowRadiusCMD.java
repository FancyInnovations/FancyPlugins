package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class ShadowRadiusCMD {

    public static final ShadowRadiusCMD INSTANCE = new ShadowRadiusCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private ShadowRadiusCMD() {
    }

    @Command("hologram-new edit <hologram> shadow_radius <radius>")
    @Description("Changes the shadow radius of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.shadow_radius")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final float radius
    ) {
        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            translator.translate("commands.hologram.edit.shadow_radius.not_display")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        if (Float.compare(radius, displayData.getShadowRadius()) == 0) {
            translator.translate("commands.hologram.edit.shadow_radius.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
            return;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setShadowRadius(radius);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.SHADOW_RADIUS)) {
            return;
        }

        displayData.setShadowRadius(copied.getShadowRadius());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.shadow_radius.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("radius", String.valueOf(radius))
                .send(actor.sender());
    }
}
