package com.fancyinnovations.fancyholograms.commands.hologram;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.Bukkit;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class BlockStateCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (hologram == null || !(hologram.getData() instanceof BlockHologramData blockData)) {
            return Collections.emptyList();
        }

        try {
            BlockData defaultBlockData = blockData.getBlock().createBlockData();

            if (args.length == 4) {
                return Arrays.stream(defaultBlockData.getAsString(true)
                        .replaceAll("^[^\\[]*\\[", "")
                        .replaceAll("\\]$", "")
                        .split(","))
                        .map(s -> s.split("=")[0])
                        .filter(prop -> prop.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args.length == 5) {
                String propertyName = args[3];
                String currentState = defaultBlockData.getAsString(true);

                for (String prop : currentState.replaceAll("^[^\\[]*\\[", "").replaceAll("\\]$", "").split(",")) {
                    String[] parts = prop.split("=");
                    if (parts[0].equals(propertyName)) {
                        List<String> possibleValues = new ArrayList<>();

                        possibleValues.add("true");
                        possibleValues.add("false");

                        possibleValues.addAll(Arrays.asList("north", "south", "east", "west", "up", "down"));

                        for (int i = 0; i <= 15; i++) {
                            possibleValues.add(String.valueOf(i));
                        }

                        return possibleValues.stream()
                                .filter(val -> {
                                    try {
                                        Bukkit.createBlockData(blockData.getBlock(), propertyName + "=" + val);
                                        return true;
                                    } catch (IllegalArgumentException e) {
                                        return false;
                                    }
                                })
                                .filter(val -> val.toLowerCase().startsWith(args[4].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
            }
        } catch (Exception e) {
            return Collections.emptyList();
        }

        return Collections.emptyList();
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {

        if (!(player.hasPermission("fancyholograms.hologram.edit.block_state"))) {
            MessageHelper.error(player, "You don't have the required permission to change the block state of this hologram");
            return false;
        }

        if (!(hologram.getData() instanceof BlockHologramData blockData)) {
            MessageHelper.error(player, "This command can only be used on block holograms");
            return false;
        }

        // If no additional args, clear the block state
        if (args.length < 4) {
            final var copied = blockData.copy(blockData.getName());
            copied.setBlockState(null);

            if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BLOCK_STATE)) {
                return false;
            }

            blockData.setBlockState(null);

            if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
                FancyHologramsPlugin.get().getStorage().save(hologram.getData());
            }

            MessageHelper.success(player, "Cleared block state (using default)");
            return true;
        }

        // Parse property=value pairs from args
        // Format: /hologram edit <name> blockstate <property> <value> [property] [value] ...

        // Start with existing properties or create default
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

        // Apply the new properties (overwriting existing ones)
        for (int i = 3; i < args.length; i += 2) {
            if (i + 1 >= args.length) {
                MessageHelper.error(player, "Missing value for property: " + args[i]);
                return false;
            }

            String property = args[i];
            String value = args[i + 1];
            existingProperties.put(property, value);
        }

        // Build the block state string with brackets format
        String blockStateString = existingProperties.entrySet().stream()
                .sorted(Map.Entry.comparingByKey()) // Sort for consistency
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(","));

        // Validate the block state by creating block data with full string format
        try {
            String fullBlockString = blockData.getBlock().getKey().toString() + "[" + blockStateString + "]";
            Bukkit.createBlockData(fullBlockString);
        } catch (IllegalArgumentException e) {
            MessageHelper.error(player, "Invalid block state: " + e.getMessage());
            return false;
        }

        if (java.util.Objects.equals(blockStateString, blockData.getBlockState())) {
            MessageHelper.warning(player, "This block state is already set");
            return false;
        }

        final var copied = blockData.copy(blockData.getName());
        copied.setBlockState(blockStateString);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.BLOCK_STATE)) {
            return false;
        }

        if (java.util.Objects.equals(blockStateString, blockData.getBlockState())) {
            MessageHelper.warning(player, "This block state is already set");
            return false;
        }

        blockData.setBlockState(blockStateString);

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Set block state: " + blockStateString);

        return true;
    }
}
