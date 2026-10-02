package de.oliver.fancynpcs.commands.lampCommands;

import de.oliver.fancylib.translations.Language;
import de.oliver.fancylib.translations.message.SimpleMessage;
import de.oliver.fancynpcs.FancyNpcs;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class FancyNpcsCMD extends FancyContext {

    public static final FancyNpcsCMD INSTANCE = new FancyNpcsCMD();

    private FancyNpcsCMD() {
    }

    @Command("fancynpcs version")
    @CommandPermission("fancynpcs.command.fancynpcs.version")
    public void onVersion(final BukkitCommandActor actor) {
        final CommandSender sender = actor.sender();
        plugin.getVersionConfig().checkVersionAndDisplay(sender, false);
    }

    @Command("fancynpcs reload")
    @CommandPermission("fancynpcs.command.fancynpcs.reload")
    public void onReload(final BukkitCommandActor actor) {
        final CommandSender sender = actor.sender();
        // Reloading all defined languages.
        translator.loadLanguages(plugin.getDataFolder().getAbsolutePath());
        // Reloading plugin configuration.
        plugin.getFancyNpcConfig().reload();
        // Getting the selected language from configuration. Defaults to fallback language.
        final Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageName().equals(plugin.getFancyNpcConfig().getLanguage()))
                .findFirst().orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);
        // Reloading all NPCs.
        // NOTE: This sometimes creates duplicated NPCs on the client-side.
        plugin.getNpcManagerImpl().reloadNpcs();
        // Sending success message to the sender.
        translator.translate("fancynpcs_reload_success").withPrefix().send(sender);
    }

    @Command("fancynpcs save")
    @CommandPermission("fancynpcs.command.fancynpcs.save")
    public void onSave(final BukkitCommandActor actor) {
        final CommandSender sender = actor.sender();
        plugin.getNpcManagerImpl().saveNpcs(true);
        translator.translate("fancynpcs_save_success").withPrefix().send(sender);
    }

    // NOTE: In the future, if there is more than a few feature flags, we might consider listing entries automatically by iterating, just like in 'list' sub-command.
    @Command("fancynpcs feature_flags")
    @CommandPermission("fancynpcs.command.fancynpcs.feature_flags")
    public void onFeatureFlags(final BukkitCommandActor actor) {
        final CommandSender sender = actor.sender();
        translator.translate("fancynpcs_feature_flags_header").send(sender);
        translator.translate("fancynpcs_feature_flags_entry")
                .replace("number", "1")
                .replace("name", "Player NPCs")
                .replace("id", FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.getName())
                .replace("state", getTranslatedState(FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled()))
                .send(sender);
        translator.translate("fancynpcs_feature_flags_entry")
                .replace("number", "2")
                .replace("name", "Native threads")
                .replace("id", FancyNpcs.USE_NATIVE_THREADS_FEATURE_FLAG.getName())
                .replace("state", getTranslatedState(FancyNpcs.USE_NATIVE_THREADS_FEATURE_FLAG.isEnabled()))
                .send(sender);
        translator.translate("fancynpcs_feature_flags_entry")
                .replace("number", "3")
                .replace("name", "Debug mode")
                .replace("id", FancyNpcs.ENABLE_DEBUG_MODE_FEATURE_FLAG.getName())
                .replace("state", getTranslatedState(FancyNpcs.ENABLE_DEBUG_MODE_FEATURE_FLAG.isEnabled()))
                .send(sender);
        translator.translate("fancynpcs_feature_flags_footer")
                .replace("count", "2")
                .replace("count_formatted", "· · 3")
                .replace("total", String.valueOf(plugin.getNpcManager().getAllNpcs().size()))
                .replace("total_formatted", "· · 3")
                .send(sender);
    }

    // NOTE: Might need to be improved later down the line, should get work done for now.
    private @NotNull String getTranslatedState(final boolean bool) {
        return (bool) ? ((SimpleMessage) translator.translate("enabled")).getMessage() : ((SimpleMessage) translator.translate("disabled")).getMessage();
    }
}
