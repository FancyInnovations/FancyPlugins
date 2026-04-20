package com.fancyinnovations.fancynpcsmodel.commands.fancynpcsmodel;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import com.fancyinnovations.fancynpcsmodel.utils.FancyContext;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import org.apache.maven.artifact.versioning.ComparableVersion;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class FNMVersionCMD extends FancyContext {

    public static final FNMVersionCMD INSTANCE = new FNMVersionCMD();

    @Command("fancynpcsmodel version")
    @Description("Shows the version of FancyNpcsModel and checks for updates.")
    @CommandPermission("fancynpcsmodel.commands.fancynpcsmodel.version")
    public void version(
            final BukkitCommandActor actor
    ) {
        VersionFetcher versionFetcher = FancyNpcsModelPlugin.get().getVersionFetcher();
        VersionConfig versionConfig = FancyNpcsModelPlugin.get().getVersionConfig();

        ComparableVersion currentVersion = new ComparableVersion(versionConfig.getVersion());
        ComparableVersion newestVersion = versionFetcher.fetchNewestVersion();

        translator.translate("commands.fancynpcsmodel.version.current_version")
                .withPrefix()
                .replace("version", versionConfig.getVersion())
                .send(actor.sender());

        if (newestVersion != null && currentVersion.compareTo(newestVersion) < 0) {
            translator.translate("commands.fancynpcsmodel.version.version_outdated")
                    .withPrefix()
                    .replace("version", versionConfig.getVersion())
                    .replace("latestVersion", newestVersion.toString())
                    .replace("downloadURL", versionFetcher.getDownloadUrl())
                    .send(actor.sender());
        }
    }

}
