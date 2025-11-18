package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.ItemHologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.utils.GlowingColor;
import de.oliver.fancylib.translations.Translator;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Locale;

public final class GlowingCMD {

    public static final GlowingCMD INSTANCE = new GlowingCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private GlowingCMD() {
    }

    @Command("hologram-new edit <hologram> glowing")
    @Description("Toggles glowing effect for item/block holograms")
    @CommandPermission("fancyholograms.hologram.edit.glowing")
    public void toggle(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        if (hologram.getData() instanceof ItemHologramData itemData) {
            itemData.setGlowing(!itemData.isGlowing());
            if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                plugin.getStorage().save(hologram.getData());
            }
            translator.translate("commands.hologram.glowing.toggled")
                    .replace("state", itemData.isGlowing() ? "on" : "off")
                    .send(actor.sender());
        } else if (hologram.getData() instanceof BlockHologramData blockData) {
            blockData.setGlowing(!blockData.isGlowing());
            if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                plugin.getStorage().save(hologram.getData());
            }
            translator.translate("commands.hologram.glowing.toggled")
                    .replace("state", blockData.isGlowing() ? "on" : "off")
                    .send(actor.sender());
        } else {
            translator.translate("commands.hologram.glowing.only_item_block")
                    .send(actor.sender());
        }
    }

    @Command("hologram-new edit <hologram> glowing disabled")
    @Description("Disables glowing effect")
    @CommandPermission("fancyholograms.hologram.edit.glowing")
    public void disable(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        if (hologram.getData() instanceof ItemHologramData itemData) {
            itemData.setGlowing(false);
            if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                plugin.getStorage().save(hologram.getData());
            }
            translator.translate("commands.hologram.glowing.disabled")
                    .send(actor.sender());
        } else if (hologram.getData() instanceof BlockHologramData blockData) {
            blockData.setGlowing(false);
            if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                plugin.getStorage().save(hologram.getData());
            }
            translator.translate("commands.hologram.glowing.disabled")
                    .send(actor.sender());
        } else {
            translator.translate("commands.hologram.glowing.only_item_block")
                    .send(actor.sender());
        }
    }

    @Command("hologram-new edit <hologram> glowing <color>")
    @Description("Sets glowing color for item/block holograms")
    @CommandPermission("fancyholograms.hologram.edit.glowing")
    public void setColor(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @Named("color") String colorName
    ) {
        NamedTextColor color;
        try {
            GlowingColor glowingColor = GlowingColor.valueOf(colorName.toUpperCase(Locale.ROOT));
            color = glowingColor.getColor();
            if (color == null) {
                translator.translate("commands.hologram.glowing.invalid_color")
                        .send(actor.sender());
                return;
            }
        } catch (IllegalArgumentException e) {
            translator.translate("commands.hologram.glowing.invalid_color")
                    .send(actor.sender());
            return;
        }

        if (hologram.getData() instanceof ItemHologramData itemData) {
            itemData.setGlowingColor(color);
            itemData.setGlowing(true);
            if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                plugin.getStorage().save(hologram.getData());
            }
            translator.translate("commands.hologram.glowing.color_set")
                    .replace("color", colorName.toLowerCase())
                    .send(actor.sender());
        } else if (hologram.getData() instanceof BlockHologramData blockData) {
            blockData.setGlowingColor(color);
            blockData.setGlowing(true);
            if (plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                plugin.getStorage().save(hologram.getData());
            }
            translator.translate("commands.hologram.glowing.color_set")
                    .replace("color", colorName.toLowerCase())
                    .send(actor.sender());
        } else {
            translator.translate("commands.hologram.glowing.only_item_block")
                    .send(actor.sender());
        }
    }
}
