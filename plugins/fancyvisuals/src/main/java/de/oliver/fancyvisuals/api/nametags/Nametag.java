package de.oliver.fancyvisuals.api.nametags;

import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record Nametag(
        @SerializedName("text_lines")
        @NotNull List<String> textLines,

        @SerializedName("background_color")
        @NotNull String backgroundColor,

        @SerializedName("text_shadow")
        @NotNull Boolean textShadow,

        @SerializedName("text_alignment")
        @NotNull TextAlignment textAlignment
) {

    public enum TextAlignment {
        @SerializedName("left")
        LEFT,

        @SerializedName("right")
        RIGHT,

        @SerializedName("center")
        CENTER
    }

}
