package de.oliver.fancyvisuals.chat;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public record ChatFormat(
        @SerializedName("format")
        @NotNull String format
) {
}
