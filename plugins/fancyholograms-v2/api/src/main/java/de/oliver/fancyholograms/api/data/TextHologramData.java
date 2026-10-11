package de.oliver.fancyholograms.api.data;

import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.api.hologram.HologramType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.TextDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class TextHologramData extends DisplayHologramData {

    public static final TextDisplay.TextAlignment DEFAULT_TEXT_ALIGNMENT = TextDisplay.TextAlignment.CENTER;
    public static final boolean DEFAULT_TEXT_SHADOW_STATE = false;
    public static final boolean DEFAULT_SEE_THROUGH = false;
    public static final int DEFAULT_TEXT_UPDATE_INTERVAL = -1;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private List<String> text;
    private transient List<Component> textComponents = null;
    private Color background;
    private TextDisplay.TextAlignment textAlignment = DEFAULT_TEXT_ALIGNMENT;
    private boolean textShadow = DEFAULT_TEXT_SHADOW_STATE;
    private boolean seeThrough = DEFAULT_SEE_THROUGH;
    private int textUpdateInterval = DEFAULT_TEXT_UPDATE_INTERVAL;

    /**
     * @param name     Name of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public TextHologramData(String name, Location location) {
        super(name, HologramType.TEXT, location);
        text = new ArrayList<>(List.of("Edit this line with /hologram edit " + name));
    }

    public List<String> getText() {
        return text;
    }

    public TextHologramData setText(List<String> text) {
        if (!Objects.equals(this.text, text)) {
            this.text = text;
            this.textComponents = null;
            setHasChanges(true);
        }

        return this;
    }

    public @NotNull List<Component> getTextComponents() {
        if (textComponents != null) {
            return new ArrayList<>(textComponents);
        }

        return parseLines(text);
    }

    public boolean hasTextComponents() {
        return textComponents != null;
    }

    public TextHologramData setTextComponents(@NotNull List<? extends ComponentLike> textComponents) {
        List<Component> components = new ArrayList<>(textComponents.size());
        List<String> serialized = new ArrayList<>(textComponents.size());
        for (ComponentLike line : textComponents) {
            Component component = line.asComponent();
            components.add(component);
            serialized.add(MINI_MESSAGE.serialize(component));
        }

        this.text = serialized;
        this.textComponents = components;
        setHasChanges(true);

        return this;
    }

    public TextHologramData setTextComponents(@NotNull ComponentLike... textComponents) {
        return setTextComponents(Arrays.asList(textComponents));
    }

    public TextHologramData clearTextComponents() {
        if (this.textComponents != null) {
            this.textComponents = null;
            setHasChanges(true);
        }

        return this;
    }

    public void addLine(String line) {
        text.add(line);
        if (textComponents != null) {
            textComponents.add(MINI_MESSAGE.deserialize(line));
        }
        setHasChanges(true);
    }

    public void addLine(@NotNull ComponentLike line) {
        Component component = line.asComponent();
        ensureTextComponents();
        text.add(MINI_MESSAGE.serialize(component));
        textComponents.add(component);
        setHasChanges(true);
    }

    public void removeLine(int index) {
        text.remove(index);
        if (textComponents != null) {
            textComponents.remove(index);
        }
        setHasChanges(true);
    }

    public void setLine(int index, @NotNull ComponentLike line) {
        Component component = line.asComponent();
        ensureTextComponents();
        text.set(index, MINI_MESSAGE.serialize(component));
        textComponents.set(index, component);
        setHasChanges(true);
    }

    private void ensureTextComponents() {
        if (textComponents == null) {
            textComponents = parseLines(text);
        }
    }

    private static @NotNull List<Component> parseLines(@NotNull List<String> lines) {
        List<Component> parsed = new ArrayList<>(lines.size());
        for (String line : lines) {
            parsed.add(MINI_MESSAGE.deserialize(line));
        }
        return parsed;
    }

    public Color getBackground() {
        return background;
    }

    public TextHologramData setBackground(Color background) {
        if (!Objects.equals(this.background, background)) {
            this.background = background;
            setHasChanges(true);
        }

        return this;
    }

    public TextDisplay.TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public TextHologramData setTextAlignment(TextDisplay.TextAlignment textAlignment) {
        if (!Objects.equals(this.textAlignment, textAlignment)) {
            this.textAlignment = textAlignment;
            setHasChanges(true);
        }

        return this;
    }

    public boolean hasTextShadow() {
        return textShadow;
    }

    public TextHologramData setTextShadow(boolean textShadow) {
        if (this.textShadow != textShadow) {
            this.textShadow = textShadow;
            setHasChanges(true);
        }

        return this;
    }

    public boolean isSeeThrough() {
        return seeThrough;
    }

    public TextHologramData setSeeThrough(boolean seeThrough) {
        if (this.seeThrough != seeThrough) {
            this.seeThrough = seeThrough;
            setHasChanges(true);
        }

        return this;
    }

    public int getTextUpdateInterval() {
        return textUpdateInterval;
    }

    public TextHologramData setTextUpdateInterval(int textUpdateInterval) {
        if (this.textUpdateInterval != textUpdateInterval) {
            this.textUpdateInterval = textUpdateInterval;
            setHasChanges(true);
        }

        return this;
    }

    @Override
    public boolean read(ConfigurationSection section, String name) {
        super.read(section, name);
        text = section.getStringList("text");
        textComponents = null;
        if (text.isEmpty()) {
            text = List.of("Could not load hologram text");
            //TODO: maybe return false here?
        }

        textShadow = section.getBoolean("text_shadow", DEFAULT_TEXT_SHADOW_STATE);
        seeThrough = section.getBoolean("see_through", DEFAULT_SEE_THROUGH);
        textUpdateInterval = section.getInt("update_text_interval", DEFAULT_TEXT_UPDATE_INTERVAL);

        String textAlignmentStr = section.getString("text_alignment", DEFAULT_TEXT_ALIGNMENT.name().toLowerCase());
        textAlignment = switch (textAlignmentStr.toLowerCase(Locale.ROOT)) {
            case "right" -> TextDisplay.TextAlignment.RIGHT;
            case "left" -> TextDisplay.TextAlignment.LEFT;
            default -> TextDisplay.TextAlignment.CENTER;
        };

        background = null;
        String backgroundStr = section.getString("background", null);
        if (backgroundStr != null) {
            if (backgroundStr.equalsIgnoreCase("transparent")) {
                background = Hologram.TRANSPARENT;
            } else if (backgroundStr.startsWith("#")) {
                background = Color.fromARGB((int) Long.parseLong(backgroundStr.substring(1), 16));
                //backwards compatibility, make rgb hex colors solid color -their alpha is 0 by default-
                if (backgroundStr.length() == 7) background = background.setAlpha(255);
            } else {
                background = Color.fromARGB(NamedTextColor.NAMES.value(backgroundStr.toLowerCase(Locale.ROOT).trim().replace(' ', '_')).value() | 0xC8000000);
            }
        }

        return true;
    }

    @Override
    public boolean write(ConfigurationSection section, String name) {
        super.write(section, name);
        section.set("text", text);
        section.set("text_shadow", textShadow);
        section.set("see_through", seeThrough);
        section.set("text_alignment", textAlignment.name().toLowerCase(Locale.ROOT));
        section.set("update_text_interval", textUpdateInterval);

        final String color;
        if (background == null) {
            color = null;
        } else if (background == Hologram.TRANSPARENT) {
            color = "transparent";
        } else {
            NamedTextColor named = background.getAlpha() == 255 ? NamedTextColor.namedColor(background.asRGB()) : null;
            color = named != null ? named.toString() : '#' + Integer.toHexString(background.asARGB());
        }

        section.set("background", color);

        return true;
    }

    @Override
    public TextHologramData copy(String name) {
        TextHologramData textHologramData = new TextHologramData(name, getLocation());
        if (this.textComponents != null) {
            textHologramData.setTextComponents(new ArrayList<>(this.textComponents));
        } else {
            textHologramData.setText(new ArrayList<>(this.getText()));
        }
        textHologramData
                .setBackground(this.getBackground())
                .setTextAlignment(this.getTextAlignment())
                .setTextShadow(this.hasTextShadow())
                .setSeeThrough(this.isSeeThrough())
                .setTextUpdateInterval(this.getTextUpdateInterval())
                .setScale(this.getScale())
                .setShadowRadius(this.getShadowRadius())
                .setShadowStrength(this.getShadowStrength())
                .setBillboard(this.getBillboard())
                .setTranslation(this.getTranslation())
                .setBrightness(this.getBrightness())
                .setVisibilityDistance(this.getVisibilityDistance())
                .setVisibility(this.getVisibility())
                .setPersistent(this.isPersistent())
                .setLinkedNpcName(this.getLinkedNpcName());

        return textHologramData;
    }
}
