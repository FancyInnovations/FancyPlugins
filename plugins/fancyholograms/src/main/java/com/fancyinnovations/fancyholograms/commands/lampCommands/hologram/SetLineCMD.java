package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.SwapLinesSuggestion;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class SetLineCMD {

    public static final SetLineCMD INSTANCE = new SetLineCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private SetLineCMD() {
    }

    @IsHologramType(types = {HologramType.TEXT})
    @Command("hologram-new edit <hologram> set_line <line> <text>")
    @Description("Sets the text of a specific line in the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.set_line")
    public void setLine(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @SuggestWith(SwapLinesSuggestion.class) int line,
            final @NotNull String text
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        if (line < 1 || line > textData.getText().size()) {
            translator.translate("commands.hologram.edit.lines.line_number_out_of_bounds")
                    .withPrefix()
                    .replace("line", String.valueOf(line))
                    .replace("min", "1")
                    .replace("max", String.valueOf(textData.getText().size()))
                    .send(actor.sender());
            return;
        }

        final var copied = textData.copy(textData.getName());
        copied.setLine(line - 1, text);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return;
        }

        textData.setLine(line - 1, text);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.lines.set")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("line", String.valueOf(line))
                .send(actor.sender());
    }
}
