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

import java.util.ArrayList;
import java.util.List;

public final class SwapLinesCMD {

    public static final SwapLinesCMD INSTANCE = new SwapLinesCMD();
    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private SwapLinesCMD() {
    }

    @IsHologramType(types = HologramType.TEXT)
    @Command("hologram-new edit <hologram> swap_lines <line1> <line2>")
    @Description("Swaps two lines")
    @CommandPermission("fancyholograms.hologram.edit.move_line")
    public void swapLines(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(SwapLinesSuggestion.class) int line1,
            final @NotNull @SuggestWith(SwapLinesSuggestion.class) int line2
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        List<String> text = textData.getText();

        if (line1 < 1 || line1 > text.size()) {
            translator.translate("commands.hologram.edit.lines.line_number_out_of_bounds")
                    .withPrefix()
                    .replace("line", String.valueOf(line1))
                    .replace("min", "1")
                    .replace("max", String.valueOf(text.size()))
                    .send(actor.sender());
            return;
        }

        if (line2 < 1 || line2 > text.size()) {
            translator.translate("commands.hologram.edit.lines.line_number_out_of_bounds")
                    .withPrefix()
                    .replace("line", String.valueOf(line2))
                    .replace("min", "1")
                    .replace("max", String.valueOf(text.size()))
                    .send(actor.sender());
            return;
        }

        if (line1 == line2) {
            translator.translate("commands.hologram.edit.lines.cannot_swap_same_line")
                    .withPrefix()
                    .replace("line", String.valueOf(line1))
                    .send(actor.sender());
            return;
        }

        final var copied = textData.copy(textData.getName());
        List<String> newText = new ArrayList<>(text);

        String temp = newText.get(line1 - 1);
        newText.set(line1 - 1, newText.get(line2 - 1));
        newText.set(line2 - 1, temp);

        copied.setText(newText);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return;
        }

        textData.setText(newText);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.lines.swap_success")
                .withPrefix()
                .replace("line1", String.valueOf(line1))
                .replace("line2", String.valueOf(line2))
                .send(actor.sender());
    }
}