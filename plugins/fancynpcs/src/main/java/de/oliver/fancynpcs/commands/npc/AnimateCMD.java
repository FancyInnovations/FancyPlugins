package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
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
import java.util.Set;

public enum AnimateCMD {
    INSTANCE;

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc animate <npc> <animation>")
    @Permission("fancynpcs.command.npc.animate")
    public void onAnimate(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "AnimateCMD/animation") String animation
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

        Set<String> animations = BetterModelHook.getAnimations(modelName);
        if (!animations.contains(animation)) {
            translator.translate("animation_not_found")
                    .replace("animation", animation)
                    .replace("model", modelName)
                    .send(sender);
            return;
        }

        FancyNpcs.getInstance().getModelController().playAnimation(npc, animation);
        translator.translate("npc_animation_played")
                .replace("npc", npc.getData().getName())
                .replace("animation", animation)
                .send(sender);
    }

    @Suggestions("AnimateCMD/animation")
    public List<String> suggestAnimation(final CommandContext<CommandSender> context, final CommandInput input) {
        if (!BetterModelHook.isAvailable()) {
            return List.of();
        }

        Npc npc = context.getOrDefault("npc", null);
        if (npc == null) {
            return List.of();
        }

        String modelName = npc.getData().getModelName();
        if (modelName == null) {
            return List.of();
        }

        return new ArrayList<>(BetterModelHook.getAnimations(modelName));
    }
}
