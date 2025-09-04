package de.oliver.fancyholograms.commands.hologram;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.data.TextHologramData;
import de.oliver.fancyholograms.api.events.HologramUpdateEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.commands.HologramCMD;
import de.oliver.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Color;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OpacityCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender sender, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!sender.hasPermission("fancyholograms.hologram.edit.opacity")) {
            MessageHelper.error(sender, "You don't have the required permission to change the opacity of a hologram.");
            return false;
        }

        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(sender, "This command can only be used on text holograms.");
            return false;
        }

        if (args.length < 4) {
            MessageHelper.error(sender, "Wrong usage: /hologram help");
            return false;
        }

        float opacity;

        try {
            opacity = Float.parseFloat(args[3]);
        } catch (NumberFormatException e) {
            MessageHelper.error(sender, "Opacity must be a number between 0 and 1.");
            return false;
        }

        if (opacity < 0 || opacity > 1) {
            MessageHelper.error(sender, "Opacity must be between 0 and 1.");
            return false;
        }

        Color background = textData.getBackground();

        int alpha = opacity == 0 ? 0 : Math.round(opacity * (255 - 26) + 26);

        if (background.getAlpha() == alpha) {
            MessageHelper.warning(sender, "This hologram already has this opacity.");
            return false;
        }

        Color updated = background.setAlpha(alpha);
        final var copied = textData.copy(textData.getName());
        copied.setBackground(updated);

        if (!HologramCMD.callModificationEvent(hologram, sender, copied, HologramUpdateEvent.HologramModification.BACKGROUND)) {
            return false;
        }

        textData.setBackground(updated);

        if (FancyHolograms.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHolograms.get().getHologramStorage().save(hologram);
        }

        MessageHelper.success(sender, "Changed hologram opacity to " + opacity);
        return true;
    }
}
