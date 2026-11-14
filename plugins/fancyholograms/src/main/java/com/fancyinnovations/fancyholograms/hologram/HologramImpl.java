package com.fancyinnovations.fancyholograms.hologram;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.data.*;
import com.fancyinnovations.fancyholograms.api.data.property.CustomComponentProviderTrait;
import com.fancyinnovations.fancyholograms.api.data.property.HologramRotation;
import com.fancyinnovations.fancyholograms.api.events.HologramDespawnEvent;
import com.fancyinnovations.fancyholograms.api.events.HologramSpawnEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.storage.json.JsonAdapter;
import com.fancyinnovations.fancyholograms.util.PluginUtils;
import com.viaversion.viaversion.api.Via;
import de.oliver.fancylib.colors.GlowingColor;
import de.oliver.fancysitula.api.entities.*;
import de.oliver.fancysitula.factories.FancySitula;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public final class HologramImpl extends Hologram {

    private final Object displayLock = new Object();
    private volatile FS_Display fsDisplay;

    public HologramImpl(@NotNull final HologramData data) {
        super(data);

        createFsDisplay();
    }

    private void createFsDisplay() {
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

        if (!data.getLocation().isWorldLoaded()) {
            return;
        }

        if (!data.getWorldName().equals(player.getLocation().getWorld().getName())) {
            this.viewers.remove(player.getUniqueId());
            return;
        }

        if (FancyHologramsPlugin.get().getFHConfiguration().isHologramsForOldClientsEnabled()) {
            final var protocolVersion = PluginUtils.isViaVersionEnabled() ? Via.getAPI().getPlayerVersion(player.getUniqueId()) : MINIMUM_PROTOCOL_VERSION;
            if (protocolVersion < MINIMUM_PROTOCOL_VERSION) {
                FancyHolograms.get().getFancyLogger().debug("Player " + player.getName() + " is using an outdated protocol version (" + protocolVersion + "). Hologram will not be shown.");
                return;
            }
        }

        final Component text = getTextFor(player);

        synchronized (displayLock) {
            if (fsDisplay == null) {
                createFsDisplay();
                if (fsDisplay == null) {
                    return; // could not be created, nothing to show
                }
            }

            syncWithData(text);

            FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
            FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, fsDisplay);

            this.viewers.add(player.getUniqueId());
            sendUpdate(fsPlayer);
        }
    }

    @Override
    public void despawnFrom(@NotNull final Player player) {
        if (fsDisplay == null) {
            return; // doesn't exist, nothing to hide
        }

        if (!new HologramDespawnEvent(this, player).callEvent()) {
            return;
        }

        synchronized (displayLock) {
            FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
            FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, fsDisplay);

            this.viewers.remove(player.getUniqueId());
        }
    }

    @Override
    public void updateFor(@NotNull final Player player) {
        if (fsDisplay == null) {
            return; // doesn't exist, nothing to refresh
        }

        if (!data.getWorldName().equals(player.getLocation().getWorld().getName())) {
            this.viewers.remove(player.getUniqueId());
            return;
        }

        if (!isViewer(player)) {
            return;
        }

        final Component text = getTextFor(player);

        synchronized (displayLock) {
            if (fsDisplay == null || !isViewer(player)) {
                return; // doesn't exist or is no longer visible, nothing to refresh
            }

            syncWithData(text);
            sendUpdate(new FS_RealPlayer(player));
        }
    }

    private Component getTextFor(@NotNull final Player player) {
        if (!(data instanceof TextHologramData)) {
            return null;
        }

        CustomComponentProviderTrait customComponentProvider = data.getTraitTrait().getTrait(CustomComponentProviderTrait.class);
        if (customComponentProvider != null) {
            return customComponentProvider.getComponentForPlayer(player);
        }

        return getShownText(player);
    }

    private void sendUpdate(FS_RealPlayer fsPlayer) {
        FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                        fsDisplay.getId(),
                        data.getLocation().x(),
                        data.getLocation().y(),
                        data.getLocation().z(),
                        data.getLocation().getYaw(),
                        data.getLocation().getPitch(),
                        true)
                .send(fsPlayer);
        FancySitula.ENTITY_FACTORY.setEntityDataFor(fsPlayer, fsDisplay);
    }

    @Override
    public Object toJson() {
        return switch (data.getType()) {
            case TEXT -> JsonAdapter.toUnion((TextHologramData) data);
            case ITEM -> JsonAdapter.toUnion((ItemHologramData) data);
            case BLOCK -> JsonAdapter.toUnion((BlockHologramData) data);
        };
    }

    private void syncWithData(Component text) {
        if (fsDisplay == null) {
            return;
        }

        // location data
        final Location location = data.getLocation();
        if (FancyHologramsPlugin.get().getFHConfiguration().isHologramRotationImprovementEnabled()) {
            fsDisplay.setLocation(location.clone().setRotation(0, 0));

            // set yaw and pitch
            HologramRotation rot = new HologramRotation(location.getYaw() + 180, location.getPitch(), 0);
            fsDisplay.setLeftRotation(rot.getQuaternion());
        } else {
            fsDisplay.setLocation(location);
        }


        if (fsDisplay instanceof FS_TextDisplay textDisplay && data instanceof TextHologramData textData) {
            textDisplay.setText(text);

            // line width
            textDisplay.setLineWidth(Hologram.LINE_WIDTH);

            // opacity
            textDisplay.setTextOpacity(textData.getTextOpacity());

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

//            BlockType blockType = RegistryAccess.registryAccess().getRegistry(RegistryKey.BLOCK).get(blockData.getBlock().getKey());
            if (blockData.getBlockData() != null) {
                blockDisplay.setBlock(blockData.getBlockData().createBlockState());
            }
        }

        if (data instanceof DisplayHologramData displayData) {
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
            if (!FancyHologramsPlugin.get().getFHConfiguration().isHologramRotationImprovementEnabled()) {
                fsDisplay.setLeftRotation(new Quaternionf());
                fsDisplay.setRightRotation(new Quaternionf());
            }

            // entity shadow
            fsDisplay.setShadowRadius(displayData.getShadowRadius());
            fsDisplay.setShadowStrength(displayData.getShadowStrength());

            // glowing
            if (displayData.getGlowingColor() != null && displayData.getGlowingColor() != GlowingColor.DISABLED && displayData.getGlowingColor().getColor() != null) {
                byte currentFlags = 0;
                try {
                    currentFlags = fsDisplay.getSharedFlags();
                } catch (NullPointerException _) {

                }
                fsDisplay.setSharedFlags((byte) (currentFlags | 0x40));

                fsDisplay.setGlowColorOverride(displayData.getGlowingColor().getColor().value());
            } else {
                try {
                    byte currentFlags = fsDisplay.getSharedFlags();
                    fsDisplay.setSharedFlags((byte) (currentFlags & ~0x40));
                } catch (NullPointerException _) {

                }

                fsDisplay.setGlowColorOverride(-1);
            }

            fsDisplay.setViewRange(displayData.getVisibilityDistance());
        }
    }

}
