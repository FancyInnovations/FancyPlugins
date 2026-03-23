package com.fancyinnovations.fancyholograms.commands.lampCommands.conditions;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.process.CommandCondition;

public class HologramTraitCondition implements CommandCondition<BukkitCommandActor> {

    public static final HologramTraitCondition INSTANCE = new HologramTraitCondition();

    private HologramTraitCondition() {

    }

    @Override
    public void test(@NotNull ExecutionContext<BukkitCommandActor> context) {
        boolean isOnlyForTrait = context.command().annotations().contains(HasHologramTrait.class);
        if (!isOnlyForTrait) {
            return;
        }
        Class<? extends HologramTrait> traitClass = context.command().annotations().get(HasHologramTrait.class).value();

        Hologram hologram = context.getResolvedArgument("hologram");
        boolean traitAttached = hologram.getData().getTraitTrait().isTraitAttached(traitClass);

        if (!traitAttached) {
            throw new CommandErrorException("You can only use this command on holograms with the " + traitClass.getSimpleName() + " trait attached.");
        }
    }
}
