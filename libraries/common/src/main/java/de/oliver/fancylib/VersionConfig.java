package de.oliver.fancylib;

import de.oliver.fancylib.versionFetcher.VersionFetcher;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class VersionConfig {

    private final Plugin plugin;
    private final VersionFetcher fetcher;
    private String version;
    private String commitHash;
    private String channel;
    private String platform;

    public VersionConfig(Plugin plugin, VersionFetcher fetcher) {
        this.plugin = plugin;
        this.fetcher = fetcher;
    }

    public void load() {
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(new FileUtils().readResource("version.yml"));
        } catch (InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        version = config.getString("version", "undefined");
        commitHash = config.getString("commit_hash", "undefined");
        channel = config.getString("channel", "undefined");
        platform = config.getString("platform", "undefined");
    }

    public boolean isDevelopmentBuild() {
        return !channel.equalsIgnoreCase("release");
    }

    public void checkVersionAndDisplay(CommandSender sender, boolean displayOnlyIfOutdated) {
        String newestVersion = usingLatestVersion();

        if (newestVersion == null) {
            MessageHelper.error(sender, "Could not fetch latest version");
            return;
        }

        if (!newestVersion.isEmpty()) {
            MessageHelper.warning(sender, outdatedVersion(newestVersion, fetcher.getDownloadUrl()));
            return;
        }

        if (!displayOnlyIfOutdated) {
            MessageHelper.success(sender, latestVersion());
        }
    }

    /**
     * @return null if could not fetch, empty string if it's the newest, the newest version if it's not the current
     */
    private String usingLatestVersion() {
        ComparableVersion newestVersion = fetcher.fetchNewestVersion();
        ComparableVersion currentVersion = new ComparableVersion(version);
        if (newestVersion == null) {
            return null;
        }

        if (newestVersion.compareTo(currentVersion) <= 0) {
            return "";
        }

        return newestVersion.toString();
    }

    private String latestVersion() {
        String result = "This server is using the latest version of {plugin}!\n" +
                "Version: {version} (Git: {hash})";

        result = result.replace("{plugin}", plugin.getName())
                .replace("{version}", version)
                .replace("{hash}", commitHash.substring(0, 7));

        return result;
    }

    private String outdatedVersion(String latestVersion, String downloadUrl) {
        String result = """
                This server is using an outdated version of {plugin}
                Current version: {current_ver}
                Latest version: {latest_ver}
                Download latest version: <click:open_url:'{download_url}'><u>click here</u></click>""";

        result = result.replace("{plugin}", plugin.getName())
                .replace("{current_ver}", version)
                .replace("{latest_ver}", latestVersion)
                .replace("{download_url}", downloadUrl);

        return result;
    }

    public String getVersion() {
        return version;
    }

    public String getCommitHash() {
        return commitHash;
    }

    public String getChannel() {
        return channel;
    }

    public String getPlatform() {
        return platform;
    }
}
