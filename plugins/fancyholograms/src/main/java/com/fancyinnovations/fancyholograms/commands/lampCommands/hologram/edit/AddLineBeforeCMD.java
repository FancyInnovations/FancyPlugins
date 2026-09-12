package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.SwapLinesSuggestion;
import com.fancyinnovations.fancyholograms.commands.oldCommands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;

public final class AddLineBeforeCMD {

    public static final AddLineBeforeCMD INSTANCE = new AddLineBeforeCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private AddLineBeforeCMD() {
    }

    @IsHologramType(types = {HologramType.TEXT})
    @Command("hologram edit <hologram> add_line_before <line> <text>")
    @Description("Adds a line before a specific line in the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.add_line_before")
    public void addLineBefore(
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

        final var lines = new ArrayList<>(textData.getText());
        lines.add(line - 1, text);

        final var copied = textData.copy(textData.getName());
        copied.setText(lines);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return;
        }

        textData.setText(lines);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.lines.inserted")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("line", String.valueOf(line))
                .send(actor.sender());
    }
}
