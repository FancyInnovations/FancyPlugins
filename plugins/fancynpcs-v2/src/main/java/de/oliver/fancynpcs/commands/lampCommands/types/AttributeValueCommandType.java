package de.oliver.fancynpcs.commands.lampCommands.types;

import de.oliver.fancynpcs.api.NpcAttribute;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class AttributeValueCommandType implements ParameterType<BukkitCommandActor, String> {

    public static final AttributeValueCommandType INSTANCE = new AttributeValueCommandType();

    public AttributeValueCommandType() {
    }

    @Override
    public String parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String value = input.consumeRemaining();
        NpcAttribute attribute = context.getResolvedArgument("attribute");
        if (!attribute.isValidValue(value)) {
            throw new FancyNpcsParameterException("command_invalid_attribute_value", value);
        }

        return value;
    }

    @Override
    public boolean isGreedy() {
        return true;
    }
}
