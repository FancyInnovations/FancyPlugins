package com.fancyinnovations.fancynpcsmodel.providers;

import com.fancyinnovations.fancynpcsmodel.providers.bettermodel.BetterModelProvider;
import com.fancyinnovations.fancynpcsmodel.providers.modelengine.ModelEngineProvider;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Keeps track of all available model providers.
 * <p>
 * Providers are checked in registration order when resolving unprefixed model
 * names: BetterModel first (for backwards compatibility), then ModelEngine.
 * A model name can be prefixed (e.g. "bm:my_model" or "me:my_model") to force
 * a specific provider.
 */
public final class ModelProviderRegistry {

    private static final List<ModelProvider> providers = new ArrayList<>();

    private ModelProviderRegistry() {
    }

    public static void init(ExtendedFancyLogger logger) {
        providers.clear();

        if (Bukkit.getPluginManager().getPlugin("BetterModel") != null) {
            providers.add(new BetterModelProvider());
            logger.info("Found BetterModel - enabling BetterModel support");
        }

        if (Bukkit.getPluginManager().getPlugin("ModelEngine") != null) {
            providers.add(new ModelEngineProvider());
            logger.info("Found ModelEngine - enabling ModelEngine support");
        }
    }

    public static List<ModelProvider> getProviders() {
        return Collections.unmodifiableList(providers);
    }

    public static boolean isEmpty() {
        return providers.isEmpty();
    }

    /**
     * Resolves a raw model value (optionally prefixed with a provider prefix,
     * e.g. "bm:my_model" or "modelengine:my_model") to a provider and model name.
     *
     * @return the resolved model or null if no installed provider has a model with that name
     */
    public static @Nullable ResolvedModel resolve(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        int colon = value.indexOf(':');
        if (colon > 0 && colon < value.length() - 1) {
            String prefix = value.substring(0, colon).toLowerCase(Locale.ROOT);
            String modelName = value.substring(colon + 1);

            for (ModelProvider provider : providers) {
                if (provider.getPrefixes().contains(prefix)) {
                    return provider.hasModel(modelName) ? new ResolvedModel(provider, modelName) : null;
                }
            }
        }

        for (ModelProvider provider : providers) {
            if (provider.hasModel(value)) {
                return new ResolvedModel(provider, value);
            }
        }

        return null;
    }

    /**
     * @return the provider that currently has a model attached to the NPC or null
     */
    public static @Nullable ModelProvider getActiveProvider(Npc npc) {
        for (ModelProvider provider : providers) {
            if (provider.hasModelApplied(npc)) {
                return provider;
            }
        }

        return null;
    }

    /**
     * Removes the models of all providers from the NPC.
     */
    public static void removeFromAll(Npc npc) {
        for (ModelProvider provider : providers) {
            provider.removeModel(npc);
        }
    }

    /**
     * @return all model names of all providers (plus prefixed variants if multiple providers are installed)
     */
    public static List<String> suggestionValues() {
        Set<String> names = new LinkedHashSet<>();

        for (ModelProvider provider : providers) {
            names.addAll(provider.getModelNames());
        }

        if (providers.size() > 1) {
            for (ModelProvider provider : providers) {
                String prefix = provider.getPrefixes().iterator().next();
                for (String modelName : provider.getModelNames()) {
                    names.add(prefix + ":" + modelName);
                }
            }
        }

        return new ArrayList<>(names);
    }

    public static void shutdown() {
        for (ModelProvider provider : providers) {
            provider.shutdown();
        }
    }

    public record ResolvedModel(ModelProvider provider, String modelName) {
    }
}
