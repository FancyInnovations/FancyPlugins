package com.fancyinnovations.fancyworlds.commands.fancyworlds;

import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import org.apache.maven.artifact.versioning.ComparableVersion;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class FWVersionCMD extends FancyContext {

    public static final FWVersionCMD INSTANCE = new FWVersionCMD();

    @Command("fancyworlds version")
    @Description("Shows the version of FancyWorlds")
    @CommandPermission("fancyworlds.commands.fancyworlds.version")
    public void version(
            final BukkitCommandActor actor
    ) {
        VersionFetcher versionFetcher = FancyWorldsPlugin.get().getVersionFetcher();
        VersionConfig versionConfig = FancyWorldsPlugin.get().getVersionConfig();

        ComparableVersion currentVersion = new ComparableVersion(versionConfig.getVersion());
        ComparableVersion newestVersion = versionFetcher.fetchNewestVersion();

        translator.translate("commands.fancyworlds.version.current_version")
                .withPrefix()
                .replace("version", versionConfig.getVersion())
                .send(actor.sender());

        if (newestVersion != null && currentVersion.compareTo(newestVersion) < 0) {
            translator.translate("commands.fancyworlds.version.version_outdated")
                    .withPrefix()
                    .replace("version", versionConfig.getVersion())
                    .replace("latestVersion", newestVersion.toString())
                    .replace("downloadURL", versionFetcher.getDownloadUrl())
                    .send(actor.sender());
        }
    }

}
