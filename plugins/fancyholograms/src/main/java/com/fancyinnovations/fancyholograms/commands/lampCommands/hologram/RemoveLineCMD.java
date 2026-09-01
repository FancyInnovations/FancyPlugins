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
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class RemoveLineCMD {

    public static final RemoveLineCMD INSTANCE = new RemoveLineCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private RemoveLineCMD() {
    }

    @IsHologramType(types = {HologramType.TEXT})
    @Command("hologram-new edit <hologram> remove_line")
    @Description("Removes a specific line from the hologram (defaults to the last line)")
    @CommandPermission("fancyholograms.commands.hologram.edit.remove_line")
    public void removeLine(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Optional @SuggestWith(SwapLinesSuggestion.class) Integer line
    ) {
        TextHologramData textData = (TextHologramData) hologram.getData();

        if (textData.getText().isEmpty()) {
            translator.translate("commands.hologram.edit.lines.empty")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
            return;
        }

        int targetLine = (line != null) ? line : textData.getText().size();

        if (targetLine < 1 || targetLine > textData.getText().size()) {
            translator.translate("commands.hologram.edit.lines.line_number_out_of_bounds")
                    .withPrefix()
                    .replace("line", String.valueOf(targetLine))
                    .replace("min", "1")
                    .replace("max", String.valueOf(textData.getText().size()))
                    .send(actor.sender());
            return;
        }

        final var copied = textData.copy(textData.getName());
        copied.removeLine(targetLine - 1);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return;
        }

        textData.removeLine(targetLine - 1);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.lines.removed")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("line", String.valueOf(targetLine))
                .send(actor.sender());
    }
}
