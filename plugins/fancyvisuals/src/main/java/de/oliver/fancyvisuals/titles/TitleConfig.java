package de.oliver.fancyvisuals.titles;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record TitleConfig(
        @SerializedName("interval_ms")
        int intervalMs,
        @SerializedName("messages")
        @NotNull List<TitleMessage> messages
) {
}
