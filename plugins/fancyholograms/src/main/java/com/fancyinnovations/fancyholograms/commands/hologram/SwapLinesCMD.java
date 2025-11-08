package com.fancyinnovations.fancyholograms.commands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.NumberHelper;
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

        if (!(player.hasPermission("fancyholograms.hologram.edit.swap_lines"))) {
            MessageHelper.error(player, "You don't have the required permission to swap lines");
            return false;
        }

        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }

        if (args.length < 5) {
            MessageHelper.error(player, "Usage: /hologram edit <name> swap_lines <first> <second>");
            return false;
        }

        final var firstLine = NumberHelper.parseInt(args[3]);
        final var secondLine = NumberHelper.parseInt(args[4]);

        if (firstLine.isEmpty() || secondLine.isEmpty()) {
            MessageHelper.error(player, "Invalid line numbers");
            return false;
        }

        int first = firstLine.get();
        int second = secondLine.get();

        List<String> text = textData.getText();

        if (first < 1 || first > text.size()) {
            MessageHelper.error(player, "First line number is out of range (1-" + text.size() + ")");
            return false;
        }

        if (second < 1 || second > text.size()) {
            MessageHelper.error(player, "Second line number is out of range (1-" + text.size() + ")");
            return false;
        }

        if (first == second) {
            MessageHelper.warning(player, "Cannot swap a line with itself");
            return false;
        }

        final var copied = textData.copy(textData.getName());
        List<String> newText = new ArrayList<>(text);

        String temp = newText.get(first - 1);
        newText.set(first - 1, newText.get(second - 1));
        newText.set(second - 1, temp);

        copied.setText(newText);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT)) {
            return false;
        }

        textData.setText(newText);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Swapped line " + first + " with line " + second);
        return true;
    }
}
