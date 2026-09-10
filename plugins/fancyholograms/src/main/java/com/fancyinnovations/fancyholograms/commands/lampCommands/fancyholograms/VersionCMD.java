package com.fancyinnovations.fancyholograms.commands.lampCommands.fancyholograms;

import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import org.apache.maven.artifact.versioning.ComparableVersion;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class VersionCMD {

    public static final VersionCMD INSTANCE = new VersionCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private VersionCMD() {

    }

    @Command("fancyholograms version")
    @Description("Shows the version of FancyHologramsPlugin")
    @CommandPermission("fancyholograms.commands.fancyholograms.version")
    public void version(
            final BukkitCommandActor actor
    ) {
        VersionFetcher versionFetcher = FancyHologramsPlugin.get().getVersionFetcher();
        VersionConfig versionConfig = FancyHologramsPlugin.get().getVersionConfig();

        ComparableVersion currentVersion = new ComparableVersion(versionConfig.getVersion());
        ComparableVersion newestVersion = versionFetcher.fetchNewestVersion();

        translator.translate("commands.fancyholograms.version.current_version")
                .withPrefix()
                .replace("version", versionConfig.getVersion())
                .send(actor.sender());

        if (newestVersion != null && currentVersion.compareTo(newestVersion) < 0) {
            translator.translate("commands.fancyholograms.version.version_outdated")
                    .withPrefix()
                    .replace("version", versionConfig.getVersion())
                    .replace("latestVersion", newestVersion.toString())
                    .replace("downloadURL", versionFetcher.getDownloadUrl())
                    .send(actor.sender());
        }
    }

}
