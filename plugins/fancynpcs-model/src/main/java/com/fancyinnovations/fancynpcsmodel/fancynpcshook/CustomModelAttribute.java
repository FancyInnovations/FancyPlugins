package com.fancyinnovations.fancynpcsmodel.fancynpcshook;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import com.fancyinnovations.fancynpcsmodel.providers.ModelProvider;
import com.fancyinnovations.fancynpcsmodel.providers.ModelProviderRegistry;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.Map;

public class CustomModelAttribute {

    public static final String ATTRIBUTE_NAME = "custom_model";
    public static final String NONE_VALUE = "@none";

    public static NpcAttribute getModelAttribute() {
        return new NpcAttribute(
                ATTRIBUTE_NAME,
                ModelProviderRegistry::suggestionValues,
                List.of(EntityType.PLAYER),
                CustomModelAttribute::setModel
        );
    }

    private static void setModel(Npc npc, String modelName) {
        // remove model if model name is "@none"
        if (modelName == null || modelName.isBlank() || modelName.equalsIgnoreCase(NONE_VALUE)) {
            ModelProviderRegistry.removeFromAll(npc);
            return;
        }

        ModelProviderRegistry.ResolvedModel resolved = ModelProviderRegistry.resolve(modelName);
        if (resolved == null) {
            FancyNpcsModelPlugin.get().getFancyLogger().error(
                    "Failed to find model with name " + modelName + " in any installed model plugin",
                    StringProperty.of("model_name", modelName),
                    StringProperty.of("npc_name", npc.getData().getName())
            );
            return;
        }

        // make sure only the resolved provider has a model attached
        for (ModelProvider provider : ModelProviderRegistry.getProviders()) {
            if (provider != resolved.provider() && provider.hasModelApplied(npc)) {
                provider.removeModel(npc);
            }
        }

        resolved.provider().applyModel(npc, resolved.modelName());
    }

    /**
     * Removes the models of all providers from the given NPC.
     * This is necessary to prevent old models still existing in the world.
     */
    public static void removeModels(Npc npc) {
        ModelProviderRegistry.removeFromAll(npc);
    }

    /**
     * @return whether the given NPC has the model attribute
     */
    public static boolean hasAttribute(Npc npc) {
        for (Map.Entry<NpcAttribute, String> entry : npc.getData().getAttributes().entrySet()) {
            if (entry.getKey().getName().equalsIgnoreCase(ATTRIBUTE_NAME)) {
                return true;
            }
        }

        return false;
    }
}
