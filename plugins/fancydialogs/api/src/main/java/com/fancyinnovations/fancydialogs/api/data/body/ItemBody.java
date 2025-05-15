package com.fancyinnovations.fancydialogs.api.data.body;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ItemBody(
        @NotNull ItemStack item,
        @Nullable TextBody description,
        boolean showDecorations,
        boolean showTooltip,
        int width,
        int height
) implements DialogBody {

}
