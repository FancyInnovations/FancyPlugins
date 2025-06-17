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

public class SwapLinesCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(player.hasPermission("fancyholograms.hologram.edit.line.swap"))) {
            MessageHelper.error(player, "You don't have the required permission to swap lines on this hologram");
            return false;
        }

        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }

        if (args.length < 5) {
            MessageHelper.error(player, "Wrong usage: /hologram help");
            return false;
        }

        final var parsedIndex1 = Ints.tryParse(args[3]);
        final var parsedIndex2 = Ints.tryParse(args[4]);

        if (parsedIndex1 == null || parsedIndex2 == null) {
            MessageHelper.error(player, "Could not parse one or both line numbers");
            return false;
        }

        final var lines = new ArrayList<>(textData.getText());

        if (parsedIndex1 < 0 || parsedIndex1 > lines.size() || parsedIndex2 < 0 || parsedIndex2 > lines.size()) {
            MessageHelper.error(player, "Invalid line index");
            return false;
        }

        final var index1 = parsedIndex1 - 1;
        final var index2 = parsedIndex2 - 1;

        if(index1 == index2) {
            MessageHelper.error(player, "Cannot swap a line with itself");
            return false;
        }

        if(index1 < 0 || index2 < 0) {
            MessageHelper.error(player, "Invalid line index");
            return false;
        }

        String tempLine = lines.get(index1);
        lines.set(index1, lines.get(index2));
        lines.set(index2, tempLine);

        final var copied = textData.copy(textData.getName());
        copied.setText(lines);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return false;
        }

        textData.setText(copied.getText());

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(player, "Swapped lines " + parsedIndex1 + " and " + parsedIndex2);
        return true;
    }
}
