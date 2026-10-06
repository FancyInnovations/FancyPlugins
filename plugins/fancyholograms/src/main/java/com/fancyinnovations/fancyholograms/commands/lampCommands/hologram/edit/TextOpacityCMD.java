package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.TextOpacitySuggestion;
import com.fancyinnovations.fancyholograms.commands.oldCommands.HologramCMD;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class TextOpacityCMD extends FancyContext {

    public static final TextOpacityCMD INSTANCE = new TextOpacityCMD();

    private TextOpacityCMD() {
    }

    @IsHologramType(types = {HologramType.TEXT})
    @Command("hologram edit <hologram> text_opacity <opacity>")
    @Description("Changes the text opacity of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.text_opacity")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(TextOpacitySuggestion.class) String opacity
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        String rawOpacity = opacity.endsWith("%") ? opacity.substring(0, opacity.length() - 1).trim() : opacity.trim();
        float percent;
        try {
            percent = Float.parseFloat(rawOpacity);
        } catch (NumberFormatException e) {
            translator.translate("commands.hologram.edit.text_opacity.invalid_range")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        if (percent < 0 || percent > 100) {
            translator.translate("commands.hologram.edit.text_opacity.invalid_range")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        byte newOpacity = (byte) Math.round((percent / 100.0f) * 255.0f);
        int formattedPercent = Math.round(percent);

        if (textData.getTextOpacity() == newOpacity) {
            translator.translate("commands.hologram.edit.text_opacity.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("opacity", String.valueOf(formattedPercent))
                    .send(actor.sender());
            return;
        }

        final var copied = textData.copy(textData.getName());
        copied.setTextOpacity(newOpacity);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT_OPACITY)) {
            return;
        }

        textData.setTextOpacity(copied.getTextOpacity());

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.text_opacity.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("opacity", String.valueOf(formattedPercent))
                .send(actor.sender());
    }
}
