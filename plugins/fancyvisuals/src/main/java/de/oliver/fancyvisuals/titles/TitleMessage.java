package de.oliver.fancyvisuals.titles;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public record TitleMessage(
        @SerializedName("title")
        @NotNull String title,
        @SerializedName("subtitle")
        @NotNull String subtitle,
        @SerializedName("fade_in_ticks")
        int fadeInTicks,
        @SerializedName("stay_ticks")
        int stayTicks,
        @SerializedName("fade_out_ticks")
        int fadeOutTicks
) {
}
