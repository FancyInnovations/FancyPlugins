package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.BlockMaterialSuggestion;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Range;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

public final class BrightnessCMD {

    public static final BrightnessCMD INSTANCE = new BrightnessCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private BrightnessCMD() {
    }

    @Command("hologram-new edit <hologram> brightness <type> <brightness>")
    @Description("Changes the block or sky brightness of the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.brightness")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull BrightnessType type,
            final @Range(min = 1, max = 15) int brightness
    ) {
        DisplayHologramData data = (DisplayHologramData) hologram.getData();

        Display.Brightness currentBrightness = data.getBrightness();
        if (currentBrightness != null) {
            if (type == BrightnessType.BLOCK && currentBrightness.getBlockLight() == brightness ||
                    type == BrightnessType.SKY && currentBrightness.getSkyLight() == brightness) {
                translator.translate("commands.hologram.edit.brightness.already_set")
                        .withPrefix()
                        .replace("hologram", hologram.getData().getName())
                        .replace("type", type.name())
                        .replace("brightness", String.valueOf(brightness))
                        .send(actor.sender());
                return;
            }
        }


        Display.Brightness newBrightness = new Display.Brightness(
                type == BrightnessType.BLOCK ? brightness : (currentBrightness == null ? 0 : currentBrightness.getBlockLight()),
                type == BrightnessType.SKY ? brightness : (currentBrightness == null ? 0 : currentBrightness.getSkyLight())
        );

        DisplayHologramData copied = data.copy(data.getName());
        copied.setBrightness(newBrightness);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.BRIGHTNESS)) {
            return;
        }

        data.setBrightness(newBrightness);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.brightness.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("type", type.name())
                .replace("brightness", String.valueOf(brightness))
                .send(actor.sender());
    }

    public enum BrightnessType {
        BLOCK,
        SKY
    }
}
