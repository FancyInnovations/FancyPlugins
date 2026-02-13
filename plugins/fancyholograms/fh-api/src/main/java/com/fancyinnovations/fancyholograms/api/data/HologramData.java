package com.fancyinnovations.fancyholograms.api.data;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.data.property.Visibility;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitTrait;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;

public class HologramData implements YamlData {

    public static final int DEFAULT_VISIBILITY_DISTANCE = -1;
    public static final Visibility DEFAULT_VISIBILITY = Visibility.ALL;
    public static final boolean DEFAULT_IS_VISIBLE = true;
    public static final boolean DEFAULT_PERSISTENCE = true;
    protected final @NotNull HologramTraitTrait traitTrait;
    private final String name;
    private final HologramType type;
    private String filePath;
    private Location location;
    private String worldName;
    private boolean hasChanges = false;
    private Runnable onModify;
    private int visibilityDistance = DEFAULT_VISIBILITY_DISTANCE;
    private Visibility visibility = DEFAULT_VISIBILITY;
    private boolean persistent = DEFAULT_PERSISTENCE;
    private String linkedNpcName;

    /**
     * @param name     Name of hologram
     * @param type     Type of hologram
     * @param location Location of hologram
     *                 Default values are already set
     */
    public HologramData(String name, HologramType type, Location location) {
        this.name = name;
        this.type = type;
        this.location = location;
        if (location != null && location.getWorld() != null) {
            this.worldName = location.getWorld().getName();
        } else {
            this.worldName = null;
        }
        this.traitTrait = new HologramTraitTrait();
    }

    public @NotNull String getName() {
        return name;
    }

    public @NotNull HologramType getType() {
        return type;
    }

    @ApiStatus.Internal
    public String getFilePath() {
        return filePath;
    }

    @ApiStatus.Internal
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public @NotNull Location getLocation() {
        return location.clone();
    }

    public HologramData setLocation(@Nullable Location location) {
        if (location == null) {
            this.location = null;
            this.worldName = null;
        } else {
            this.location = location.clone();
            if (this.location.getWorld() == null) {
                this.worldName = null;
            }
        }

        setHasChanges(true);
        return this;
    }

    public String getWorldName() {
        return worldName;
    }

    /**
     * Use setLocation instead of this method.
     */
    @ApiStatus.Internal
    public HologramData setWorldName(String worldName) {
        this.worldName = worldName;
        if (this.location != null) {
            World world = Bukkit.getWorld(worldName);
            if (world != null) {
                this.location.setWorld(world);
            }
        }
        setHasChanges(true);
        return this;
    }

    @ApiStatus.Internal
    public HologramData setWorld(World world) {
        if (this.location == null) {
            this.location = new Location(world, 0, 0, 0);
        } else {
            this.location.setWorld(world);
        }

        setHasChanges(true);
        return this;
    }

    /**
     * @return Whether the hologram needs to send an update to players
     */
    @ApiStatus.Internal
    public final boolean hasChanges() {
        return hasChanges;
    }

    /**
     * @param hasChanges Whether the hologram needs to send an update to players
     */
    @ApiStatus.Internal
    public final void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
        if (hasChanges && onModify != null) {
            onModify.run();
        }
    }

    @ApiStatus.Internal
    public void setOnModify(Runnable onModify) {
        this.onModify = onModify;
    }

    public int getVisibilityDistance() {
        if (visibilityDistance > 0) {
            return visibilityDistance;
        }

        return FancyHolograms.get().getHologramConfiguration().getDefaultVisibilityDistance();
    }

    public HologramData setVisibilityDistance(int visibilityDistance) {
        this.visibilityDistance = visibilityDistance;
        setHasChanges(true);
        return this;
    }

    /**
     * Get the type of visibility for the hologram.
     *
     * @return type of visibility.
     */
    public Visibility getVisibility() {
        return this.visibility;
    }

    /**
     * Set the type of visibility for the hologram.
     */
    public HologramData setVisibility(@NotNull Visibility visibility) {
        if (!Objects.equals(this.visibility, visibility)) {
            this.visibility = visibility;
            setHasChanges(true);

            if (this.visibility.equals(Visibility.MANUAL)) {
                Visibility.ManualVisibility.clear();
            }
        }

        return this;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public HologramData setPersistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    public String getLinkedNpcName() {
        return linkedNpcName;
    }

    public HologramData setLinkedNpcName(String linkedNpcName) {
        if (!Objects.equals(this.linkedNpcName, linkedNpcName)) {
            this.linkedNpcName = linkedNpcName;
            setHasChanges(true);
        }

        return this;
    }

    @ApiStatus.Experimental
    public @NotNull HologramTraitTrait getTraitTrait() {
        return traitTrait;
    }

    @ApiStatus.Experimental
    public HologramData addTrait(HologramTrait trait) {
        traitTrait.addTrait(trait);
        return this;
    }

    @ApiStatus.Experimental
    public HologramData addTrait(Class<? extends HologramTrait> traitClass) {
        HologramTrait trait = null;
        try {
            trait = traitClass.getConstructor(null).newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            FancyHolograms.get().getFancyLogger().error("Failed to instantiate trait " + traitClass.getSimpleName(), ThrowableProperty.of(e));
        }

        traitTrait.addTrait(trait);
        return this;
    }

    @Override
    @ApiStatus.Internal
    public boolean read(ConfigurationSection section, String name) {
        String worldName = section.getString("location.world", "world");
        float x = (float) section.getDouble("location.x", 0);
        float y = (float) section.getDouble("location.y", 0);
        float z = (float) section.getDouble("location.z", 0);
        float yaw = (float) section.getDouble("location.yaw", 0);
        float pitch = (float) section.getDouble("location.pitch", 0);

        World world = Bukkit.getWorld(worldName);
        location = new Location(world, x, y, z, yaw, pitch);
        visibilityDistance = section.getInt("visibility_distance", DEFAULT_VISIBILITY_DISTANCE);
        visibility = Optional.ofNullable(section.getString("visibility"))
                .flatMap(Visibility::byString)
                .orElseGet(() -> {
                    final var visibleByDefault = section.getBoolean("visible_by_default", DisplayHologramData.DEFAULT_IS_VISIBLE);
                    return visibleByDefault ? Visibility.ALL : Visibility.PERMISSION_REQUIRED;
                });
        linkedNpcName = section.getString("linkedNpc");

        return true;
    }

    @Override
    @ApiStatus.Internal
    public boolean write(ConfigurationSection section, String name) {
        section.set("type", type.name());
        section.set("location.world", location.getWorld().getName());
        section.set("location.x", location.x());
        section.set("location.y", location.y());
        section.set("location.z", location.z());
        section.set("location.yaw", location.getYaw());
        section.set("location.pitch", location.getPitch());

        section.set("visibility_distance", visibilityDistance);
        section.set("visibility", visibility.name());
        section.set("persistent", persistent);
        section.set("linkedNpc", linkedNpcName);

        return true;
    }

    public HologramData copy(String name) {
        return new HologramData(name, type, this.getLocation())
                .setVisibilityDistance(this.getVisibilityDistance())
                .setVisibility(this.getVisibility())
                .setPersistent(this.isPersistent())
                .setLinkedNpcName(this.getLinkedNpcName());
    }
}
