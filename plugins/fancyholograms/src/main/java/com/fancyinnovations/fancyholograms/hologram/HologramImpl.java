package com.fancyinnovations.fancyholograms.hologram;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramDespawnEvent;
import com.fancyinnovations.fancyholograms.api.events.HologramSpawnEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.util.PluginUtils;
import com.viaversion.viaversion.api.Via;
import de.oliver.fancysitula.api.entities.*;
import de.oliver.fancysitula.factories.FancySitula;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public final class HologramImpl extends Hologram {

    private FS_Display fsDisplay;
    private long lastSyncTime = 0L;
    private final long minSyncIntervalMs = FancyHologramsPlugin.get().getFHConfiguration().getHologramUpdateInterval();

    public HologramImpl(@NotNull final HologramData data) {
        super(data);

        final var location = data.getLocation();
        if (!location.isWorldLoaded()) {
            return;
        }

        switch (data.getType()) {
            case TEXT -> this.fsDisplay = new FS_TextDisplay();
            case ITEM -> this.fsDisplay = new FS_ItemDisplay();
            case BLOCK -> this.fsDisplay = new FS_BlockDisplay();
        }
    }


    @Override
    public void spawnTo(@NotNull final Player player) {
        if (!new HologramSpawnEvent(this, player).callEvent()) {
            return;
        }

        if (fsDisplay == null) {
            return; // could not be created, nothing to show
        }

        // this implies that the world is loaded
        if (!data.getWorldName().equals(player.getLocation().getWorld().getName())) {
            return;
        }

        if (FancyHologramsPlugin.get().getFHConfiguration().isHologramsForOldClientsEnabled()) {
            final var protocolVersion = PluginUtils.isViaVersionEnabled() ? Via.getAPI().getPlayerVersion(player.getUniqueId()) : MINIMUM_PROTOCOL_VERSION;
            if (protocolVersion < MINIMUM_PROTOCOL_VERSION) {
                FancyHolograms.get().getFancyLogger().debug("Player " + player.getName() + " is using an outdated protocol version (" + protocolVersion + "). Hologram will not be shown.");
                return;
            }
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, fsDisplay);

        this.viewers.add(player.getUniqueId());
        updateFor(player);
    }

    @Override
    public void despawnFrom(@NotNull final Player player) {
        if (fsDisplay == null) {
            return; // doesn't exist, nothing to hide
        }

        if (!data.getWorldName().equals(player.getLocation().getWorld().getName())) {
            return;
        }

        if (!new HologramDespawnEvent(this, player).callEvent()) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, fsDisplay);

        this.viewers.remove(player.getUniqueId());
    }


    @Override
    public void updateFor(@NotNull final Player player) {
        if (fsDisplay == null) {
            return; // doesn't exist, nothing to refresh
        }

        if (!data.getWorldName().equals(player.getLocation().getWorld().getName())) {
            return;
        }

        if (!isViewer(player)) {
            return;
        }

        syncWithData();

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

    private void syncWithData() {
        if (fsDisplay == null) {
            return;
        }

        final long now = System.currentTimeMillis();
        if (now - lastSyncTime < minSyncIntervalMs && !data.hasChanges()) {
            return;
        }

        lastSyncTime = now;

        // location data
        final var location = data.getLocation();
        fsDisplay.setLocation(location);

        if (fsDisplay instanceof FS_TextDisplay textDisplay && data instanceof com.fancyinnovations.fancyholograms.api.data.TextHologramData textData) {
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
        } else if (fsDisplay instanceof FS_ItemDisplay itemDisplay && data instanceof com.fancyinnovations.fancyholograms.api.data.ItemHologramData itemData) {
            // item
            itemDisplay.setItem(itemData.getItemStack());
        } else if (fsDisplay instanceof FS_BlockDisplay blockDisplay && data instanceof com.fancyinnovations.fancyholograms.api.data.BlockHologramData blockData) {
            // block

//            BlockType blockType = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK).get(blockData.getBlock().getKey());
            blockDisplay.setBlock(blockData.getBlock().createBlockData().createBlockState());
        }

        if (data instanceof com.fancyinnovations.fancyholograms.api.data.DisplayHologramData displayData) {
            // interpolation
            fsDisplay.setTransformationInterpolationDuration(displayData.getInterpolationDuration());
            fsDisplay.setTransformationInterpolationStartDeltaTicks(0);

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

            // glowing
            if (displayData.getGlowingColor() != null && displayData.getGlowingColor() != de.oliver.fancylib.colors.GlowingColor.DISABLED && displayData.getGlowingColor().getColor() != null) {
                byte currentFlags = 0;
                try {
                    currentFlags = fsDisplay.getSharedFlags();
                } catch (NullPointerException e) {

                }
                fsDisplay.setSharedFlags((byte) (currentFlags | 0x40));

                fsDisplay.setGlowColorOverride(displayData.getGlowingColor().getColor().value());
            } else {
                try {
                    byte currentFlags = fsDisplay.getSharedFlags();
                    fsDisplay.setSharedFlags((byte) (currentFlags & ~0x40));
                } catch (NullPointerException e) {

                }

                fsDisplay.setGlowColorOverride(-1);
            }

            fsDisplay.setViewRange(displayData.getVisibilityDistance());
        }
    }

}
