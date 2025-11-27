package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public final class BlockStateCMD {

    public static final BlockStateCMD INSTANCE = new BlockStateCMD();

    private BlockStateCMD() {
    }

    @Command("hologram-new edit <hologram> blockstate <args>")
    @Description("Sets or clears block state properties")
    @CommandPermission("fancyholograms.hologram.edit.block_state")
    public void blockState(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @Default("") String args
    ) {
        if (!(hologram.getData() instanceof BlockHologramData blockData)) {
            MessageHelper.error(actor.sender(), "This command can only be used on block holograms");
            return;
        }

        // Split args by spaces
        String[] argArray = args.trim().isEmpty() ? new String[0] : args.split("\\s+");

        // If no args, clear the block state
        if (argArray.length == 0) {
            final var copied = blockData.copy(blockData.getName());
            copied.setBlockState(null);

            if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.BLOCK_STATE)) {
                return;
            }

            blockData.setBlockState(null);

            if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
                FancyHologramsPlugin.get().getStorage().save(hologram.getData());
            }

            MessageHelper.success(actor.sender(), "Cleared block state (using default)");
            return;
        }

        // Start with existing properties
        Map<String, String> existingProperties = new HashMap<>();

        // Parse existing block state if present
        if (blockData.getBlockState() != null && !blockData.getBlockState().isEmpty()) {
            String[] props = blockData.getBlockState().split(",");
            for (String prop : props) {
                String[] parts = prop.split("=", 2);
                if (parts.length == 2) {
                    existingProperties.put(parts[0].trim(), parts[1].trim());
                }
            }
        }

        // Parse property-value pairs from args
        for (int i = 0; i < argArray.length; i += 2) {
            if (i + 1 >= argArray.length) {
                MessageHelper.error(actor.sender(), "Missing value for property: " + argArray[i]);
                return;
            }
            existingProperties.put(argArray[i], argArray[i + 1]);
        }

        // Build the block state string
        String blockStateString = existingProperties.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(","));

        // Validate the block state
        try {
            String fullBlockString = blockData.getBlock().getKey().toString() + "[" + blockStateString + "]";
            Bukkit.createBlockData(fullBlockString);
        } catch (IllegalArgumentException e) {
            MessageHelper.error(actor.sender(), "Invalid block state: " + e.getMessage());
            return;
        }

        if (java.util.Objects.equals(blockStateString, blockData.getBlockState())) {
            MessageHelper.warning(actor.sender(), "This block state is already set");
            return;
        }

        final var copied = blockData.copy(blockData.getName());
        copied.setBlockState(blockStateString);

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.BLOCK_STATE)) {
            return;
        }

        blockData.setBlockState(blockStateString);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(actor.sender(), "Set block state: " + blockStateString);
    }
}
