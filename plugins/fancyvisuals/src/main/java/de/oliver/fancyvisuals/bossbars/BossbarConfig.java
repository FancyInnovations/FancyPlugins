package de.oliver.fancyvisuals.bossbars;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record BossbarConfig(
        @SerializedName("id")
        @NotNull String id,
        @SerializedName("text")
        @NotNull String text,
        @SerializedName("progress")
        double progress,
        @SerializedName("color")
        @Nullable String color,
        @SerializedName("style")
        @Nullable String style,
        @SerializedName("flags")
        @NotNull List<String> flags,
        @SerializedName("visible")
        boolean visible
) {
}
