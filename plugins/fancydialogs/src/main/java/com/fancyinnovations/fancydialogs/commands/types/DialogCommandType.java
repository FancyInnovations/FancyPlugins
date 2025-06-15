package com.fancyinnovations.fancydialogs.commands.types;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.registry.DialogRegistry;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

public class DialogCommandType implements ParameterType<BukkitCommandActor, Dialog> {

    public static final DialogCommandType INSTANCE = new DialogCommandType();
    private static final DialogRegistry REGISTRY = FancyDialogsPlugin.get().getDialogRegistry();

    private DialogCommandType() {
        // Private constructor to prevent instantiation
    }

    @Override
    public Dialog parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<@NotNull BukkitCommandActor> context) {
        String id = input.readString();

        Dialog dialog = REGISTRY.get(id);
        if (dialog != null) {
            return dialog;
        }

        FancyDialogsPlugin.get().getTranslator()
                .translate("dialog.not_found")
                .replace("id", id)
                .send(context.actor().sender());

        return null;
    }

    @Override
    public @NotNull SuggestionProvider<@NotNull BukkitCommandActor> defaultSuggestions() {
        return (ctx) -> REGISTRY.getAll().stream()
                .map(Dialog::getId)
                .toList();
    }
}
