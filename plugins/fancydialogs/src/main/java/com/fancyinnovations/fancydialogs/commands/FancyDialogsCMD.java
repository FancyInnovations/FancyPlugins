package com.fancyinnovations.fancydialogs.commands;

import com.fancyinnovations.fancydialogs.FancyDialogsPlugin;
import com.fancyinnovations.fancydialogs.api.Dialog;
import com.fancyinnovations.fancydialogs.api.data.DialogData;
import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancydialogs.config.FancyDialogsConfig;
import com.fancyinnovations.fancydialogs.dialog.DialogImpl;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancylib.translations.Language;
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
    @CommandPermission("fancydialogs.commands.fancydialogs.version")
    public void version(
            final BukkitCommandActor actor
    ) {
        String version = plugin.getPluginMeta().getVersion();

        translator.translate("commands.fancydialogs.version")
                .withPrefix()
                .replace("version", version)
                .send(actor.sender());
    }

    @Command("fancydialogs config reload")
    @Description("Reloads the FancyDialogs configuration file")
    @CommandPermission("fancydialogs.commands.fancydialogs.config.reload")
    public void configReload(
            final BukkitCommandActor actor
    ) {
        if (actor.isPlayer()) {
            new ConfirmationDialog("Are you sure you want to reload the configuration? This will reset all changes made to the config file.")
                    .withTitle("Confirm reload")
                    .withOnConfirm(() -> reloadConfig(actor))
                    .withOnCancel(() -> translator.translate("commands.fancydialogs.config.reload.cancelled").withPrefix().send(actor.sender()))
                    .ask(actor.asPlayer());
        } else {
            reloadConfig(actor);
        }
    }

    private void reloadConfig(BukkitCommandActor actor) {
        FancyDialogsConfig config = plugin.getFancyDialogsConfig();
        config.load();

        plugin.getFancyLogger().setCurrentLevel(LogLevel.valueOf(config.getLogLevel()));

        translator.loadLanguages(plugin.getDataFolder().getAbsolutePath());
        final Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageName().equals(config.getLanguage()))
                .findFirst().orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);

        translator.translate("commands.fancydialogs.config.reload.success")
                .withPrefix()
                .send(actor.sender());
    }

    @Command("fancydialogs storage save")
    @Description("Saves all dialog data to the storage")
    @CommandPermission("fancydialogs.commands.fancydialogs.storage.save")
    public void storageSave(
            final BukkitCommandActor actor
    ) {
        List<DialogData> dialogs = plugin.getDialogRegistry().getAll().stream()
                .map(Dialog::getData)
                .toList();

        plugin.getDialogStorage().saveBatch(dialogs);

        translator.translate("commands.fancydialogs.storage.save.success")
                .withPrefix()
                .replace("count", String.valueOf(dialogs.size()))
                .send(actor.sender());
    }

    @Command("fancydialogs storage load")
    @Description("Loads all dialog data from the storage")
    @CommandPermission("fancydialogs.commands.fancydialogs.storage.load")
    public void storageLoad(
            final BukkitCommandActor actor
    ) {
        if (actor.isPlayer()) {
            new ConfirmationDialog("Are you sure you want to load all dialog data from the storage? This will overwrite any existing dialogs.")
                    .withTitle("Confirm load")
                    .withOnConfirm(() -> loadStorage(actor))
                    .withOnCancel(() -> translator.translate("commands.fancydialogs.storage.load.cancelled").withPrefix().send(actor.sender()))
                    .ask(actor.asPlayer());
        } else {
            loadStorage(actor);
        }
    }

    private void loadStorage(BukkitCommandActor actor) {
        Collection<DialogData> dialogs = plugin.getDialogStorage().loadAll();

        for (DialogData dialogData : dialogs) {
            DialogImpl dialog = new DialogImpl(dialogData.id(), dialogData);
            plugin.getDialogRegistry().register(dialog);
        }

        translator.translate("commands.fancydialogs.storage.load.success")
                .withPrefix()
                .replace("count", String.valueOf(dialogs.size()))
                .send(actor.sender());
    }

    @Command("fancydialogs storage reload")
    @Description("Clears the dialog registry and loads all dialog data from the storage")
    @CommandPermission("fancydialogs.commands.fancydialogs.storage.reload")
    public void storageReload(
            final BukkitCommandActor actor
    ) {
        if (actor.isPlayer()) {
            new ConfirmationDialog("Are you sure you want to reload all dialog data from the storage? This will clear the dialog registry and overwrite any existing dialogs.")
                    .withTitle("Confirm reload")
                    .withOnConfirm(() -> reloadStorage(actor))
                    .withOnCancel(() -> translator.translate("commands.fancydialogs.storage.reload.cancelled").withPrefix().send(actor.sender()))
                    .ask(actor.asPlayer());
        } else {
            reloadStorage(actor);
        }
    }

    private void reloadStorage(BukkitCommandActor actor) {
        plugin.getDialogRegistry().clear();

        Collection<DialogData> dialogs = plugin.getDialogStorage().loadAll();
        for (DialogData dialogData : dialogs) {
            DialogImpl dialog = new DialogImpl(dialogData.id(), dialogData);
            plugin.getDialogRegistry().register(dialog);
        }

        translator.translate("commands.fancydialogs.storage.reload.success")
                .withPrefix()
                .replace("count", String.valueOf(dialogs.size()))
                .send(actor.sender());
    }

    @Command("fancydialogs registry list")
    @Description("Lists all registered dialogs")
    @CommandPermission("fancydialogs.commands.fancydialogs.registry.list")
    public void registryList(
            final BukkitCommandActor actor
    ) {
        Collection<Dialog> dialogs = plugin.getDialogRegistry().getAll();
        if (dialogs.isEmpty()) {
            translator.translate("commands.dialog.list.empty")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.dialog.list.header")
                .withPrefix()
                .replace("count", String.valueOf(dialogs.size()))
                .send(actor.sender());

        for (Dialog dialog : dialogs) {
            translator.translate("commands.dialog.list.entry")
                    .replace("id", dialog.getId())
                    .replace("title", dialog.getData().title())
                    .send(actor.sender());
        }
    }

    @Command("fancydialogs registry clear")
    @Description("Clears the dialog registry")
    @CommandPermission("fancydialogs.commands.fancydialogs.registry.clear")
    public void registryClear(
            final BukkitCommandActor actor
    ) {
        if (actor.isPlayer()) {
            new ConfirmationDialog("Are you sure you want to clear the dialog registry? This will remove all registered dialogs.")
                    .withTitle("Confirm clear")
                    .withOnConfirm(() -> clearRegistry(actor))
                    .withOnCancel(() -> translator.translate("commands.fancydialogs.registry.clear.cancelled").withPrefix().send(actor.sender()))
                    .ask(actor.asPlayer());
        } else {
            clearRegistry(actor);
        }
    }

    private void clearRegistry(BukkitCommandActor actor) {
        plugin.getDialogRegistry().clear();
        translator.translate("commands.fancydialogs.registry.clear.success")
                .withPrefix()
                .send(actor.sender());
    }

    @Command("fancydialogs registry unregister <dialog>")
    @Description("Unregisters a dialog by its ID")
    @CommandPermission("fancydialogs.commands.fancydialogs.registry.unregister")
    public void registryUnregister(
            final BukkitCommandActor actor,
            final Dialog dialog
    ) {
        if (actor.isPlayer()) {
            new ConfirmationDialog("Are you sure you want to unregister the dialog with ID '" + dialog.getId() + "'? This will remove it from the registry.")
                    .withTitle("Confirm unregister")
                    .withOnConfirm(() -> unregisterDialog(actor, dialog))
                    .withOnCancel(() -> translator.translate("commands.fancydialogs.registry.unregister.cancelled").withPrefix().send(actor.sender()))
                    .ask(actor.asPlayer());
        } else {
            unregisterDialog(actor, dialog);
        }
    }

    private void unregisterDialog(
            final BukkitCommandActor actor,
            final Dialog dialog
    ) {
        plugin.getDialogRegistry().unregister(dialog.getId());
        translator.translate("commands.fancydialogs.registry.unregister.success")
                .withPrefix()
                .replace("id", dialog.getId())
                .send(actor.sender());
    }

    @Command("fancydialogs joined_players_cache clear")
    @Description("Clears the joined players cache")
    @CommandPermission("fancydialogs.commands.fancydialogs.joined_players_cache.clear")
    public void joinedPlayersCacheClear(
            final BukkitCommandActor actor
    ) {
        if (actor.isPlayer()) {
            new ConfirmationDialog("Are you sure you want to clear the joined players cache?")
                    .withTitle("Confirm clear")
                    .withOnConfirm(() -> clearJoinedPlayersCache(actor))
                    .withOnCancel(() -> translator.translate("commands.fancydialogs.joined_players_cache.clear.cancelled").withPrefix().send(actor.sender()))
                    .ask(actor.asPlayer());
        } else {
            clearJoinedPlayersCache(actor);
        }
    }

    private void clearJoinedPlayersCache(
            final BukkitCommandActor actor
    ) {
        plugin.getJoinedPlayersCache().clear();
        translator.translate("commands.fancydialogs.joined_players_cache.clear.success")
                .withPrefix()
                .send(actor.sender());
    }
}
