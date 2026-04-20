package com.fancyinnovations.fancynpcsmodel.commands.fancynpcsmodel;

import com.fancyinnovations.config.ConfigField;
import com.fancyinnovations.fancynpcsmodel.utils.FancyContext;
import de.oliver.fancyanalytics.logger.LogLevel;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Collection;
import java.util.Comparator;

public class FNMConfigCMD extends FancyContext {

    public static final FNMConfigCMD INSTANCE = new FNMConfigCMD();

    @Command("fancynpcsmodel config reload")
    @Description("Reloads the config of FancyNpcsModel.")
    @CommandPermission("fancynpcsmodel.commands.fancynpcsmodel.config.reload")
    public void configReload(
            final BukkitCommandActor actor
    ) {
        config.reload();
        logger.setCurrentLevel(LogLevel.valueOf(config.getLogLevel()));
        plugin.registerTranslator();

        translator.translate("commands.fancynpcsmodel.config.reload.success")
                .withPrefix()
                .send(actor.sender());
    }

    @Command("fancynpcsmodel config show")
    @Description("Shows the current configuration")
    @CommandPermission("fancynpcsmodel.commands.fancynpcsmodel.config.show")
    public void show(
            final BukkitCommandActor actor
    ) {
        Collection<ConfigField<?>> fields = config.getConfig().getFields().values()
                .stream()
                .sorted(Comparator.comparing(ConfigField::path))
                .toList();

        translator.translate("commands.fancynpcsmodel.config.show.settings_header")
                .withPrefix()
                .send(actor.sender());

        for (ConfigField<?> field : fields) {
            if (!field.path().startsWith("settings.")) {
                continue;
            }

            translator.translate("commands.fancynpcsmodel.config.show.entry")
                    .replace("path", field.path().substring("settings.".length()))
                    .replace("value", config.getConfig().get(field.path()).toString())
                    .replace("default", String.valueOf(field.defaultValue()))
                    .send(actor.sender());
        }

        actor.sender().sendMessage(" ");

        translator.translate("commands.fancynpcsmodel.config.show.experimental_header")
                .withPrefix()
                .send(actor.sender());

        for (ConfigField<?> field : fields) {
            if (!field.path().startsWith("experimental_features.")) {
                continue;
            }

            translator.translate("commands.fancynpcsmodel.config.show.entry")
                    .replace("path", field.path().substring("experimental_features.".length()))
                    .replace("value", config.getConfig().get(field.path()).toString())
                    .replace("default", String.valueOf(field.defaultValue()))
                    .send(actor.sender());
        }

    }

}
