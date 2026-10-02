package de.oliver.fancylib.versionFetcher;

import com.google.gson.Gson;
import org.apache.maven.artifact.versioning.ComparableVersion;

import java.util.Map;

public class FancySpacesVersionFetcher implements VersionFetcher {

    private final String pluginName;
    private final String channel;
    private ComparableVersion newestVersion;

    public FancySpacesVersionFetcher(String pluginName, String channel) {
        this.pluginName = pluginName;
        this.channel = channel;
        this.newestVersion = null;
    }

    public FancySpacesVersionFetcher(String pluginName) {
        this(pluginName, "release");
    }

    @Override
    public ComparableVersion fetchNewestVersion() {
        if (newestVersion != null) return newestVersion;

        String jsonString = VersionFetcher.getDataFromUrl("https://fancyspaces.net/api/v1/spaces/" + pluginName.toLowerCase() + "/versions/latest?channel=" + channel + "&platform=paper");
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }

        Gson gson = new Gson();
        try {
            Map<String, Object> version = gson.fromJson(jsonString, Map.class);

            String versionNumber = (String) version.get("name");
            newestVersion = new ComparableVersion(versionNumber);

            return newestVersion;

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getDownloadUrl() {
        return "https://fancyspaces.net/spaces/" + pluginName + "/versions";
    }
}
