package de.oliver.fancynpcs.commands.lampCommands.types;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.actions.NpcAction;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class ActionTypeCommandType implements ParameterType<BukkitCommandActor, NpcAction> {

    public static final ActionTypeCommandType INSTANCE = new ActionTypeCommandType();

    private ActionTypeCommandType() {
    }

    @Override
    public NpcAction parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String value = input.readString();
        NpcAction action = FancyNpcs.getInstance().getActionManager().getActionByName(value);
        if (action == null) {
            throw new FancyNpcsParameterException("command_invalid_action_type", value);
        }

        return action;
    }

    @Override
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> FancyNpcs.getInstance().getActionManager().getAllActions().stream()
                .map(NpcAction::getName)
                .filter(name -> !name.equalsIgnoreCase("player_command_as_op"))
                .toList();
    }
}
