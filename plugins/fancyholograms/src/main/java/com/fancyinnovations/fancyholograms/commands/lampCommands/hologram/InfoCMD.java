package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.*;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.Formats;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class InfoCMD {

    public static final InfoCMD INSTANCE = new InfoCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private InfoCMD() {
    }

    @Command("hologram-new info <hologram>")
    @Description("Shows information about a hologram")
    @CommandPermission("fancyholograms.commands.hologram.info")
    public void info(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        HologramData data = hologram.getData();

        translator.translate("commands.hologram.info.header")
                .withPrefix()
                .replace("name", data.getName())
                .send(actor.sender());

        translator.translate("commands.hologram.info.name")
                .replace("name", data.getName())
                .send(actor.sender());

        translator.translate("commands.hologram.info.type")
                .replace("type", data.getType().name())
                .send(actor.sender());

        translator.translate("commands.hologram.info.location")
                .replace("world", data.getWorldName() != null ? data.getWorldName() : "unknown")
                .replace("x", Formats.COORDINATES_DECIMAL.format(data.getLocation().getX()))
                .replace("y", Formats.COORDINATES_DECIMAL.format(data.getLocation().getY()))
                .replace("z", Formats.COORDINATES_DECIMAL.format(data.getLocation().getZ()))
                .send(actor.sender());

        if (data.getLocation().getWorld() == null) {
            translator.translate("commands.hologram.info.world_not_loaded")
                    .send(actor.sender());
        }

        translator.translate("commands.hologram.info.visibility_distance")
                .replace("distance", String.valueOf(data.getVisibilityDistance()))
                .send(actor.sender());

        if (data instanceof DisplayHologramData displayData) {
            Vector3f scale = displayData.getScale();
            String scaleStr = (scale.x() == scale.y() && scale.y() == scale.z())
                    ? "x" + scale.x()
                    : scale.x() + ", " + scale.y() + ", " + scale.z();

            translator.translate("commands.hologram.info.scale")
                    .replace("scale", scaleStr)
                    .send(actor.sender());

            translator.translate("commands.hologram.info.billboard")
                    .replace("billboard", displayData.getBillboard().name())
                    .send(actor.sender());

            translator.translate("commands.hologram.info.shadow_radius")
                    .replace("radius", String.valueOf(displayData.getShadowRadius()))
                    .send(actor.sender());

            translator.translate("commands.hologram.info.shadow_strength")
                    .replace("strength", String.valueOf(displayData.getShadowStrength()))
                    .send(actor.sender());
        }

        if (data.getLinkedNpcName() != null) {
            translator.translate("commands.hologram.info.linked_npc")
                    .replace("npc", data.getLinkedNpcName())
                    .send(actor.sender());
        }

        if (data instanceof TextHologramData textData) {
            translator.translate("commands.hologram.info.text_header")
                    .send(actor.sender());

            for (String line : textData.getText()) {
                translator.translate("commands.hologram.info.text_line")
                        .replace("line", line)
                        .send(actor.sender());
            }

            String bgStr = textData.getBackground() != null
                    ? "#" + Integer.toHexString(textData.getBackground().asARGB())
                    : "default";
            translator.translate("commands.hologram.info.background")
                    .replace("background", bgStr)
                    .send(actor.sender());

            translator.translate("commands.hologram.info.text_alignment")
                    .replace("alignment", textData.getTextAlignment().name())
                    .send(actor.sender());

            translator.translate("commands.hologram.info.see_through")
                    .replace("enabled", textData.isSeeThrough() ? "enabled" : "disabled")
                    .send(actor.sender());

            translator.translate("commands.hologram.info.text_shadow")
                    .replace("enabled", textData.hasTextShadow() ? "enabled" : "disabled")
                    .send(actor.sender());

            if (textData.getTextUpdateInterval() == -1) {
                translator.translate("commands.hologram.info.update_text_interval_disabled")
                        .send(actor.sender());
            } else {
                translator.translate("commands.hologram.info.update_text_interval")
                        .replace("interval", String.valueOf(textData.getTextUpdateInterval()))
                        .send(actor.sender());
            }
        } else if (data instanceof BlockHologramData blockData) {
            translator.translate("commands.hologram.info.block")
                    .replace("block", blockData.getBlock().name())
                    .send(actor.sender());
        } else if (data instanceof ItemHologramData itemData) {
            translator.translate("commands.hologram.info.item")
                    .replace("item", itemData.getItemStack().getType().name())
                    .send(actor.sender());
        }
    }
}
