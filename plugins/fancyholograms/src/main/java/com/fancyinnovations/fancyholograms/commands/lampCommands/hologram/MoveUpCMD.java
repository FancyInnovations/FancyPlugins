package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.MoveLineUpSuggestion;
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

public final class MoveUpCMD {

    public static final MoveUpCMD INSTANCE = new MoveUpCMD();

    private MoveUpCMD() {
    }

    @Command("hologram-new edit <hologram> moveLineUp <line>")
    @Description("Moves a line up by one position")
    @CommandPermission("fancyholograms.hologram.edit.move_line")
    public void moveLineUp(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(MoveLineUpSuggestion.class) int line
    ) {
        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(actor.sender(), "This command can only be used on text holograms");
            return;
        }

        List<String> text = textData.getText();

        if (line < 1 || line > text.size()) {
            MessageHelper.error(actor.sender(), "Line number is out of range (1-" + text.size() + ")");
            return;
        }

        if (line == 1) {
            MessageHelper.warning(actor.sender(), "Line 1 cannot be moved up");
            return;
        }

        final var copied = textData.copy(textData.getName());
        List<String> newText = new ArrayList<>(text);

        String temp = newText.get(line - 1);
        newText.set(line - 1, newText.get(line - 2));
        newText.set(line - 2, temp);

        copied.setText(newText);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return;
        }

        textData.setText(newText);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(actor.sender(), "Moved line " + line + " up to position " + (line - 1));
    }
}