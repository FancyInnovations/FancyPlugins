package com.fancyinnovations.fancynpcsmodel.commands.fancynpcsmodel;

import com.fancyinnovations.fancynpcsmodel.main.FancyNpcsModelPlugin;
import com.fancyinnovations.fancynpcsmodel.utils.FancyContext;
import de.oliver.fancylib.VersionConfig;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

public class FNMVersionCMD extends FancyContext {

    public static final FNMVersionCMD INSTANCE = new FNMVersionCMD();

    private FNMVersionCMD() {
    }

    @Command("fancynpcsmodel version")
    @Permission("fancynpcsmodel.commands.fancynpcsmodel.version")
    public void version(
            CommandSender sender
    ) {
        VersionFetcher versionFetcher = FancyNpcsModelPlugin.get().getVersionFetcher();
        VersionConfig versionConfig = FancyNpcsModelPlugin.get().getVersionConfig();

        ComparableVersion currentVersion = new ComparableVersion(versionConfig.getVersion());
        ComparableVersion newestVersion = versionFetcher.fetchNewestVersion();

        translator.translate("commands.fancynpcsmodel.version.current_version")
                .withPrefix()
                .replace("version", versionConfig.getVersion())
                .send(sender);

        if (newestVersion != null && currentVersion.compareTo(newestVersion) < 0) {
            translator.translate("commands.fancynpcsmodel.version.version_outdated")
                    .withPrefix()
                    .replace("version", versionConfig.getVersion())
                    .replace("latestVersion", newestVersion.toString())
                    .replace("downloadURL", versionFetcher.getDownloadUrl())
                    .send(sender);
        }
    }

}
