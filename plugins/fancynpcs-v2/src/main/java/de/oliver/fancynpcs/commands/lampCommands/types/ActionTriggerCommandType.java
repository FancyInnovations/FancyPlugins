package de.oliver.fancynpcs.commands.lampCommands.types;

import de.oliver.fancynpcs.api.actions.ActionTrigger;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.List;

public final class ActionTriggerCommandType implements ParameterType<BukkitCommandActor, ActionTrigger> {
    public static final ActionTriggerCommandType INSTANCE = new ActionTriggerCommandType();

    private ActionTriggerCommandType() {
    }

    @Override
    public ActionTrigger parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String value = input.readString();
        ActionTrigger trigger = ActionTrigger.getByName(value);
        if (trigger == null) {
            throw new FancyNpcsParameterException("command_invalid_action_trigger", value);
        }

        return trigger;
    }

    @Override
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> List.of("any_click", "left_click", "right_click");
    }
}
