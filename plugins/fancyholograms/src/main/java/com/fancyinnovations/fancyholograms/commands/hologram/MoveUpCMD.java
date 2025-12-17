package com.fancyinnovations.fancyholograms.commands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoveUpCMD implements Subcommand {

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.move_line"))) {
            MessageHelper.error(player, "You don't have the required permission to edit a hologram");
            return false;
        }

        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }

        int line;
        try {
            line = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            MessageHelper.error(player, "Please provide a valid line number");
            return false;
        }

        List<String> text = textData.getText();

        if (line < 1 || line > text.size()) {
            MessageHelper.error(player, "Line number is out of range (1-" + text.size() + ")");
            return false;
        }

        if (line == 1) {
            MessageHelper.warning(player, "Line 1 cannot be moved up");
            return false;
        }

        final var copied = textData.copy(textData.getName());
        List<String> newText = new ArrayList<>(text);

        String temp = newText.get(line - 1);
        newText.set(line - 1, newText.get(line - 2));
        newText.set(line - 2, temp);

        copied.setText(newText);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return false;
        }

        textData.setText(newText);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Moved line " + line + " up to position " + (line - 1));

        return true;
    }
}
