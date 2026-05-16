package de.oliver.fancyvisuals.tablist.data;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CustomTablistEntries(
        @SerializedName("entries")
        @NotNull List<CustomTablistEntry> entries
) {
}
