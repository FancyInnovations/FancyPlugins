package com.fancyinnovations.fancyholograms.api.data;

import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class BlockHologramData extends DisplayHologramData {

    public static final Material DEFAULT_BLOCK = Material.GRASS_BLOCK;

    private Material block = DEFAULT_BLOCK;
    private BlockData blockData;

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public BlockHologramData(String name, Location location) {
        super(name, HologramType.BLOCK, location);
    }

    public Material getBlock() {
        if (blockData != null) {
            return blockData.getMaterial();
        }
        return block;
    }

    public BlockHologramData setBlock(Material block) {
        if (block == null) {
            block = DEFAULT_BLOCK;
        }
        if (!Objects.equals(this.block, block) || this.blockData == null || this.blockData.getMaterial() != block) {
            this.block = block;
            try {
                this.blockData = block.createBlockData();
            } catch (Exception ignored) {
                this.blockData = null;
            }
            setHasChanges(true);
        }

        return this;
    }

    public BlockData getBlockData() {
        if (blockData == null) {
            try {
                blockData = (block != null ? block : DEFAULT_BLOCK).createBlockData();
            } catch (Exception ignored) {
                // Bukkit server not initialized
            }
        }
        return blockData;
    }

    public BlockHologramData setBlockData(BlockData blockData) {
        if (blockData == null) {
            return setBlock(DEFAULT_BLOCK);
        }
        if (!Objects.equals(this.blockData, blockData)) {
            this.blockData = blockData;
            this.block = blockData.getMaterial();
            setHasChanges(true);
        }
        return this;
    }

    public Map<String, String> getBlockStateProperties() {
        BlockData data = getBlockData();
        if (data == null) {
            return Collections.emptyMap();
        }
        String asString = data.getAsString();
        int openBracket = asString.indexOf('[');
        int closeBracket = asString.indexOf(']');
        if (openBracket == -1 || closeBracket <= openBracket) {
            return Collections.emptyMap();
        }
        String propertiesString = asString.substring(openBracket + 1, closeBracket);
        if (propertiesString.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, String> properties = new LinkedHashMap<>();
        String[] pairs = propertiesString.split(",");
        for (String pair : pairs) {
            int eqIndex = pair.indexOf('=');
            if (eqIndex > 0) {
                String key = pair.substring(0, eqIndex).trim();
                String value = pair.substring(eqIndex + 1).trim();
                properties.put(key, value);
            }
        }
        return Collections.unmodifiableMap(properties);
    }

    public BlockHologramData setBlockStateProperty(String property, String value) {
        Map<String, String> currentProps = new LinkedHashMap<>(getBlockStateProperties());
        currentProps.put(property, value);
        String joined = currentProps.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(","));
        BlockData newBlockData = Bukkit.createBlockData(getBlock(), "[" + joined + "]");
        return setBlockData(newBlockData);
    }

    @Override
    @ApiStatus.Internal
    public boolean read(ConfigurationSection section, String name) {
        super.read(section, name);
        String blockString = section.getString("block");
        if (blockString == null) {
            blockString = section.getString("block_material", "GRASS_BLOCK");
        }
        try {
            blockData = Bukkit.createBlockData(blockString);
            block = blockData.getMaterial();
        } catch (Exception e) {
            Material mat = Material.getMaterial(blockString.toUpperCase());
            if (mat == null) {
                mat = DEFAULT_BLOCK;
            }
            setBlock(mat);
        }

        return true;
    }

    @Override
    @ApiStatus.Internal
    public boolean write(ConfigurationSection section, String name) {
        super.write(section, name);
        BlockData data = getBlockData();
        section.set("block", data != null ? data.getAsString() : block.name());

        return true;
    }

    @Override
    public BlockHologramData copy(String name) {
        BlockHologramData blockHologramData = new BlockHologramData(name, getLocation());
        BlockData data = this.getBlockData();
        if (data != null) {
            blockHologramData.setBlockData(data.clone());
        } else {
            blockHologramData.setBlock(this.getBlock());
        }
        blockHologramData
                .setScale(this.getScale())
                .setShadowRadius(this.getShadowRadius())
                .setShadowStrength(this.getShadowStrength())
                .setBillboard(this.getBillboard())
                .setTranslation(this.getTranslation())
                .setBrightness(this.getBrightness())
                .setVisibilityDistance(getVisibilityDistance())
                .setVisibility(this.getVisibility())
                .setPersistent(this.isPersistent())
                .setLinkedNpcName(getLinkedNpcName());

        return blockHologramData;
    }
}
