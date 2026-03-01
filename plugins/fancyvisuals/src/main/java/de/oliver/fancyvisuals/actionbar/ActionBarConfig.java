package de.oliver.fancyvisuals.actionbar;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ActionBarConfig(
        @SerializedName("messages")
        @NotNull List<String> messages,
        @SerializedName("interval_ms")
        int intervalMs
) {
}
