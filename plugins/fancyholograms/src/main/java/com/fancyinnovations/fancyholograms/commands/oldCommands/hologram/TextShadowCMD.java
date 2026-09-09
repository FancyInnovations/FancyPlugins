package com.fancyinnovations.fancyholograms.commands.oldCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.oldCommands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.oldCommands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class TextShadowCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.text_shadow"))) {
            MessageHelper.error(player, "You don't have the required permission to edit a hologram");
            return false;
        }

        if (!(hologram.getData() instanceof TextHologramData textData)) {
            MessageHelper.error(player, "This command can only be used on text holograms");
            return false;
        }

        final var enabled = switch (args[3].toLowerCase(Locale.ROOT)) {
            case "true" -> true;
            case "false" -> false;
            default -> null;
        };

        if (enabled == null) {
            MessageHelper.error(player, "Could not parse text shadow flag");
            return false;
        }

        if (enabled == textData.hasTextShadow()) {
            MessageHelper.warning(player, "This hologram already has text shadow " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        final var copied = textData.copy(textData.getName());
        copied.setTextShadow(enabled);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.TEXT_SHADOW)) {
            return false;
        }

        if (enabled == textData.hasTextShadow()) {
            MessageHelper.warning(player, "This hologram already has text shadow " + (enabled ? "enabled" : "disabled"));
            return false;
        }

        textData.setTextShadow(copied.hasTextShadow());

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Changed text shadow");
        return true;
    }
}
