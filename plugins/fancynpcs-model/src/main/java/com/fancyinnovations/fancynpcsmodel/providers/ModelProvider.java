package com.fancyinnovations.fancynpcsmodel.providers;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Abstraction over a model plugin (e.g. BetterModel or ModelEngine) that can
 * attach custom models to FancyNpcs NPCs.
 */
public interface ModelProvider {

    /**
     * @return unique lowercase id of this provider (e.g. "bettermodel")
     */
    String getId();

    /**
     * @return human friendly name of the backing plugin (e.g. "BetterModel")
     */
    String getDisplayName();

    /**
     * @return prefixes that can be used in model names to force this provider (e.g. "bm:my_model")
     */
    Collection<String> getPrefixes();

    /**
     * @return all model names that are currently loaded in the backing plugin
     */
    Collection<String> getModelNames();

    /**
     * @return whether the backing plugin has a model with the given name
     */
    boolean hasModel(String modelName);

    /**
     * Attaches the given model to the NPC.
     * <p>
     * Implementations must be safe to call multiple times with the same model,
     * because attributes are applied every time an NPC is updated for a player.
     */
    void applyModel(Npc npc, String modelName);

    /**
     * Detaches any model of this provider from the NPC.
     */
    void removeModel(Npc npc);

    /**
     * @return whether this provider currently has a model attached to the NPC
     */
    boolean hasModelApplied(Npc npc);

    /**
     * Plays an animation on the model that is attached to the NPC.
     *
     * @return whether the animation was started successfully
     */
    boolean playAnimation(Npc npc, String animation, boolean loop);

    /**
     * @return names of all animations of the model that is attached to the NPC
     */
    Collection<String> getAnimationNames(Npc npc);

    /**
     * @return whether FancyNpcs' own interaction handling should be cancelled for
     * NPCs using this provider (because the provider routes clicks through its own hitboxes)
     */
    boolean shouldCancelNativeInteraction();

    /**
     * @return a bukkit listener that should be registered for this provider or null
     */
    default @Nullable Listener createListener() {
        return null;
    }

    /**
     * Called when the plugin shuts down.
     */
    default void shutdown() {
    }
}
