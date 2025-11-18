package com.fancyinnovations.fancyholograms.api.data;

import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public class ItemHologramData extends DisplayHologramData {

    public static final ItemStack DEFAULT_ITEM = new ItemStack(Material.APPLE);

    private ItemStack item = DEFAULT_ITEM;
    private boolean glowing = false;
    private NamedTextColor glowingColor = NamedTextColor.WHITE;

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public ItemHologramData(String name, Location location) {
        super(name, HologramType.ITEM, location);
    }

    public ItemStack getItemStack() {
        return item;
    }

    public ItemHologramData setItemStack(ItemStack item) {
        if (!Objects.equals(this.item, item)) {
            this.item = item;
            setHasChanges(true);
        }

        return this;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public ItemHologramData setGlowing(boolean glowing) {
        if (this.glowing != glowing) {
            this.glowing = glowing;
            setHasChanges(true);
        }

        return this;
    }

    public NamedTextColor getGlowingColor() {
        return glowingColor;
    }

    public ItemHologramData setGlowingColor(NamedTextColor glowingColor) {
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
        item = section.getItemStack("item", DEFAULT_ITEM);
        glowing = section.getBoolean("glowing", false);
        glowingColor = NamedTextColor.NAMES.value(section.getString("glowing_color", "white"));

        return true;
    }

    @Override
    @ApiStatus.Internal
    public boolean write(ConfigurationSection section, String name) {
        super.write(section, name);
        section.set("item", item);
        section.set("glowing", glowing);
        section.set("glowing_color", glowingColor.toString());

        return true;
    }

    @Override
    public ItemHologramData copy(String name) {
        ItemHologramData itemHologramData = new ItemHologramData(name, getLocation());
        itemHologramData
                .setItemStack(this.getItemStack())
                .setScale(this.getScale())
                .setShadowRadius(this.getShadowRadius())
                .setShadowStrength(this.getShadowStrength())
                .setBillboard(this.getBillboard())
                .setTranslation(this.getTranslation())
                .setBrightness(this.getBrightness())
                .setGlowing(this.isGlowing())
                .setGlowingColor(this.getGlowingColor())
                .setVisibilityDistance(this.getVisibilityDistance())
                .setVisibility(this.getVisibility())
                .setPersistent(this.isPersistent())
                .setLinkedNpcName(this.getLinkedNpcName());

        return itemHologramData;
    }
}
