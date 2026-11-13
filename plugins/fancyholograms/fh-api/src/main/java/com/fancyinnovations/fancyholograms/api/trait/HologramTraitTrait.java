package com.fancyinnovations.fancyholograms.api.trait;

import com.fancyinnovations.fancyholograms.api.events.HologramTraitAttachedEvent;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@HologramTraitClass(traitName = "trait_trait")
public class HologramTraitTrait extends HologramTrait {

    private final List<HologramTrait> traits;

    public HologramTraitTrait() {
        this.traits = new ArrayList<>();
    }

    public void addTrait(HologramTrait trait) {
        Objects.requireNonNull(trait, "trait");

        // Attach the trait to the hologram if hologram already exists
        if (hologram != null) {
            if (!new HologramTraitAttachedEvent(hologram, trait, false).callEvent()) {
                return;
            }

            this.traits.add(trait);
            try {
                trait.attachHologram(hologram);
            } catch (RuntimeException | Error e) {
                this.traits.remove(trait);
                throw e;
            }
        } else {
            this.traits.add(trait);
        }
    }

    public void addTrait(Class<? extends HologramTrait> trait) {
        try {
            HologramTrait newTrait = trait.getConstructor().newInstance();
            addTrait(newTrait);
        } catch (Exception e) {
            logger.error("Failed to instantiate trait " + trait.getSimpleName(), ThrowableProperty.of(e));
        }
    }

    public void removeTrait(HologramTrait trait) {
        if (this.traits.remove(trait)) {
            // Detach the trait from the hologram if it was successfully removed
            if (hologram != null) {
                trait.onUnregister();
                logger.debug("Detached trait " + trait.getClass().getSimpleName() + " from hologram " + hologram.getData().getName());
            }
        } else {
            logger.warn("Trait " + trait.getClass().getSimpleName() + " not found in hologram " + hologram.getData().getName());
        }
    }

    public void removeTrait(Class<? extends HologramTrait> trait) {
        for (HologramTrait t : this.traits) {
            if (t.getClass().equals(trait)) {
                removeTrait(t);
                return;
            }
        }
        logger.warn("Trait " + trait.getSimpleName() + " not found in hologram " + hologram.getData().getName());
    }

    @Override
    public void onAttach() {
        // Attach all default traits to the hologram
        List<HologramTraitRegistry.TraitInfo> registeredTraits = api.getTraitRegistry().getTraits();
        for (HologramTraitRegistry.TraitInfo ti : registeredTraits) {
            if (!ti.isDefault()) {
                continue;
            }

            try {
                HologramTrait trait = ti.clazz().getConstructor().newInstance();
                this.traits.add(trait);
            } catch (Exception e) {
                logger.error("Failed to instantiate default trait " + ti.name(), ThrowableProperty.of(e));
            }

            logger.debug("Attached default trait " + ti.name() + " to hologram " + hologram.getData().getName());
        }

        // Attach all traits that were added to the hologram
        Iterator<HologramTrait> iterator = traits.iterator();
        while (iterator.hasNext()) {
            HologramTrait trait = iterator.next();
            try {
                if (!new HologramTraitAttachedEvent(hologram, trait, false).callEvent()) {
                    iterator.remove();
                    continue;
                }

                trait.attachHologram(hologram);
            } catch (RuntimeException | LinkageError e) {
                iterator.remove();
                logger.error("Failed to attach trait " + trait.getName() + " to hologram " + hologram.getData().getName(), ThrowableProperty.of(e));
            }
        }
    }

    @Override
    public void onSpawn(Player player) {
        for (HologramTrait trait : this.traits) {
            trait.onSpawn(player);
        }
    }

    @Override
    public void onDespawn(Player player) {
        for (HologramTrait trait : this.traits) {
            trait.onDespawn(player);
        }
    }

    @Override
    public void onUpdate(Player player) {
        for (HologramTrait trait : this.traits) {
            trait.onUpdate(player);
        }
    }

    @Override
    public void onModify() {
        for (HologramTrait trait : this.traits) {
            trait.onModify();
        }
    }

    @Override
    public void onRegister() {
        for (HologramTrait trait : this.traits) {
            trait.onRegister();
        }
    }

    @Override
    public void onUnregister() {
        for (HologramTrait trait : this.traits) {
            trait.onUnregister();
        }
    }

    @Override
    public void load() {
        for (HologramTrait trait : this.traits) {
            trait.load();
        }
    }

    @Override
    public void save() {
        for (HologramTrait trait : this.traits) {
            trait.save();
        }
    }

    public List<HologramTrait> getTraits() {
        return traits;
    }

}
