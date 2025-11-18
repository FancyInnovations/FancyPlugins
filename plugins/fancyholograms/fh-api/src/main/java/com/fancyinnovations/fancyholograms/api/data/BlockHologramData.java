package com.fancyinnovations.fancyholograms.api.data;

import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public class BlockHologramData extends DisplayHologramData {

    public static Material DEFAULT_BLOCK = Material.GRASS_BLOCK;

    private Material block = DEFAULT_BLOCK;
    private boolean glowing = false;
    private NamedTextColor glowingColor = NamedTextColor.WHITE;

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public BlockHologramData(String name, Location location) {
        super(name, HologramType.BLOCK, location);
    }

    public Material getBlock() {
        return block;
    }

    public BlockHologramData setBlock(Material block) {
        if (!Objects.equals(this.block, block)) {
            this.block = block;
            setHasChanges(true);
        }

        return this;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public BlockHologramData setGlowing(boolean glowing) {
        if (this.glowing != glowing) {
            this.glowing = glowing;
            setHasChanges(true);
        }

        return this;
    }

    public NamedTextColor getGlowingColor() {
        return glowingColor;
    }

    public BlockHologramData setGlowingColor(NamedTextColor glowingColor) {
        if (!Objects.equals(this.glowingColor, glowingColor)) {
            this.glowingColor = glowingColor;
            setHasChanges(true);
        }

        return this;
    }

    @Override
    @ApiStatus.Internal
    public boolean read(ConfigurationSection section, String name) {
        super.read(section, name);
        block = Material.getMaterial(section.getString("block", "GRASS_BLOCK").toUpperCase());
        glowing = section.getBoolean("glowing", false);
        glowingColor = NamedTextColor.NAMES.value(section.getString("glowing_color", "white"));

        return true;
    }

    @Override
    @ApiStatus.Internal
    public boolean write(ConfigurationSection section, String name) {
        super.write(section, name);
        section.set("block", block.name());
        section.set("glowing", glowing);
        section.set("glowing_color", glowingColor.toString());

        return true;
    }

    @Override
    public BlockHologramData copy(String name) {
        BlockHologramData blockHologramData = new BlockHologramData(name, getLocation());
        blockHologramData
                .setBlock(this.getBlock())
                .setScale(this.getScale())
                .setShadowRadius(this.getShadowRadius())
                .setShadowStrength(this.getShadowStrength())
                .setBillboard(this.getBillboard())
                .setTranslation(this.getTranslation())
                .setBrightness(this.getBrightness())
                .setGlowing(this.isGlowing())
                .setGlowingColor(this.getGlowingColor())
                .setVisibilityDistance(getVisibilityDistance())
                .setVisibility(this.getVisibility())
                .setPersistent(this.isPersistent())
                .setLinkedNpcName(getLinkedNpcName());

        return blockHologramData;
    }
}
