package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.OpacitySuggestion;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class OpacityCMD {

    public static final OpacityCMD INSTANCE = new OpacityCMD();

    private OpacityCMD() {
    }

    @Command("hologram-new edit <hologram> textopacity <opacity>")
    @Description("Changes the opacity of the text hologram")
    @CommandPermission("fancyholograms.hologram.edit.opacity")
    public void setOpacity(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Range(min = 0, max = 100) @SuggestWith(OpacitySuggestion.class) int opacity
    ) {
        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(actor.sender(), "This command can only be used on text holograms");
            return;
        }

        // Convert percentage (0-100) to byte value (0-255)
        final byte opacityByte = (byte) Math.round(opacity * 255.0 / 100.0);

        if (opacityByte == textData.getTextOpacity()) {
            MessageHelper.warning(actor.sender(), "This hologram already has opacity set to " + opacity + "%");
            return;
        }

        final var copied = textData.copy(textData.getName());
        copied.setTextOpacity(opacityByte);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT_OPACITY)) {
            return;
        }

        textData.setTextOpacity(opacityByte);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(actor.sender(), "Changed text opacity to " + opacity + "%");
    }
}
