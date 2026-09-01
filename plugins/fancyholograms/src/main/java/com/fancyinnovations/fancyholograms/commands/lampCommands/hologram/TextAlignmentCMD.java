package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class TextAlignmentCMD {

    public static final TextAlignmentCMD INSTANCE = new TextAlignmentCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private TextAlignmentCMD() {
    }

    @IsHologramType(types = {HologramType.TEXT})
    @Command("hologram-new edit <hologram> text_alignment <alignment>")
    @Description("Sets the text alignment of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.text_alignment")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull TextDisplay.TextAlignment alignment
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        if (alignment == textData.getTextAlignment()) {
            translator.translate("commands.hologram.edit.text_alignment.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("alignment", alignment.name())
                    .send(actor.sender());
            return;
        }

        final var copied = textData.copy(textData.getName());
        copied.setTextAlignment(alignment);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT_ALIGNMENT)) {
            return;
        }

        textData.setTextAlignment(copied.getTextAlignment());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.text_alignment.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("alignment", alignment.name())
                .send(actor.sender());
    }
}
