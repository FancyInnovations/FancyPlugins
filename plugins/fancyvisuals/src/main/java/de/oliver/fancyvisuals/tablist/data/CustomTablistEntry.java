package de.oliver.fancyvisuals.tablist.data;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CustomTablistEntry(
        @SerializedName("id")
        @NotNull String id,
        @SerializedName("text")
        @NotNull String text,
        @SerializedName("sort_priority")
        int sortPriority,
        @SerializedName("ping")
        int ping,
        @SerializedName("game_mode")
        @Nullable String gameMode,
        @SerializedName("profile_name")
        @Nullable String profileName,
        @SerializedName("skin")
        @Nullable TablistSkin skin
) {
}
