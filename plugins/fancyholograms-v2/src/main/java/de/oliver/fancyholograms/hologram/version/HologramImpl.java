package de.oliver.fancyholograms.hologram.version;

import de.oliver.fancyholograms.api.data.*;
import de.oliver.fancyholograms.api.events.HologramHideEvent;
import de.oliver.fancyholograms.api.events.HologramShowEvent;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.util.PluginUtils;
import de.oliver.fancysitula.api.entities.*;
import de.oliver.fancysitula.factories.FancySitula;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HologramImpl extends Hologram {

    /**
     * Vertical spacing between lines for legacy clients (in blocks).
     */
    private static final float LINE_SPACING = 0.3f;

    private FS_Display fsDisplay;

    /**
     * Per-player legacy line displays for pre-1.19.4 clients.
     * Maps player UUID to a list of single-line text displays.
     */
    private final Map<UUID, List<FS_TextDisplay>> legacyLineDisplays = new ConcurrentHashMap<>();

    public HologramImpl(@NotNull final HologramData data) {
        super(data);
    }

    @Override
    public int getEntityId() {
        if (fsDisplay == null) {
            return -1;
        }
        return fsDisplay.getId();
    }

    @Override
    public @Nullable org.bukkit.entity.Display getDisplayEntity() {
        return null;
    }

    @Override
    public void create() {
        final var location = data.getLocation();
        if (!location.isWorldLoaded()) {
            return;
        }

        switch (data.getType()) {
            case TEXT -> this.fsDisplay = new FS_TextDisplay();
            case ITEM -> this.fsDisplay = new FS_ItemDisplay();
            case BLOCK -> this.fsDisplay = new FS_BlockDisplay();
        }

        if (data instanceof DisplayHologramData dd) {
            fsDisplay.setTransformationInterpolationDuration(dd.getInterpolationDuration());
            fsDisplay.setTransformationInterpolationStartDeltaTicks(0);
        }

        update();
    }

    @Override
    public void delete() {
        this.fsDisplay = null;
        legacyLineDisplays.clear();
    }

    @Override
    public void update() {
        if (fsDisplay == null) {
            return;
        }

        // location data
        final var location = data.getLocation();
        if (location.getWorld() == null || !location.isWorldLoaded()) {
            return;
        }
        fsDisplay.setLocation(location);

        if (fsDisplay instanceof FS_TextDisplay textDisplay && data instanceof TextHologramData textData) {
            // line width
            textDisplay.setLineWidth(Hologram.LINE_WIDTH);

            // background
            final var background = textData.getBackground();
            if (background == null) {
                textDisplay.setBackground(1073741824); // default background
            } else if (background == Hologram.TRANSPARENT) {
                textDisplay.setBackground(0);
            } else {
                textDisplay.setBackground(background.asARGB());
            }

            textDisplay.setStyleFlags((byte) 0);
            textDisplay.setShadow(textData.hasTextShadow());
            textDisplay.setSeeThrough(textData.isSeeThrough());

            switch (textData.getTextAlignment()) {
                case LEFT -> textDisplay.setAlignLeft(true);
                case RIGHT -> textDisplay.setAlignRight(true);
                case CENTER -> {
                    textDisplay.setAlignLeft(false);
                    textDisplay.setAlignRight(false);
                }
            }
        } else if (fsDisplay instanceof FS_ItemDisplay itemDisplay && data instanceof ItemHologramData itemData) {
            // item
            itemDisplay.setItem(itemData.getItemStack());
        } else if (fsDisplay instanceof FS_BlockDisplay blockDisplay && data instanceof BlockHologramData blockData) {
            // block
            blockDisplay.setBlock(blockData.getBlock().createBlockData().createBlockState());
        }

        if (data instanceof DisplayHologramData displayData) {
            // billboard data
            fsDisplay.setBillboard(FS_Display.Billboard.valueOf(displayData.getBillboard().name()));

            // brightness
            if (displayData.getBrightness() != null) {
                fsDisplay.setBrightnessOverride(displayData.getBrightness().getBlockLight() << 4 | displayData.getBrightness().getSkyLight() << 20);
            }

            // entity transformation
            fsDisplay.setTranslation(displayData.getTranslation());
            fsDisplay.setScale(displayData.getScale());
            fsDisplay.setLeftRotation(new Quaternionf());
            fsDisplay.setRightRotation(new Quaternionf());

            // entity shadow
            fsDisplay.setShadowRadius(displayData.getShadowRadius());
            fsDisplay.setShadowStrength(displayData.getShadowStrength());

            fsDisplay.setViewRange(displayData.getVisibilityDistance());
        }
    }


    @Override
    public boolean show(@NotNull final Player player) {
        if (!new HologramShowEvent(this, player).callEvent()) {
            return false;
        }

        if (this.fsDisplay == null) {
            create(); // try to create it if it doesn't exist every time
        }

        if (fsDisplay == null) {
            return false; // could not be created, nothing to show
        }

        if (!data.getLocation().getWorld().getName().equals(player.getLocation().getWorld().getName())) {
            return false;
        }

        // Handle legacy clients (pre-1.19.4) that need single-line text displays
        if (PluginUtils.isViaVersionEnabled() && PluginUtils.isLegacyClient(player)) {
            if (data instanceof TextHologramData) {
                return showLegacyLines(player);
            }
        }

        // Normal flow for modern clients
        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, fsDisplay);

        this.viewers.add(player.getUniqueId());
        refreshHologram(player);

        return true;
    }

    @Override
    public boolean hide(@NotNull final Player player) {
        if (!new HologramHideEvent(this, player).callEvent()) {
            return false;
        }

        // Check for legacy displays first
        if (legacyLineDisplays.containsKey(player.getUniqueId())) {
            return hideLegacyLines(player);
        }

        if (fsDisplay == null) {
            return false; // doesn't exist, nothing to hide
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, fsDisplay);

        this.viewers.remove(player.getUniqueId());
        return true;
    }


    @Override
    public void refresh(@NotNull final Player player) {
        if (fsDisplay == null) {
            return; // doesn't exist, nothing to refresh
        }

        if (!isViewer(player)) {
            return;
        }

        // Check for legacy displays
        if (legacyLineDisplays.containsKey(player.getUniqueId())) {
            refreshLegacyLines(player);
            return;
        }

        // Normal flow for modern clients
        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                        fsDisplay.getId(),
                        data.getLocation().x(),
                        data.getLocation().y(),
                        data.getLocation().z(),
                        data.getLocation().getYaw(),
                        data.getLocation().getPitch(),
                        true)
                .send(fsPlayer);


        if (fsDisplay instanceof FS_TextDisplay textDisplay) {
            textDisplay.setText(getShownText(player));
        }

        FancySitula.ENTITY_FACTORY.setEntityDataFor(fsPlayer, fsDisplay);
    }

    // ==================== Legacy Client Support Methods ====================

    /**
     * Shows the hologram to a legacy client by spawning multiple single-line text displays.
     *
     * @param player The player to show the hologram to
     * @return true if the hologram was shown successfully
     */
    private boolean showLegacyLines(@NotNull Player player) {
        if (!(data instanceof TextHologramData textData)) {
            return false;
        }

        List<String> lines = textData.getText();
        List<FS_TextDisplay> lineDisplays = new ArrayList<>(lines.size());
        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        for (int i = 0; i < lines.size(); i++) {
            FS_TextDisplay lineDisplay = createLineDisplay();
            lineDisplays.add(lineDisplay);
            FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, lineDisplay);
        }

        legacyLineDisplays.put(player.getUniqueId(), lineDisplays);
        this.viewers.add(player.getUniqueId());
        refreshLegacyLines(player);

        return true;
    }

    /**
     * Hides the hologram from a legacy client by despawning all single-line text displays.
     *
     * @param player The player to hide the hologram from
     * @return true if the hologram was hidden successfully
     */
    private boolean hideLegacyLines(@NotNull Player player) {
        List<FS_TextDisplay> lineDisplays = legacyLineDisplays.remove(player.getUniqueId());
        if (lineDisplays == null) {
            return false;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        for (FS_TextDisplay lineDisplay : lineDisplays) {
            FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, lineDisplay);
        }

        this.viewers.remove(player.getUniqueId());
        return true;
    }

    /**
     * Refreshes the hologram for a legacy client by updating all single-line text displays.
     *
     * @param player The player to refresh the hologram for
     */
    private void refreshLegacyLines(@NotNull Player player) {
        if (!(data instanceof TextHologramData textData)) {
            return;
        }

        List<FS_TextDisplay> lineDisplays = legacyLineDisplays.get(player.getUniqueId());
        if (lineDisplays == null) {
            return;
        }

        List<String> lines = textData.getText();
        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        Location baseLoc = data.getLocation();

        handleLineCountChange(player, lines.size());
        lineDisplays = legacyLineDisplays.get(player.getUniqueId()); // Refresh reference after potential changes

        if (lineDisplays == null || lineDisplays.isEmpty()) {
            return;
        }

        double offsetX = 0, offsetY = 0, offsetZ = 0;
        if (data instanceof DisplayHologramData displayData && displayData.getTranslation() != null) {
            offsetX = displayData.getTranslation().x();
            offsetY = displayData.getTranslation().y();
            offsetZ = displayData.getTranslation().z();
        }

        float totalHeight = (lines.size() - 1) * LINE_SPACING;
        double startY = baseLoc.getY() + offsetY + (totalHeight / 2);

        for (int i = 0; i < Math.min(lines.size(), lineDisplays.size()); i++) {
            FS_TextDisplay lineDisplay = lineDisplays.get(i);
            double lineX = baseLoc.getX() + offsetX;
            double lineY = startY - (i * LINE_SPACING);
            double lineZ = baseLoc.getZ() + offsetZ;

            lineDisplay.setLocation(lineX, lineY, lineZ);
            lineDisplay.setRotation(baseLoc.getYaw(), baseLoc.getPitch());

            Component lineText = ModernChatColorHandler.translate(lines.get(i), player);
            lineDisplay.setText(lineText);

            applyTextDisplayProperties(lineDisplay, textData);

            FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                            lineDisplay.getId(),
                            lineX,
                            lineY,
                            lineZ,
                            baseLoc.getYaw(),
                            baseLoc.getPitch(),
                            true)
                    .send(fsPlayer);

            FancySitula.ENTITY_FACTORY.setEntityDataFor(fsPlayer, lineDisplay);
        }
    }

    /**
     * Creates a new single-line text display entity for legacy clients.
     *
     * @return A new FS_TextDisplay
     */
    private FS_TextDisplay createLineDisplay() {
        return new FS_TextDisplay();
    }

    /**
     * Applies basic text display properties from the hologram data to a line display.
     * Only applies essential properties - complex transformations (scale, translation, etc.)
     * are intentionally skipped for legacy clients.
     *
     * @param lineDisplay The line display to apply properties to
     * @param textData    The text hologram data to read properties from
     */
    private void applyTextDisplayProperties(FS_TextDisplay lineDisplay, TextHologramData textData) {
        lineDisplay.setLineWidth(Hologram.LINE_WIDTH);

        Color background = textData.getBackground();
        if (background == null) {
            lineDisplay.setBackground(1073741824); // default background
        } else if (background == Hologram.TRANSPARENT) {
            lineDisplay.setBackground(0);
        } else {
            lineDisplay.setBackground(background.asARGB());
        }

        lineDisplay.setStyleFlags((byte) 0);
        lineDisplay.setShadow(textData.hasTextShadow());
        lineDisplay.setSeeThrough(textData.isSeeThrough());

        switch (textData.getTextAlignment()) {
            case LEFT -> lineDisplay.setAlignLeft(true);
            case RIGHT -> lineDisplay.setAlignRight(true);
            case CENTER -> {
                lineDisplay.setAlignLeft(false);
                lineDisplay.setAlignRight(false);
            }
        }

        if (data instanceof DisplayHologramData displayData) {
            lineDisplay.setBillboard(FS_Display.Billboard.valueOf(displayData.getBillboard().name()));

            if (displayData.getBrightness() != null) {
                lineDisplay.setBrightnessOverride(
                        displayData.getBrightness().getBlockLight() << 4 |
                                displayData.getBrightness().getSkyLight() << 20
                );
            }

            lineDisplay.setViewRange(displayData.getVisibilityDistance());
        }
    }

    /**
     * Handles changes in the number of lines by adding or removing line displays.
     *
     * @param player       The player to update displays for
     * @param newLineCount The new number of lines in the hologram
     */
    private void handleLineCountChange(Player player, int newLineCount) {
        List<FS_TextDisplay> lineDisplays = legacyLineDisplays.get(player.getUniqueId());
        if (lineDisplays == null) {
            return;
        }

        int currentCount = lineDisplays.size();
        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        if (newLineCount > currentCount) {
            // Add new line displays
            for (int i = currentCount; i < newLineCount; i++) {
                FS_TextDisplay newLine = createLineDisplay();
                lineDisplays.add(newLine);
                FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, newLine);
            }
        } else if (newLineCount < currentCount) {
            // Remove excess line displays
            for (int i = currentCount - 1; i >= newLineCount; i--) {
                FS_TextDisplay removedLine = lineDisplays.remove(i);
                FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, removedLine);
            }
        }
    }
}
