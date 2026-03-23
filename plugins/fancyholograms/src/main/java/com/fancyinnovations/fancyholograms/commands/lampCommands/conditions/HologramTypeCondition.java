package com.fancyinnovations.fancyholograms.commands.lampCommands.conditions;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.exception.CommandErrorException;
import revxrsal.commands.exception.CommandExceptionHandler;
import revxrsal.commands.exception.context.ErrorContext;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.process.CommandCondition;

import java.util.Arrays;
import java.util.List;

public class HologramTypeCondition implements CommandCondition<BukkitCommandActor>, CommandExceptionHandler<BukkitCommandActor> {

    public static final HologramTypeCondition INSTANCE = new HologramTypeCondition();

    private HologramTypeCondition() {

    }

    @Override
    public void test(@NotNull ExecutionContext<BukkitCommandActor> context) {
        boolean isOnlyForTypes = context.command().annotations().contains(IsHologramType.class);
        if (!isOnlyForTypes) {
            return;
        }
        HologramType[] allowedTypes = context.command().annotations().get(IsHologramType.class).types();

        Hologram hologram = context.getResolvedArgument("hologram");
        boolean hologramIsOfAllowedType = false;
        for (HologramType allowedType : allowedTypes) {
            if (hologram.getData().getType() == allowedType) {
                hologramIsOfAllowedType = true;
                break;
            }
        }


        if (!hologramIsOfAllowedType) {
            throw new InvalidHologramTypeException(hologram.getData().getType(), allowedTypes);
        }
    }

    @Override
    public void handleException(@NotNull Throwable throwable, @NotNull ErrorContext context) {
        if (throwable instanceof InvalidHologramTypeException e) {
            FancyHologramsPlugin.get().getTranslator()
                    .translate("common.hologram.invalid_type")
                    .withPrefix()
                    .replace("types", String.join(", ", e.getExpectedTypes().stream().map(HologramType::name).toList()))
                    .send(((BukkitCommandActor) context.actor()).sender());
        }
    }

    public static class InvalidHologramTypeException extends CommandErrorException {

        private final HologramType got;
        private final List<HologramType> expectedTypes;

        public InvalidHologramTypeException(@NotNull HologramType got, @NotNull HologramType[] expected) {
            super("This command can only be executed on holograms of the following types: " + String.join(", ", Arrays.toString(expected)));
            this.got = got;
            this.expectedTypes = Arrays.asList(expected);
        }

        public HologramType getGot() {
            return got;
        }

        public List<HologramType> getExpectedTypes() {
            return expectedTypes;
        }
    }

}
