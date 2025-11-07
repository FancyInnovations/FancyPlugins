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

import java.util.List;

public class OpacityCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.opacity"))) {
            MessageHelper.error(player, "You don't have the required permission to edit a hologram");
            return false;
        }

        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }

        final var parsedNumber = NumberHelper.parseInt(args[3]);

        if (parsedNumber.isEmpty()) {
            MessageHelper.error(player, "Invalid opacity value.");
            return false;
        }

        final int opacityPercentage = parsedNumber.get();

        if (opacityPercentage < 0 || opacityPercentage > 100) {
            MessageHelper.error(player, "Invalid opacity value, must be between 0 and 100");
            return false;
        }

        // Convert percentage (0-100) to byte value (0-255)
        final byte opacity = (byte) Math.round(opacityPercentage * 255.0 / 100.0);

        if (opacity == textData.getTextOpacity()) {
            MessageHelper.warning(player, "This hologram already has opacity set to " + opacityPercentage + "%");
            return false;
        }

        final var copied = textData.copy(textData.getName());
        copied.setTextOpacity(opacity);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT_OPACITY)) {
            return false;
        }

        if (opacity == textData.getTextOpacity()) {
            MessageHelper.warning(player, "This hologram already has opacity set to " + opacityPercentage + "%");
            return false;
        }

        textData.setTextOpacity(copied.getTextOpacity());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Changed text opacity to " + opacityPercentage + "%");
        return true;
    }
}
