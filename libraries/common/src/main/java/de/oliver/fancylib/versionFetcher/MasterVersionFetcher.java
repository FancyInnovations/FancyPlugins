package de.oliver.fancylib.versionFetcher;

import org.apache.maven.artifact.versioning.ComparableVersion;

import java.util.LinkedList;

public class MasterVersionFetcher implements VersionFetcher {

    private final String pluginName;
    private ComparableVersion newestVersion;
    private LinkedList<VersionFetcher> fetchers;

    public MasterVersionFetcher(String pluginName) {
        this.pluginName = pluginName;
        this.fetchers = new LinkedList<>();
        fetchers.push(new ReposiliteVersionFetcher(pluginName));
        fetchers.push(new HangarVersionFetcher(pluginName));
        fetchers.push(new FancySpacesVersionFetcher(pluginName));
        fetchers.push(new ModrinthVersionFetcher(pluginName));
    }

    @Override
    public ComparableVersion fetchNewestVersion() {
        for (VersionFetcher fetcher : fetchers) {
            ComparableVersion version = fetcher.fetchNewestVersion();
            if (version == null) continue;
            newestVersion = version;
            return newestVersion;
        }
        return null;
    }

    @Override
    public String getDownloadUrl() {
        return "https://modrinth.com/plugin/" + pluginName;
    }
}
