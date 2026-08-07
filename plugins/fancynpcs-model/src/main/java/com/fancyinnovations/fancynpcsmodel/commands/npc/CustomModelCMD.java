package com.fancyinnovations.fancynpcsmodel.commands.npc;

import com.fancyinnovations.fancynpcsmodel.fancynpcshook.CustomModelAttribute;
import com.fancyinnovations.fancynpcsmodel.providers.ModelProviderRegistry;
import com.fancyinnovations.fancynpcsmodel.utils.FancyContext;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CustomModelCMD extends FancyContext {

    public static final CustomModelCMD INSTANCE = new CustomModelCMD();

    private CustomModelCMD() {
    }

    @Command("npc custom_model <npc> <model>")
    @Permission("fancynpcsmodel.command.npc.custom_model")
    public void onCustomModel(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "CustomModelCMD/model") String model
    ) {
        NpcAttribute customModelAttribute = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(EntityType.PLAYER, CustomModelAttribute.ATTRIBUTE_NAME);

        if (model.equalsIgnoreCase(CustomModelAttribute.NONE_VALUE)) {
            npc.getData().removeAttribute(customModelAttribute);
            CustomModelAttribute.removeModels(npc);
            npc.updateForAll();

            translator.translate("commands.npc.custom_model.removed")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .send(sender);
            return;
        }

        ModelProviderRegistry.ResolvedModel resolved = ModelProviderRegistry.resolve(model);
        if (resolved == null) {
            translator.translate("common.model_not_found")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("model", model)
                    .send(sender);
            return;
        }


        npc.getData().addAttribute(customModelAttribute, model);
        npc.updateForAll();

        translator.translate("commands.npc.custom_model.applied")
                .withPrefix()
                .replace("npc", npc.getData().getName())
                .replace("model", resolved.modelName())
                .replace("provider", resolved.provider().getDisplayName())
                .send(sender);
    }

    @Suggestions("CustomModelCMD/model")
    public List<String> suggestModels(final CommandContext<CommandSender> context, final CommandInput input) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add(CustomModelAttribute.NONE_VALUE);
        suggestions.addAll(ModelProviderRegistry.suggestionValues());
        return suggestions;
    }
}
