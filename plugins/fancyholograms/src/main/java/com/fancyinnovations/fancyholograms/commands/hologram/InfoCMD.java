package com.fancyinnovations.fancyholograms.commands.hologram;

import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.List;

public class InfoCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.info"))) {
            MessageHelper.error(player, "You don't have the required permission to view the info of a hologram");
            return false;
        }

        HologramData data = hologram.getData();

        MessageHelper.info(player, "<b>Information about the " + hologram.getData().getName() + " hologram:");
        MessageHelper.info(player, "Name: <gray>" + hologram.getData().getName());
        MessageHelper.info(player, "Type: <gray>" + hologram.getData().getType().name());
        MessageHelper.info(player, "Location: <gray>" + data.getWorldName() + " " + data.getLocation().getX() + " / " + data.getLocation().getY() + " / " + data.getLocation().getZ());
        if (data.getLocation().getWorld() == null) {
            MessageHelper.warning(player, "The world of this hologram is not loaded.");
        }

        MessageHelper.info(player, "Visibility distance: <gray>" + data.getVisibilityDistance() + " blocks");

        if (data instanceof com.fancyinnovations.fancyholograms.api.data.DisplayHologramData displayData) {
            Vector3f scale = displayData.getScale();
            if (scale.x() == scale.y() && scale.y() == scale.z()) {
                MessageHelper.info(player, "Scale: <gray>x" + displayData.getScale().x());
            } else {
                MessageHelper.info(player, "Scale: <gray>" + displayData.getScale().x() + ", " + displayData.getScale().y() + ", " + displayData.getScale().z());
            }

            MessageHelper.info(player, "Billboard: <gray>" + displayData.getBillboard().name());
            MessageHelper.info(player, "Shadow radius: <gray>" + displayData.getShadowRadius());
            MessageHelper.info(player, "Shadow strength: <gray>" + displayData.getShadowStrength());
        }

        if (data.getLinkedNpcName() != null) {
            MessageHelper.info(player, "Linked npc: <gray>" + data.getLinkedNpcName());
        }

        if (data instanceof com.fancyinnovations.fancyholograms.api.data.TextHologramData textData) {
            MessageHelper.info(player, "Text: ");
            for (String line : textData.getText()) {
                MessageHelper.info(player, " <reset> " + line);
            }

            if (textData.getBackground() != null) {
                MessageHelper.info(player, "Background: <gray>" + '#' + Integer.toHexString(textData.getBackground().asARGB()));
            } else {
                MessageHelper.info(player, "Background: <gray>default");
            }

            MessageHelper.info(player, "Text alignment: <gray>" + textData.getTextAlignment().name());
            MessageHelper.info(player, "See through: <gray>" + (textData.isSeeThrough() ? "enabled" : "disabled"));
            MessageHelper.info(player, "Text shadow: <gray>" + (textData.hasTextShadow() ? "enabled" : "disabled"));
            if (textData.getTextUpdateInterval() == -1) {
                MessageHelper.info(player, "Update text interval: <gray>not updating");
            } else {
                MessageHelper.info(player, "Update text interval: <gray>" + textData.getTextUpdateInterval() + " ticks");
            }
        } else if (data instanceof com.fancyinnovations.fancyholograms.api.data.BlockHologramData blockData) {
            MessageHelper.info(player, "Block: <gray>" + blockData.getBlock().name());
        } else if (data instanceof com.fancyinnovations.fancyholograms.api.data.ItemHologramData itemData) {
            MessageHelper.info(player, "Item: <gray>" + itemData.getItemStack().getType().name());
        }

        return true;
    }
}
