package com.fancyinnovations.fancyholograms.commands.hologram;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancylib.colors.GlowingColor;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GlowingCMD implements Subcommand {

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.glowing"))) {
            MessageHelper.error(player, "You don't have the required permission to edit a hologram");
            return false;
        }
        // Check if hologram is ITEM or BLOCK type
        if (!(hologram.getData() instanceof DisplayHologramData displayData) ||
                (!(hologram.getData() instanceof ItemHologramData) && !(hologram.getData() instanceof BlockHologramData))) {
            translator.translate("commands.hologram.edit.glowing.only_item_block")
                    .send(player);
            return false;
        }

        String colorStr = args[3];
        GlowingColor color = GlowingColor.valueOf(colorStr.toUpperCase());

        // Handle disabled state
        if (color == GlowingColor.DISABLED) {
            // Create copy for event
            final var copied = displayData.copy(displayData.getName());
            copied.setGlowingColor(GlowingColor.DISABLED);

            // Call modification event
            if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.GLOWING)) {
                return false;
            }

            // Apply change
            displayData.setGlowingColor(GlowingColor.DISABLED);

            // Auto-save if enabled
            if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                plugin.getStorage().save(hologram.getData());
            }

            translator.translate("commands.hologram.edit.glowing.disabled")
                    .replace("hologram", hologram.getData().getName())
                    .send(player);
        } else {
            // Create copy for event
            final var copied = displayData.copy(displayData.getName());
            copied.setGlowingColor(color);

            // Call modification event
            if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.GLOWING)) {
                return false;
            }

            // Apply change
            displayData.setGlowingColor(color);

            // Auto-save if enabled
            if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                plugin.getStorage().save(hologram.getData());
            }

            translator.translate("commands.hologram.edit.glowing.color_set")
                    .replace("hologram", hologram.getData().getName())
                    .replace("color", ((SimpleMessage) translator.translate(color.getTranslationKey())).getMessage())
                    .send(player);
        }

        return true;
    }
}
