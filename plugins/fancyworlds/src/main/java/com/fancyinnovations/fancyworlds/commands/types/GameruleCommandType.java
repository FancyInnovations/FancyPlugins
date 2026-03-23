package com.fancyinnovations.fancyworlds.commands.types;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import net.kyori.adventure.key.Key;
import org.bukkit.GameRule;
import org.bukkit.GameRules;
import org.bukkit.Registry;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.exception.InvalidValueException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class GameruleCommandType extends BukkitExceptionHandler implements ParameterType<BukkitCommandActor, GameRule> {

    public static final GameruleCommandType INSTANCE = new GameruleCommandType();

    private GameruleCommandType() {
        // Private constructor to prevent instantiation
    }

    @Override
    public GameRule<?> parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String gameruleName = input.readString();
        GameRule<?> gamerule = Registry.GAME_RULE.get(Key.key(Key.MINECRAFT_NAMESPACE, gameruleName));
        if (gamerule != null) {
            return gamerule;
        }

        throw new InvalidGameruleException(gameruleName);
    }

    @HandleException
    public void onInvalidGamerule(InvalidGameruleException e, BukkitCommandActor actor) {
        FancyWorldsPlugin.get().getTranslator()
                .translate("common.gamerule_not_found")
                .withPrefix()
                .replace("gameruleName", e.input())
                .send(actor.sender());
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return context -> Registry.GAME_RULE.stream()
                .map(gr -> gr.getKey().getKey())
                .toList();
    }

    public static class InvalidGameruleException extends InvalidValueException {
        public InvalidGameruleException(@NotNull String input) {
            super(input);
        }
    }
}
