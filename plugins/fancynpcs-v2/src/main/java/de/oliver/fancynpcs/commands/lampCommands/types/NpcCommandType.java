package de.oliver.fancynpcs.commands.lampCommands.types;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.UUID;

public final class NpcCommandType implements ParameterType<BukkitCommandActor, Npc> {

    public static final NpcCommandType INSTANCE = new NpcCommandType();

    private NpcCommandType() {
    }

    private static boolean isUuid(String value) {
        try {
            UUID.fromString(value);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    @Override
    public Npc parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String value = input.readString();

        Npc npc;
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && context.actor().isPlayer()) {
            Player player = context.actor().requirePlayer();
            npc = FancyNpcs.getInstance().getNpcManager().getNpc(value, player.getUniqueId());
        } else {
            npc = isUuid(value)
                    ? FancyNpcs.getInstance().getNpcManager().getNpcById(value)
                    : FancyNpcs.getInstance().getNpcManager().getNpc(value);
        }

        if (npc == null) {
            throw new FancyNpcsParameterException("command_invalid_npc", value);
        }
        return npc;
    }

    @Override
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> {
            if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && context.actor().isPlayer()) {
                return FancyNpcs.getInstance().getNpcManager().getAllNpcs().stream()
                        .filter(npc -> npc.getData().getCreator().equals(context.actor().requirePlayer().getUniqueId()))
                        .map(npc -> npc.getData().getName()).toList();
            }
            return FancyNpcs.getInstance().getNpcManager().getAllNpcs().stream()
                    .map(npc -> npc.getData().getName()).toList();
        };
    }
}
