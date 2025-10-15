package de.oliver.deployment.hangar;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public record HangarVersionUpload(String version, Map<Platform, List<PluginDependency>> pluginDependencies,
                                  Map<Platform, List<String>> platformDependencies, String description,
                                  List<MultipartFileOrUrl> files, String channel) {

    public enum Platform {

        PAPER,
        WATERFALL,
        VELOCITY
    }

    public record PluginDependency(String name, boolean required, @Nullable String externalUrl) {

        /**
         * Creates a new PluginDependency with the given name, whether the dependency is required, and the namespace of the dependency.
         *
         * @param hangarProjectName name of the dependency, being its Hangar project id
         * @param required          whether the dependency is required
         * @return a new PluginDependency
         */
        public static PluginDependency createWithHangarNamespace(final String hangarProjectName, final boolean required) {
            return new PluginDependency(hangarProjectName, required, null);
        }

        /**
         * Creates a new PluginDependency with the given name, external url, and whether the dependency is required.
         *
         * @param name        name of the dependency
         * @param required    whether the dependency is required
         * @param externalUrl url to the dependency
         * @return a new PluginDependency
         */
        public static PluginDependency createWithUrl(final String name, final String externalUrl, final boolean required) {
            return new PluginDependency(name, required, externalUrl);
        }

    }

    /**
     * Represents a file that is either uploaded or downloaded from an external url.
     *
     * @param platforms   platforms the download is compatible with
     * @param externalUrl external url of the download, or null if the download is a file
     */
    public record MultipartFileOrUrl(List<Platform> platforms, @Nullable String externalUrl) {
    }
}