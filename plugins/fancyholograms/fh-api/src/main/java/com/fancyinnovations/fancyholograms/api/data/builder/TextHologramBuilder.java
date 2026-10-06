package com.fancyinnovations.fancyholograms.api.data.builder;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.TextDisplay;

import java.util.List;

public class TextHologramBuilder extends HologramBuilder {

    private TextHologramBuilder(String name, Location location) {
        super();
        this.data = new TextHologramData(name, location);
    }

    /**
     * Creates a new instance of TextHologramBuilder with the specified name and location.
     *
     * @param name     the name of the text hologram
     * @param location the location of the text hologram
     */
    public static TextHologramBuilder create(String name, Location location) {
        return new TextHologramBuilder(name, location);
    }

    /**
     * Sets the text lines for the text hologram.
     * Each string in the list represents a separate line of text.
     *
     * @param text the list of text lines
     */
    public TextHologramBuilder text(List<String> text) {
        ((TextHologramData) data).setText(text);
        return this;
    }

    /**
     * Sets the text lines for the text hologram using a single string.
     * The string will be treated as a single line of text.
     *
     * @param text the text line
     */
    public TextHologramBuilder text(String text) {
        return text(List.of(text));
    }

    /**
     * Sets the text lines for the text hologram using an array of strings.
     * Each string in the array represents a separate line of text.
     *
     * @param text the array of text lines
     */
    public TextHologramBuilder text(String... text) {
        return text(List.of(text));
    }

    /**
     * Sets the background color of the text hologram.
     *
     * @param background the background color
     */
    public TextHologramBuilder background(Color background) {
        ((TextHologramData) data).setBackground(background);
        return this;
    }

    /**
     * Sets the background color of the text hologram using a color code in ARGB format.
     *
     * @param background the ARGB color code as a string (#AARRGGBB)
     */
    public TextHologramBuilder background(String background) {
        int argb = Integer.parseInt(background.substring(1), 16);
        return background(Color.fromARGB(argb));
    }

    /**
     * Sets the text alignment of the text hologram.
     *
     * @param textAlignment the text alignment (e.g., LEFT, CENTER, RIGHT)
     */
    public TextHologramBuilder textAlignment(TextDisplay.TextAlignment textAlignment) {
        ((TextHologramData) data).setTextAlignment(textAlignment);
        return this;
    }

    /**
     * Sets whether the text hologram should have a shadow effect.
     * In newer versions, you can also use the <a href="https://docs.advntr.dev/minimessage/format.html#shadow-color">shadow by MiniMessage</a>, which also supports shadow colors.
     *
     * @param textShadow true to enable text shadow, false to disable
     */
    public TextHologramBuilder textShadow(boolean textShadow) {
        ((TextHologramData) data).setTextShadow(textShadow);
        return this;
    }

    /**
     * Sets whether the text hologram should be see-through.
     *
     * @param seeThrough true to enable see-through, false to disable
     */
    public TextHologramBuilder seeThrough(boolean seeThrough) {
        ((TextHologramData) data).setSeeThrough(seeThrough);
        return this;
    }

    /**
     * Sets the interval (in ticks) at which the text of the hologram should be updated.
     * This is useful for dynamic text / placeholders that changes over time.
     *
     * @param updateTextInterval the update interval in ticks
     */
    public TextHologramBuilder updateTextInterval(int updateTextInterval) {
        ((TextHologramData) data).setTextUpdateInterval(updateTextInterval);
        return this;
    }

    /**
     * Sets the opacity of the text hologram.
     *
     * @param textOpacity the text opacity byte (-1 or 255 for fully opaque, 0 for fully transparent)
     */
    public TextHologramBuilder textOpacity(byte textOpacity) {
        ((TextHologramData) data).setTextOpacity(textOpacity);
        return this;
    }

    /**
     * Sets the opacity of the text hologram.
     *
     * @param textOpacity the text opacity value (0 to 255)
     */
    public TextHologramBuilder textOpacity(int textOpacity) {
        return textOpacity((byte) textOpacity);
    }

}
