package com.fancyinnovations.fancyholograms.commands.lampCommands.fancyholograms;

import com.fancyinnovations.config.Config;
import com.fancyinnovations.config.ConfigField;
import com.fancyinnovations.fancyholograms.config.FHConfiguration;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancylib.translations.Translator;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Collection;
import java.util.Comparator;

public final class ConfigCMD {

    public static final ConfigCMD INSTANCE = new ConfigCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private ConfigCMD() {
    }

    @Command("fancyholograms config show")
    @Description("Shows all configuration options (and experimental feature flags) and their current value")
    @CommandPermission("fancyholograms.commands.fancyholograms.config.show")
    public void show(
            final BukkitCommandActor actor
    ) {
        Config config = plugin.getFHConfiguration().getConfig();
        Collection<ConfigField<?>> fields = config.getFields().values()
                .stream()
                .sorted(Comparator.comparing(ConfigField::path))
                .toList();

        translator.translate("commands.fancyholograms.config.show.settings_header")
                .withPrefix()
                .send(actor.sender());

        for (ConfigField<?> field : fields) {
            if (!field.path().startsWith("settings.")) {
                continue;
            }

            translator.translate("commands.fancyholograms.config.show.entry")
                    .replace("path", field.path().substring("settings.".length()))
                    .replace("value", config.get(field.path()).toString())
                    .replace("default", String.valueOf(field.defaultValue()))
                    .send(actor.sender());
        }

        actor.sender().sendMessage(" ");

        translator.translate("commands.fancyholograms.config.show.experimental_header")
                .withPrefix()
                .send(actor.sender());

        for (ConfigField<?> field : fields) {
            if (!field.path().startsWith("experimental_features.")) {
                continue;
            }

            translator.translate("commands.fancyholograms.config.show.entry")
                    .replace("path", field.path().substring("experimental_features.".length()))
                    .replace("value", config.get(field.path()).toString())
                    .replace("default", String.valueOf(field.defaultValue()))
                    .send(actor.sender());
        }
    }

    @Command("fancyholograms config reload")
    @Description("Reloads the configuration file and applies the changes")
    @CommandPermission("fancyholograms.commands.fancyholograms.config.reload")
    public void reload(
            final BukkitCommandActor actor
    ) {
        FHConfiguration config = plugin.getFHConfiguration();
        ExtendedFancyLogger logger = plugin.getFancyLogger();

        config.reload();
        logger.setCurrentLevel(LogLevel.valueOf(config.getLogLevel()));

        translator.translate("commands.fancyholograms.config.reload.success")
                .withPrefix()
                .send(actor.sender());
    }

}
