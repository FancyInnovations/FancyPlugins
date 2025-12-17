package com.fancyinnovations.fancyholograms.commands.hologram;

import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.utils.GlowingColor;
import de.oliver.fancylib.MessageHelper;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class GlowingCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (args.length == 4) {
            return Arrays.stream(GlowingColor.values())
                    .map(color -> color.name().toLowerCase(Locale.ROOT))
                    .collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(player.hasPermission("fancyholograms.hologram.edit.glowing"))) {
            MessageHelper.error(player, "You don't have the required permission to change the glowing of a hologram");
            return false;
        }

        if (!(hologram.getData() instanceof DisplayHologramData displayData)) {
            MessageHelper.error(player, "This command can only be used on display holograms");
            return false;
        }

        if (hologram.getData().getType() == com.fancyinnovations.fancyholograms.api.hologram.HologramType.TEXT) {
            MessageHelper.error(player, "You can only make item and block holograms glow");
            return false;
        }

        if (args.length == 3) {
            final var copied = displayData.copy(displayData.getName());
            copied.setGlowing(!displayData.isGlowing());

            if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.GLOWING)) {
                return false;
            }

            displayData.setGlowing(copied.isGlowing());

            if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
                FancyHologramsPlugin.get().getStorage().save(hologram.getData());
            }

            MessageHelper.success(player, "Toggled glowing " + (displayData.isGlowing() ? "on" : "off"));
            return true;
        }

        final var colorArg = args[3].toLowerCase(Locale.ROOT);

        if (colorArg.equals("disabled")) {
            final var copied = displayData.copy(displayData.getName());
            copied.setGlowing(false);

            if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.GLOWING)) {
                return false;
            }

            displayData.setGlowing(false);

            if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
                FancyHologramsPlugin.get().getStorage().save(hologram.getData());
            }

            MessageHelper.success(player, "Disabled glowing");
            return true;
        }

        NamedTextColor color;
        try {
            GlowingColor glowingColor = GlowingColor.valueOf(colorArg.toUpperCase(Locale.ROOT));
            color = glowingColor.getColor();
            if (color == null) {
                MessageHelper.error(player, "Could not parse glowing color");
                return false;
            }
        } catch (IllegalArgumentException e) {
            MessageHelper.error(player, "Could not parse glowing color");
            return false;
        }

        if (Objects.equals(color, displayData.getGlowingColor()) && displayData.isGlowing()) {
            MessageHelper.warning(player, "This hologram already has this glowing color");
            return false;
        }

        final var copied = displayData.copy(displayData.getName());
        copied.setGlowingColor(color);
        copied.setGlowing(true);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.GLOWING_COLOR)) {
            return false;
        }

        if (Objects.equals(copied.getGlowingColor(), displayData.getGlowingColor()) && displayData.isGlowing()) {
            MessageHelper.warning(player, "This hologram already has this glowing color");
            return false;
        }

        displayData.setGlowingColor(copied.getGlowingColor());
        displayData.setGlowing(true);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Changed glowing color");
        return true;
    }
}