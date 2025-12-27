package de.oliver.fancynpcs.bettermodel;

import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.data.renderer.ModelRenderer;
import kr.toxicity.model.api.tracker.EntityTrackerRegistry;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public final class BetterModelHook {

    private static final boolean AVAILABLE;

    static {
        boolean found = false;
        try {
            Class.forName("kr.toxicity.model.api.BetterModel");
            found = true;
        } catch (ClassNotFoundException ignored) {
        }
        AVAILABLE = found;
    }

    private BetterModelHook() {
    }

    public static boolean isAvailable() {
        return AVAILABLE;
    }

    public static Optional<ModelRenderer> getModel(String name) {
        if (!AVAILABLE) {
            return Optional.empty();
        }
        return BetterModel.model(name);
    }

    public static Optional<EntityTrackerRegistry> getRegistry(Entity entity) {
        if (!AVAILABLE) {
            return Optional.empty();
        }
        return BetterModel.registry(entity);
    }

    public static Set<String> getModelNames() {
        if (!AVAILABLE) {
            return Collections.emptySet();
        }
        return BetterModel.modelKeys();
    }

    public static Set<String> getAnimations(String modelName) {
        if (!AVAILABLE) {
            return Collections.emptySet();
        }
        return getModel(modelName)
                .map(renderer -> renderer.animations().keySet())
                .orElse(Collections.emptySet());
    }
}
