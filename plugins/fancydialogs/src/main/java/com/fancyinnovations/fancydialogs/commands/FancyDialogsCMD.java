package com.fancyinnovations.fancydialogs.commands;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.dialog.DialogImpl;
import de.oliver.fancylib.translations.Translator;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Collection;
import java.util.List;

public final class FancyDialogsCMD {

    public static final FancyDialogsCMD INSTANCE = new FancyDialogsCMD();

    private final FancyDialogsPlugin plugin = FancyDialogsPlugin.get();
    private final Translator translator = FancyDialogsPlugin.get().getTranslator();

    private FancyDialogsCMD() {
    }

    @Command("fancydialogs version")
    @Description("Shows the version of FancyDialogs")
    @CommandPermission("fancydialogs.commands.tutorial")
    public void version(
            final BukkitCommandActor actor
    ) {
        String version = plugin.getPluginMeta().getVersion();

        translator.translate("commands.fancydialogs.version")
                .replace("version", version)
                .send(actor.sender());
    }

    @Command("fancydialogs storage save")
    @Description("Saves all dialog data to the storage")
    @CommandPermission("fancydialogs.commands.storage.save")
    public void storageSave(
            final BukkitCommandActor actor
    ) {
        List<DialogData> dialogs = plugin.getDialogRegistry().getAll().stream()
                .map(Dialog::getData)
                .toList();

        plugin.getDialogStorage().saveBatch(dialogs);

        translator.translate("commands.fancydialogs.storage.save.success")
                .replace("count", String.valueOf(dialogs.size()))
                .send(actor.sender());
    }

    @Command("fancydialogs storage load")
    @Description("Loads all dialog data from the storage")
    @CommandPermission("fancydialogs.commands.storage.load")
    public void storageLoad(
            final BukkitCommandActor actor
    ) {
        Collection<DialogData> dialogs = plugin.getDialogStorage().loadAll();

        for (DialogData dialogData : dialogs) {
            DialogImpl dialog = new DialogImpl(dialogData.id(), dialogData);
            plugin.getDialogRegistry().register(dialog);
        }

        translator.translate("commands.fancydialogs.storage.load.success")
                .replace("count", String.valueOf(dialogs.size()))
                .send(actor.sender());
    }

    @Command("fancydialogs storage reload")
    @Description("Clears the dialog registry and loads all dialog data from the storage")
    @CommandPermission("fancydialogs.commands.storage.load")
    public void storageReload(
            final BukkitCommandActor actor
    ) {
        plugin.getDialogRegistry().clear();

        Collection<DialogData> dialogs = plugin.getDialogStorage().loadAll();
        for (DialogData dialogData : dialogs) {
            DialogImpl dialog = new DialogImpl(dialogData.id(), dialogData);
            plugin.getDialogRegistry().register(dialog);
        }

        translator.translate("commands.fancydialogs.storage.reload.success")
                .replace("count", String.valueOf(dialogs.size()))
                .send(actor.sender());
    }

    @Command("fancydialogs registry list")
    @Description("Lists all registered dialogs")
    @CommandPermission("fancydialogs.commands.registry.list")
    public void registryList(
            final BukkitCommandActor actor
    ) {
        Collection<Dialog> dialogs = plugin.getDialogRegistry().getAll();
        if (dialogs.isEmpty()) {
            translator.translate("commands.dialog.registry.list.empty").send(actor.sender());
            return;
        }

        translator.translate("commands.dialog.registry.list.header")
                .replace("count", String.valueOf(dialogs.size()))
                .send(actor.sender());

        for (Dialog dialog : dialogs) {
            translator.translate("commands.dialog.registry.list.entry")
                    .replace("id", dialog.getId())
                    .replace("title", dialog.getData().title())
                    .send(actor.sender());
        }
    }

    @Command("fancydialogs registry clear")
    @Description("Clears the dialog registry")
    @CommandPermission("fancydialogs.commands.registry.clear")
    public void registryClear(
            final BukkitCommandActor actor
    ) {
        plugin.getDialogRegistry().clear();
        translator.translate("commands.fancydialogs.registry.clear.success").send(actor.sender());
    }

    @Command("fancydialogs registry unregister <dialog>")
    @Description("Unregisters a dialog by its ID")
    @CommandPermission("fancydialogs.commands.registry.unregister")
    public void registryUnregister(
            final BukkitCommandActor actor,
            final Dialog dialog
    ) {
        plugin.getDialogRegistry().unregister(dialog.getId());
        translator.translate("commands.fancydialogs.registry.unregister.success")
                .replace("id", dialog.getId())
                .send(actor.sender());
    }
}
