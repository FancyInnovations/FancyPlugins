package de.oliver.fancyholograms.commands.hologram;

import com.google.common.primitives.Ints;
import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoveUpCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!player.hasPermission("fancyholograms.hologram.edit.line.moveup")) {
            MessageHelper.error(player, "You don't have the required permission to move lines up");
            return false;
        }

        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }

        if (args.length < 4) {
            MessageHelper.error(player, "Wrong usage: /hologram help");
            return false;
        }

        final var parsedIndex = Ints.tryParse(args[3]);

        if (parsedIndex == null || parsedIndex <= 0) {
            MessageHelper.error(player, "Invalid line index");
            return false;
        }

        final var index = parsedIndex - 1;
        final var lines = new ArrayList<>(textData.getText());

        if (index >= lines.size() || index - 1 < 0) {
            MessageHelper.error(player, "Invalid line index");
            return false;
        }

        String temp = lines.get(index);
        lines.set(index, lines.get(index - 1));
        lines.set(index - 1, temp);

        final var copied = textData.copy(textData.getName());
        copied.setText(lines);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return false;
        }

        textData.setText(copied.getText());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Moved line " + parsedIndex + " up");
        return true;
    }
}
