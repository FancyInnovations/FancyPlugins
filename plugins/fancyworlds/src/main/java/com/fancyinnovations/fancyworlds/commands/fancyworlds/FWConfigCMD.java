package com.fancyinnovations.fancyworlds.commands.fancyworlds;

import com.fancyinnovations.config.Config;
import com.fancyinnovations.config.ConfigField;
import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancyworlds.config.FancyWorldsConfigImpl;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancylib.translations.message.SimpleMessage;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Collection;
import java.util.Comparator;

public class FWConfigCMD extends FancyContext {

    public static final FWConfigCMD INSTANCE = new FWConfigCMD();

    @Command("fancyworlds config reload")
    @Description("Reloads the config of FancyWorlds.")
    @CommandPermission("fancyworlds.commands.fancyworlds.config.reload")
    public void configReload(
            final BukkitCommandActor actor
    ) {
        if (actor.isPlayer()) {
            SimpleMessage question = (SimpleMessage) translator.translate("commands.fancyworlds.config.reload.confirmation");
            new ConfirmationDialog(question.getMessage())
                    .withTitle("Confirm reload")
                    .withOnConfirm(() -> configReloadImpl(actor))
                    .withOnCancel(() -> translator.translate("commands.fancyworlds.config.reload.cancelled").withPrefix().send(actor.sender()))
                    .ask(actor.asPlayer());
        } else {
            configReloadImpl(actor);
        }
    }

    private void configReloadImpl(
            final BukkitCommandActor actor
    ) {
        config.reload();
        logger.setCurrentLevel(LogLevel.valueOf(config.getLogLevel()));
        plugin.registerTranslator();

        translator.translate("commands.fancyworlds.config.reload.success")
                .withPrefix()
                .send(actor.sender());
    }

    @Command("fancyworlds config show")
    @Description("Shows the current configuration")
    @CommandPermission("fancyworlds.commands.fancyworlds.config.show")
    public void show(
            final BukkitCommandActor actor
    ) {
        Config config = ((FancyWorldsConfigImpl) plugin.getFancyWorldsConfig()).getConfig();
        Collection<ConfigField<?>> fields = config.getFields().values()
                .stream()
                .sorted(Comparator.comparing(ConfigField::path))
                .toList();

        translator.translate("commands.fancyworlds.config.show.settings_header")
                .withPrefix()
                .send(actor.sender());

        for (ConfigField<?> field : fields) {
            if (!field.path().startsWith("settings.")) {
                continue;
            }

            translator.translate("commands.fancyworlds.config.show.entry")
                    .replace("path", field.path().substring("settings.".length()))
                    .replace("value", config.get(field.path()).toString())
                    .replace("default", String.valueOf(field.defaultValue()))
                    .send(actor.sender());
        }

        actor.sender().sendMessage(" ");

        translator.translate("commands.fancyworlds.config.show.experimental_header")
                .withPrefix()
                .send(actor.sender());

        for (ConfigField<?> field : fields) {
            if (!field.path().startsWith("experimental_features.")) {
                continue;
            }

            translator.translate("commands.fancyworlds.config.show.entry")
                    .replace("path", field.path().substring("experimental_features.".length()))
                    .replace("value", config.get(field.path()).toString())
                    .replace("default", String.valueOf(field.defaultValue()))
                    .send(actor.sender());
        }

    }

}
