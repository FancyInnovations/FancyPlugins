package de.oliver.fancyvisuals.tablist.data;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public record TablistHeaderFooter(
        @SerializedName("header")
        @NotNull String header,
        @SerializedName("footer")
        @NotNull String footer
) {
}
