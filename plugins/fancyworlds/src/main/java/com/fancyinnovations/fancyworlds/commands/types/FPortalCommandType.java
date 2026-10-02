package com.fancyinnovations.fancyworlds.commands.types;

import com.fancyinnovations.fancyworlds.api.portals.FPortal;
import com.fancyinnovations.fancyworlds.api.portals.PortalService;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.BukkitExceptionHandler;
import revxrsal.commands.exception.InvalidValueException;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class FPortalCommandType extends BukkitExceptionHandler implements ParameterType<BukkitCommandActor, FPortal> {

    public static final FPortalCommandType INSTANCE = new FPortalCommandType();

    private FPortalCommandType() {
    }

    @Override
    public FPortal parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String portalName = input.readString();
        FPortal portal = PortalService.get().getPortalByName(portalName);
        if (portal != null) {
            return portal;
        }
        throw new InvalidFPortalException(portalName);
    }

    @HandleException
    public void onInvalidFPortal(InvalidFPortalException exception, BukkitCommandActor actor) {
        FancyWorldsPlugin.get().getTranslator()
                .translate("common.portal_not_found")
                .withPrefix()
                .replace("portalName", exception.input())
                .send(actor.sender());
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return context -> PortalService.get().getAllPortals().stream()
                .map(FPortal::getName)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    public static class InvalidFPortalException extends InvalidValueException {
        public InvalidFPortalException(@NotNull String input) {
            super(input);
        }
    }
}
