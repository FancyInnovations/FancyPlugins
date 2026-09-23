package de.oliver.fancynpcs.commands.lampCommands.types;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public final class AttributeCommandType implements ParameterType<BukkitCommandActor, NpcAttribute> {

    public static final AttributeCommandType INSTANCE = new AttributeCommandType();

    private AttributeCommandType() {
    }

    @Override
    public NpcAttribute parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String value = input.readString();
        Npc npc = context.getResolvedArgument("npc");

        NpcAttribute attribute = FancyNpcs.getInstance().getAttributeManager().getAttributeByName(npc.getData().getType(), value);
        if (attribute == null) {
            throw new FancyNpcsParameterException("command_invalid_attribute", value);
        }

        return attribute;
    }
}
