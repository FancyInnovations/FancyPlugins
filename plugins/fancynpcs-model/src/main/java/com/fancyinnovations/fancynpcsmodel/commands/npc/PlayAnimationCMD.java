package com.fancyinnovations.fancynpcsmodel.commands.npc;

import com.fancyinnovations.fancynpcsmodel.fancynpcshook.ModelAttribute;
import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import com.fancyinnovations.fancynpcsmodel.utils.FancyContext;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import kr.toxicity.model.api.BetterModel;
import kr.toxicity.model.api.animation.AnimationModifier;
import kr.toxicity.model.api.tracker.EntityTracker;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Flag;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PlayAnimationCMD extends FancyContext {

    public static final PlayAnimationCMD INSTANCE = new PlayAnimationCMD();

    private PlayAnimationCMD() {
    }

    @Command("npc play_animation <npc> <animation>")
    @Permission("fancynpcsmodel.command.npc.custom_model")
    public void onPlayAnimation(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final @NotNull @Argument(suggestions = "PlayAnimationCMD/animation") String animation,
            final @Flag("loop") boolean loop
    ) {
        if (!ModelAttribute.hasAttribute(npc)) {
            translator.translate("commands.npc.play_animation.no_model_assigned")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .send(sender);
            return;
        }

        EntityTracker tracker = ModelAttribute.getEntityTracker(npc);

        AnimationModifier modifier = loop ? AnimationModifier.DEFAULT : AnimationModifier.DEFAULT_WITH_PLAY_ONCE;
        if (!tracker.animate(animation, modifier)) {
            translator.translate("commands.npc.play_animation.failed")
                    .withPrefix()
                    .replace("npc", npc.getData().getName())
                    .replace("animation", animation)
                    .send(sender);
            return;
        }

        translator.translate("commands.npc.play_animation.playing")
                .withPrefix()
                .replace("npc", npc.getData().getName())
                .replace("animation", animation)
                .send(sender);
    }

    @Suggestions("PlayAnimationCMD/animation")
    public List<String> suggestAnimations(final CommandContext<CommandSender> context, final CommandInput input) {
        EntityTracker tracker = ModelAttribute.getEntityTracker(context.get("npc"));
        if (tracker == null) return new ArrayList<>();

        return tracker.renderer().animations().keySet().stream().toList();
    }
}
