package de.oliver.fancyvisuals.tablist.data;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

public record TablistPlayerFormat(
        @SerializedName("prefix")
        @NotNull String prefix,
        @SerializedName("name")
        @NotNull String name,
        @SerializedName("suffix")
        @NotNull String suffix,
        @SerializedName("sort_priority")
        int sortPriority
) {
}
