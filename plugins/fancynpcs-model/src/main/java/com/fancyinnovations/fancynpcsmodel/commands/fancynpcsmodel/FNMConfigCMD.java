package com.fancyinnovations.fancynpcsmodel.commands.fancynpcsmodel;

import com.fancyinnovations.config.ConfigField;
import com.fancyinnovations.fancynpcsmodel.utils.FancyContext;
import de.oliver.fancyanalytics.logger.LogLevel;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.Collection;
import java.util.Comparator;

public class FNMConfigCMD extends FancyContext {

    public static final FNMConfigCMD INSTANCE = new FNMConfigCMD();

    private FNMConfigCMD() {
    }

    @Command("fancynpcsmodel config reload")
    @Permission("fancynpcsmodel.commands.fancynpcsmodel.config.reload")
    public void configReload(
            CommandSender sender
    ) {
        config.reload();
        logger.setCurrentLevel(LogLevel.valueOf(config.getLogLevel()));
        plugin.registerTranslator();

        translator.translate("commands.fancynpcsmodel.config.reload.success")
                .withPrefix()
                .send(sender);
    }

    @Command("fancynpcsmodel config show")
    @Permission("fancynpcsmodel.commands.fancynpcsmodel.config.show")
    public void show(
            CommandSender sender
    ) {
        Collection<ConfigField<?>> fields = config.getConfig().getFields().values()
                .stream()
                .sorted(Comparator.comparing(ConfigField::path))
                .toList();

        translator.translate("commands.fancynpcsmodel.config.show.settings_header")
                .withPrefix()
                .send(sender);

        for (ConfigField<?> field : fields) {
            if (!field.path().startsWith("settings.")) {
                continue;
            }

            translator.translate("commands.fancynpcsmodel.config.show.entry")
                    .replace("path", field.path().substring("settings.".length()))
                    .replace("value", config.getConfig().get(field.path()).toString())
                    .replace("default", String.valueOf(field.defaultValue()))
                    .send(sender);
        }

        sender.sendMessage(" ");

        translator.translate("commands.fancynpcsmodel.config.show.experimental_header")
                .withPrefix()
                .send(sender);

        for (ConfigField<?> field : fields) {
            if (!field.path().startsWith("experimental_features.")) {
                continue;
            }

            translator.translate("commands.fancynpcsmodel.config.show.entry")
                    .replace("path", field.path().substring("experimental_features.".length()))
                    .replace("value", config.getConfig().get(field.path()).toString())
                    .replace("default", String.valueOf(field.defaultValue()))
                    .send(sender);
        }

    }

}
