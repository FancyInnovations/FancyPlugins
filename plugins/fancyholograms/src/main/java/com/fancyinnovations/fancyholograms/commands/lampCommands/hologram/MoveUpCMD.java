package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.MoveLineUpSuggestion;
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

public final class MoveUpCMD {

    public static final MoveUpCMD INSTANCE = new MoveUpCMD();
    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private MoveUpCMD() {
    }

    @IsHologramType(types = HologramType.TEXT)
    @Command("hologram-new edit <hologram> move_line_up <line>")
    @Description("Moves a line up by one position")
    @CommandPermission("fancyholograms.hologram.edit.move_line")
    public void moveLineUp(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(MoveLineUpSuggestion.class) int line
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        List<String> text = textData.getText();

        if (line < 2 || line > text.size()) {
            translator.translate("commands.hologram.edit.lines.line_number_out_of_bounds")
                    .withPrefix()
                    .replace("line", String.valueOf(line))
                    .replace("min", "2")
                    .replace("max", String.valueOf(text.size()))
                    .send(actor.sender());
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

        translator.translate("commands.hologram.edit.lines.move_success")
                .withPrefix()
                .replace("line", String.valueOf(line))
                .replace("position", String.valueOf(line - 1))
                .send(actor.sender());
    }
}