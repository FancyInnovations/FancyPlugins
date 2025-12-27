package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.bettermodel.BetterModelHook;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public enum ModelCMD {
    INSTANCE;

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc model <npc>")
    @Permission("fancynpcs.command.npc.model")
    public void onModelInfo(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc
    ) {
        if (!BetterModelHook.isAvailable()) {
            translator.translate("bettermodel_not_available").send(sender);
            return;
        }

        String currentModel = npc.getData().getModelName();
        if (currentModel == null) {
            translator.translate("npc_model_none").replace("npc", npc.getData().getName()).send(sender);
        } else {
            translator.translate("npc_model_info")
                    .replace("npc", npc.getData().getName())
                    .replace("model", currentModel)
                    .send(sender);
        }
    }

    @Command("npc model <npc> <model>")
    @Permission("fancynpcs.command.npc.model")
    public void onModelSet(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(value = "model", suggestions = "ModelCMD/model") String model
    ) {
        if (!BetterModelHook.isAvailable()) {
            translator.translate("bettermodel_not_available").send(sender);
            return;
        }

        if (model.equalsIgnoreCase("@none") || model.equalsIgnoreCase("none")) {
            String currentModel = npc.getData().getModelName();
            if (currentModel == null) {
                translator.translate("npc_model_none").replace("npc", npc.getData().getName()).send(sender);
                return;
            }

            if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MODEL, null, sender).callEvent()) {
                FancyNpcs.getInstance().getModelController().removeModel(npc);
                npc.removeForAll();
                npc.spawnForAll();
                translator.translate("npc_model_removed").replace("npc", npc.getData().getName()).send(sender);
            } else {
                translator.translate("command_npc_modification_cancelled").send(sender);
            }
            return;
        }

        if (!BetterModelHook.getModelNames().contains(model)) {
            translator.translate("model_not_found").replace("model", model).send(sender);
            return;
        }

        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MODEL, model, sender).callEvent()) {
            boolean success = FancyNpcs.getInstance().getModelController().applyModel(npc, model);
            if (success) {
                translator.translate("npc_model_set")
                        .replace("npc", npc.getData().getName())
                        .replace("model", model)
                        .send(sender);
            } else {
                translator.translate("model_apply_failed").replace("model", model).send(sender);
            }
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Command("npc model <npc> eyeheight <height>")
    @Permission("fancynpcs.command.npc.model")
    public void onModelEyeHeight(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final float height
    ) {
        if (!BetterModelHook.isAvailable()) {
            translator.translate("bettermodel_not_available").send(sender);
            return;
        }

        String modelName = npc.getData().getModelName();
        if (modelName == null) {
            translator.translate("npc_model_none").replace("npc", npc.getData().getName()).send(sender);
            return;
        }

        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.MODEL, height, sender).callEvent()) {
            npc.getData().setModelEyeHeight(height);
            translator.translate("npc_model_eyeheight_set")
                    .replace("npc", npc.getData().getName())
                    .replace("height", String.valueOf(height))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }

    @Suggestions("ModelCMD/model")
    public List<String> suggestModel(final CommandContext<CommandSender> context, final CommandInput input) {
        if (!BetterModelHook.isAvailable()) {
            return List.of();
        }

        List<String> suggestions = new ArrayList<>();
        suggestions.add("@none");
        suggestions.addAll(BetterModelHook.getModelNames());
        return suggestions;
    }
}
