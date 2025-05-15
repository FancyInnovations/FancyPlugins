package com.fancyinnovations.fancydialogs.api.body;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBody extends DialogBody {

    private @NotNull ItemStack item;
    private @Nullable TextBody description;
    boolean showDecorations;
    boolean showTooltip;
    int width;
    int height;

    public ItemBody(@NotNull ItemStack item, @Nullable TextBody description, boolean showDecorations, boolean showTooltip, int width, int height) {
        this.item = item;
        this.description = description;
        this.showDecorations = showDecorations;
        this.showTooltip = showTooltip;
        this.width = width;
        this.height = height;
    }

    public @NotNull ItemStack getItem() {
        return item;
    }

    public void setItem(@NotNull ItemStack item) {
        this.item = item;
    }

    public @Nullable TextBody getDescription() {
        return description;
    }

    public void setDescription(@Nullable TextBody description) {
        this.description = description;
    }

    public boolean isShowDecorations() {
        return showDecorations;
    }

    public void setShowDecorations(boolean showDecorations) {
        this.showDecorations = showDecorations;
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public void setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
