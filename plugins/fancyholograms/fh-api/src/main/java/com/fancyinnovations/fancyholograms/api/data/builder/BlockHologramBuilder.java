package com.fancyinnovations.fancyholograms.api.data.builder;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class BlockHologramBuilder extends HologramBuilder {

    private BlockHologramBuilder(String name, Location location) {
        super();
        this.data = new BlockHologramData(name, location);
    }

    /**
     * Creates a new instance of BlockHologramBuilder with the specified name and location.
     *
     * @param name     the name of the block hologram
     * @param location the location of the block hologram
     * @return a new instance of BlockHologramBuilder
     */
    public static BlockHologramBuilder create(String name, Location location) {
        return new BlockHologramBuilder(name, location);
    }

    /**
     * Sets the block material for the block hologram.
     *
     * @param block the material of the block
     * @return the builder instance
     */
    public BlockHologramBuilder block(Material block) {
        ((BlockHologramData) data).setBlock(block);
        return this;
    }

    /**
     * Sets the block material and a block state property for the block hologram.
     *
     * @param block    the material of the block
     * @param property the property name
     * @param value    the property value
     * @return the builder instance
     */
    public BlockHologramBuilder block(Material block, String property, String value) {
        ((BlockHologramData) data).setBlock(block);
        ((BlockHologramData) data).setBlockStateProperty(property, value);
        return this;
    }

    /**
     * Sets the block data for the block hologram.
     *
     * @param blockData the block data
     * @return the builder instance
     */
    public BlockHologramBuilder blockData(BlockData blockData) {
        ((BlockHologramData) data).setBlockData(blockData);
        return this;
    }

    /**
     * Sets a block state property for the block hologram.
     *
     * @param property the property name
     * @param value    the property value
     * @return the builder instance
     */
    public BlockHologramBuilder blockState(String property, String value) {
        ((BlockHologramData) data).setBlockStateProperty(property, value);
        return this;
    }

}
