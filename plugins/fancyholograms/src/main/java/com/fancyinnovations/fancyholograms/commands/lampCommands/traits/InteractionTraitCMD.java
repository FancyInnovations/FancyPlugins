package com.fancyinnovations.fancyholograms.commands.lampCommands.traits;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.HasHologramTrait;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.NpcActionSuggestion;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.fancyinnovations.fancyholograms.trait.builtin.InteractionTrait;
import de.oliver.fancylib.translations.Translator;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.List;

public class InteractionTraitCMD {

    public static final InteractionTraitCMD INSTANCE = new InteractionTraitCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private InteractionTraitCMD() {

    }

    @Command("hologramtrait interaction <hologram> info")
    @Description("Displays information about the interaction trait for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.interaction.info")
    @HasHologramTrait(InteractionTrait.class)
    public void info(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        final InteractionTrait trait = hologram.getData().getTraitTrait().getTrait(InteractionTrait.class);
        assert trait != null;

        translator.translate("commands.hologramtrait.interaction.info.header")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(actor.sender());

        translator.translate("commands.hologramtrait.interaction.info.hitbox")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("hitbox", trait.getHitbox().getData().getName())
                .send(actor.sender());

        List<InteractionTrait.ActionConfig> actions = trait.getConfig().actions();
        translator.translate("commands.hologramtrait.interaction.info.actions_header")
                .withPrefix()
                .replace("count", String.valueOf(actions.size()))
                .send(actor.sender());

        for (int i = 0; i < actions.size(); i++) {
            InteractionTrait.ActionConfig action = actions.get(i);

            translator.translate("commands.hologramtrait.interaction.info.actions_entry")
                    .withPrefix()
                    .replace("index", String.valueOf(i + 1))
                    .replace("action", action.action())
                    .replace("value", action.value())
                    .send(actor.sender());
        }
    }

    @Command("hologramtrait interaction <hologram> update_hitbox")
    @Description("Updates the hitbox for the interaction trait for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.interaction.update_hitbox")
    @HasHologramTrait(InteractionTrait.class)
    public void updateHitbox(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        final InteractionTrait trait = hologram.getData().getTraitTrait().getTrait(InteractionTrait.class);
        assert trait != null;

        trait.updateHitbox();

        translator.translate("commands.hologramtrait.interaction.update_hitbox.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(actor.sender());
    }

    @Command("hologramtrait interaction <hologram> add_action <action> <value>")
    @Description("Adds an action to the interaction trait for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.interaction.add_action")
    @HasHologramTrait(InteractionTrait.class)
    public void addAction(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(NpcActionSuggestion.class) String action,
            final @NotNull String value
    ) {
        final InteractionTrait trait = hologram.getData().getTraitTrait().getTrait(InteractionTrait.class);
        assert trait != null;

        List<InteractionTrait.ActionConfig> actions = trait.getConfig().actions();
        actions.add(new InteractionTrait.ActionConfig(action, value));
        trait.setConfig(new InteractionTrait.Configuration(actions));

        translator.translate("commands.hologramtrait.interaction.add_action.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("action", action)
                .replace("value", value)
                .send(actor.sender());
    }

    @Command("hologramtrait interaction <hologram> remove_action <index>")
    @Description("Removes an action from the interaction trait for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.interaction.remove_action")
    @HasHologramTrait(InteractionTrait.class)
    public void removeAction(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final int index
    ) {
        final InteractionTrait trait = hologram.getData().getTraitTrait().getTrait(InteractionTrait.class);
        assert trait != null;

        List<InteractionTrait.ActionConfig> actions = trait.getConfig().actions();
        if (index < 1 || index > actions.size()) {
            translator.translate("commands.hologramtrait.interaction.remove_action.invalid_index")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("index", String.valueOf(index))
                    .replace("min", "1")
                    .replace("max", String.valueOf(actions.size()))
                    .send(actor.sender());
            return;
        }

        InteractionTrait.ActionConfig removedAction = actions.remove(index - 1);
        trait.setConfig(new InteractionTrait.Configuration(actions));

        translator.translate("commands.hologramtrait.interaction.remove_action.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("action", removedAction.action())
                .replace("value", removedAction.value())
                .send(actor.sender());
    }

    @Command("hologramtrait interaction <hologram> clear_actions")
    @Description("Clears all actions from the interaction trait for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.interaction.clear_actions")
    @HasHologramTrait(InteractionTrait.class)
    public void clearActions(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram
    ) {
        final InteractionTrait trait = hologram.getData().getTraitTrait().getTrait(InteractionTrait.class);
        assert trait != null;

        trait.setConfig(new InteractionTrait.Configuration(List.of()));

        translator.translate("commands.hologramtrait.interaction.clear_actions.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .send(actor.sender());
    }

    @Command("hologramtrait interaction <hologram> set_action <index> <action> <value>")
    @Description("Sets an action at a specific index for the interaction trait for the hologram")
    @CommandPermission("fancyholograms.commands.hologramtrait.interaction.set_action")
    @HasHologramTrait(InteractionTrait.class)
    public void setAction(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final int index,
            final @NotNull @SuggestWith(NpcActionSuggestion.class) String action,
            final @NotNull String value
    ) {
        final InteractionTrait trait = hologram.getData().getTraitTrait().getTrait(InteractionTrait.class);
        assert trait != null;

        List<InteractionTrait.ActionConfig> actions = trait.getConfig().actions();
        if (index < 1 || index > actions.size()) {
            translator.translate("commands.hologramtrait.interaction.set_action.invalid_index")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("index", String.valueOf(index))
                    .replace("min", "1")
                    .replace("max", String.valueOf(actions.size()))
                    .send(actor.sender());
            return;
        }

        actions.set(index - 1, new InteractionTrait.ActionConfig(action, value));
        trait.setConfig(new InteractionTrait.Configuration(actions));

        translator.translate("commands.hologramtrait.interaction.set_action.success")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("index", String.valueOf(index))
                .replace("action", action)
                .replace("value", value)
                .send(actor.sender());
    }

}
