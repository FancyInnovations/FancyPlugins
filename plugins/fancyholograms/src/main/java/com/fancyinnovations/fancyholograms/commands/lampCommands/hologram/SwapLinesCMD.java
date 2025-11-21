package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.SwapLinesSuggestion;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
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

    private SwapLinesCMD() {
    }

    @Command("hologram-new edit <hologram> swapLines <line1> <line2>")
    @Description("Swaps two lines")
    @CommandPermission("fancyholograms.hologram.edit.swap_lines")
    public void swapLines(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(SwapLinesSuggestion.class) int line1,
            final @NotNull @SuggestWith(SwapLinesSuggestion.class) int line2
    ) {
        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(actor.sender(), "This command can only be used on text holograms");
            return;
        }

        List<String> text = textData.getText();

        if (line1 < 1 || line1 > text.size()) {
            MessageHelper.error(actor.sender(), "First line number is out of range (1-" + text.size() + ")");
            return;
        }

        if (line2 < 1 || line2 > text.size()) {
            MessageHelper.error(actor.sender(), "Second line number is out of range (1-" + text.size() + ")");
            return;
        }

        if (line1 == line2) {
            MessageHelper.warning(actor.sender(), "Cannot swap a line with itself");
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

        MessageHelper.success(actor.sender(), "Swapped line " + line1 + " with line " + line2);
    }
}