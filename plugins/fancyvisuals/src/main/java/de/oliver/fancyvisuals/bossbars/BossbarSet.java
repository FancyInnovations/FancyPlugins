package de.oliver.fancyvisuals.bossbars;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record BossbarSet(
        @SerializedName("bars")
        @NotNull List<BossbarConfig> bars
) {
}
