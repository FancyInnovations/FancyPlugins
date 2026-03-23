package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.MoveLineDownSuggestion;
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

public final class MoveDownCMD {

    public static final MoveDownCMD INSTANCE = new MoveDownCMD();
    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private MoveDownCMD() {
    }

    @IsHologramType(types = HologramType.TEXT)
    @Command("hologram-new edit <hologram> move_line_down <line>")
    @Description("Moves a line down by one position")
    @CommandPermission("fancyholograms.hologram.edit.move_line")
    public void moveLineDown(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(MoveLineDownSuggestion.class) int line
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        List<String> text = textData.getText();

        if (line < 1 || line >= text.size()) {
            translator.translate("commands.hologram.edit.lines.line_number_out_of_bounds")
                    .withPrefix()
                    .replace("line", String.valueOf(line))
                    .replace("min", "1")
                    .replace("max", String.valueOf(text.size() - 1))
                    .send(actor.sender());
            return;
        }

        final var copied = textData.copy(textData.getName());
        List<String> newText = new ArrayList<>(text);

        String temp = newText.get(line - 1);
        newText.set(line - 1, newText.get(line));
        newText.set(line, temp);

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
                .replace("position", String.valueOf(line + 1))
                .send(actor.sender());
    }
}