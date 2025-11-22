package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.colors.GlowingColor;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.translations.message.SimpleMessage;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class GlowingCMD {

    public static final GlowingCMD INSTANCE = new GlowingCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private GlowingCMD() {
    }

    @Command("hologram-new edit <hologram> glowing")
    @Description("Toggle glowing on/off for item and block holograms")
    @CommandPermission("fancyholograms.commands.hologram.glowing")
    public void toggle(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        // Check if hologram is ITEM or BLOCK type
        if (!(hologram.getData() instanceof DisplayHologramData displayData) ||
            (!(hologram.getData() instanceof ItemHologramData) && !(hologram.getData() instanceof BlockHologramData))) {
            translator.translate("commands.hologram.edit.glowing.only_item_block")
                    .send(actor.sender());
            return;
        }

        // Toggle: if currently disabled, enable with white; if enabled, disable
        final GlowingColor newColor = displayData.getGlowingColor() == GlowingColor.DISABLED
                ? GlowingColor.WHITE
                : GlowingColor.DISABLED;

        // Create copy for event
        final var copied = displayData.copy(displayData.getName());
        copied.setGlowingColor(newColor);

        // Call modification event
        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.GLOWING)) {
            return;
        }

        // Apply change
        displayData.setGlowingColor(newColor);

        // Auto-save if enabled
        if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        // Send success message
        if (newColor == GlowingColor.DISABLED) {
            translator.translate("commands.hologram.edit.glowing.disabled")
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
        } else {
            translator.translate("commands.hologram.edit.glowing.enabled")
                    .replace("hologram", hologram.getData().getName())
                    .replace("color", ((SimpleMessage) translator.translate(newColor.getTranslationKey())).getMessage())
                    .send(actor.sender());
        }
    }

    @Command("hologram-new edit <hologram> glowing <color>")
    @Description("Set glowing color for item and block holograms")
    @CommandPermission("fancyholograms.commands.hologram.glowing")
    public void setColor(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull GlowingColor color
    ) {
        // Check if hologram is ITEM or BLOCK type
        if (!(hologram.getData() instanceof DisplayHologramData displayData) ||
            (!(hologram.getData() instanceof ItemHologramData) && !(hologram.getData() instanceof BlockHologramData))) {
            translator.translate("commands.hologram.edit.glowing.only_item_block")
                    .send(actor.sender());
            return;
        }

        // Handle disabled state
        if (color == GlowingColor.DISABLED) {
            // Create copy for event
            final var copied = displayData.copy(displayData.getName());
            copied.setGlowingColor(GlowingColor.DISABLED);

            // Call modification event
            if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.GLOWING)) {
                return;
            }

            // Apply change
            displayData.setGlowingColor(GlowingColor.DISABLED);

            // Auto-save if enabled
            if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                plugin.getStorage().save(hologram.getData());
            }

            translator.translate("commands.hologram.edit.glowing.disabled")
                    .replace("hologram", hologram.getData().getName())
                    .send(actor.sender());
        } else {
            // Create copy for event
            final var copied = displayData.copy(displayData.getName());
            copied.setGlowingColor(color);

            // Call modification event
            if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.GLOWING)) {
                return;
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
                    .send(actor.sender());
        }
    }
}
