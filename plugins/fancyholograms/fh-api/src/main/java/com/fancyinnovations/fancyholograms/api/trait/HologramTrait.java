package com.fancyinnovations.fancyholograms.api.trait;

import com.fancyinnovations.fancyholograms.api.FancyHolograms;
import com.fancyinnovations.fancyholograms.api.HologramController;
import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.jdb.JDB;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.ScheduledExecutorService;

/**
 * Represents a trait that can be attached to a hologram. This class provides a structure for
 * managing the lifecycle of traits related to holograms. It defines methods to handle
 * initialization, attachment, updates, and data persistence.
 * <p>
 * Subclasses of this abstract class must implement the specific behavior of the trait by
 * overriding the provided lifecycle methods.
 */
@ApiStatus.Experimental
public abstract class HologramTrait {

    protected final FancyHolograms api = FancyHolograms.get();
    protected final ExtendedFancyLogger logger = api.getFancyLogger();
    protected final HologramController controller = api.getController();
    protected final HologramRegistry registry = api.getRegistry();
    protected final ScheduledExecutorService hologramThread = api.getHologramThread();
    protected Hologram hologram;
    protected JDB storage;

    @ApiStatus.Internal
    public final void attachHologram(Hologram hologram) {
        if (this.hologram != null) {
            throw new IllegalStateException("Trait is already attached to a hologram");
        }

        this.hologram = hologram;
        this.storage = new JDB("plugins/FancyHolograms/data/traits/" + getName());

        try {
            onAttach();
        } catch (RuntimeException | Error e) {
            try {
                onUnregister();
            } catch (RuntimeException | Error cleanupError) {
                e.addSuppressed(cleanupError);
            }
            this.hologram = null;
            this.storage = null;
            throw e;
        }
    }

    /**
     * Called when the trait is attached to a hologram.
     * The hologram is available at this point.
     */
    public void onAttach() {
    }

    /**
     * Called when the hologram is spawned to a player.
     */
    public void onSpawn(Player player) {
    }

    /**
     * Called when the hologram is despawned from a player.
     */
    public void onDespawn(Player player) {
    }

    /**
     * Called when the trait is updated to a player.
     */
    public void onUpdate(Player player) {
    }

    /**
     * Called when the hologram is modified.
     */
    public void onModify() {
    }

    /**
     * Called when the hologram is registered in the registry.
     */
    public void onRegister() {
    }

    /**
     * Called when the hologram is unregistered from the registry.
     */
    public void onUnregister() {
    }

    /**
     * Called when the hologram is being loaded.
     * In this method you should load all necessary data for the trait.
     */
    public void load() {
    }

    /**
     * Called when the hologram is being saved.
     * In this method you should save all necessary data for the trait.
     */
    public void save() {
    }

    public final String getName() {
        if (getClass().isAnnotationPresent(HologramTraitClass.class)) {
            return getClass().getAnnotation(HologramTraitClass.class).traitName();
        }

        throw new IllegalArgumentException("Trait class " + getClass() + " is not annotated with HologramTraitClass");
    }

    public final boolean isTraitAttached(Class<? extends HologramTrait> trait) {
        for (HologramTrait hologramTrait : hologram.getData().getTraitTrait().getTraits()) {
            if (hologramTrait.getClass().equals(trait)) {
                return true;
            }
        }

        return false;
    }

    public final <T extends HologramTrait> T getTrait(Class<T> trait) {
        for (HologramTrait hologramTrait : hologram.getData().getTraitTrait().getTraits()) {
            if (hologramTrait.getClass().equals(trait)) {
                return (T) hologramTrait;
            }
        }

        return null;
    }

    public final Hologram getHologram() {
        return hologram;
    }
}
