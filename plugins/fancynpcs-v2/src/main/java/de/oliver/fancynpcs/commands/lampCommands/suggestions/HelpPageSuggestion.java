package de.oliver.fancynpcs.commands.lampCommands.suggestions;

import de.oliver.fancylib.translations.message.MultiMessage;
import de.oliver.fancynpcs.FancyNpcs;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.util.ArrayList;
import java.util.Collection;

public final class HelpPageSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        MultiMessage contents = (MultiMessage) FancyNpcs.getInstance().getTranslator().translate("npc_help_contents");
        int pages = contents.getRawMessages().size() / 6 + 1;
        ArrayList<String> result = new ArrayList<>();
        for (int i = 1; i <= pages; i++) result.add(String.valueOf(i));
        return result;
    }
}
