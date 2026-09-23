package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import com.fancyinnovations.fancyholograms.commands.oldCommands.HologramCMD;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class ShadowStrengthCMD extends FancyContext {

    public static final ShadowStrengthCMD INSTANCE = new ShadowStrengthCMD();

    private ShadowStrengthCMD() {
    }

    @Command("hologram edit <hologram> shadow_strength <strength>")
    @Description("Changes the shadow strength of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.shadow_strength")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final float strength
    ) {
        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            translator.translate("commands.hologram.edit.shadow_strength.not_display")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        if (Float.compare(strength, displayData.getShadowStrength()) == 0) {
            translator.translate("commands.hologram.edit.shadow_strength.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
            return;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setShadowStrength(strength);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.SHADOW_STRENGTH)) {
            return;
        }

        displayData.setShadowStrength(copied.getShadowStrength());

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.shadow_strength.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("strength", String.valueOf(strength))
                .send(actor.sender());
    }
}
