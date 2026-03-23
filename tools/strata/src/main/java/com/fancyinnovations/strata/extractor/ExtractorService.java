package com.fancyinnovations.strata.extractor;

import com.fancyinnovations.strata.Strata;
import com.fancyinnovations.strata.utils.ZipUtils;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExtractorService {

    private final Strata strata;

    public ExtractorService(Strata strata) {
        this.strata = strata;
    }


    public boolean extractServerBundle(String versionId) {
        Path bundlesPath = strata.getCacheDir().toPath().resolve("server-bundles");

        Path jarsPath = strata.getCacheDir().toPath().resolve("server-jars");
        try {
            Files.createDirectories(jarsPath);
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to create server jars directory",
                    StringProperty.of("versionId", versionId),
                    StringProperty.of("jarsPath", jarsPath.toString()),
                    ThrowableProperty.of(e)
            );
            return false;
        }

        Path librariesPath = strata.getCacheDir().toPath().resolve("server-libraries");
        try {
            Files.createDirectories(librariesPath);
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to create server libraries directory",
                    StringProperty.of("versionId", versionId),
                    StringProperty.of("librariesPath", librariesPath.toString()),
                    ThrowableProperty.of(e)
            );
            return false;
        }

        Path serverBundlePath = bundlesPath.resolve("server-bundle-" + versionId + ".jar");
        if (!serverBundlePath.toFile().exists()) {
            strata.getLogger().error(
                    "Server bundle for version " + versionId + " does not exist",
                    StringProperty.of("versionId", versionId),
                    StringProperty.of("expectedPath", serverBundlePath.toString())
            );
            return false;
        }

        Path serverJarPath = jarsPath.resolve("server-" + versionId + ".jar");
        Path librariesPathForVersion = librariesPath.resolve("libraries-" + versionId);

        if (Files.exists(serverJarPath) && Files.exists(librariesPathForVersion)) {
            strata.getLogger().info(
                    "Server jar and libraries for version " + versionId + " already extracted",
                    StringProperty.of("versionId", versionId)
            );
            return true;
        }

        Path tempExtractPath = strata.getCacheDir().toPath().resolve("temp-extract-" + versionId);
        try {
            Files.createDirectories(tempExtractPath);
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to create temporary extract directory",
                    StringProperty.of("versionId", versionId),
                    StringProperty.of("tempExtractPath", tempExtractPath.toString()),
                    ThrowableProperty.of(e)
            );
            return false;
        }

        try {
            ZipUtils.unzip(serverBundlePath.toString(), tempExtractPath.toString());
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to extract server bundle",
                    StringProperty.of("versionId", versionId),
                    StringProperty.of("serverBundlePath", serverBundlePath.toString()),
                    ThrowableProperty.of(e)
            );
            return false;
        }

        try {
            Files.move(tempExtractPath.resolve("META-INF/versions/" + versionId + "/server-" + versionId + ".jar"), serverJarPath);
            Files.move(tempExtractPath.resolve("META-INF/libraries"), librariesPathForVersion);
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to move extracted files",
                    StringProperty.of("versionId", versionId),
                    StringProperty.of("serverJarPath", serverJarPath.toString()),
                    StringProperty.of("librariesPath", librariesPathForVersion.toString()),
                    ThrowableProperty.of(e)
            );
            return false;
        }

        try {
            // recurseively delete temp extract directory
            Files.walk(tempExtractPath).sorted((a, b) -> b.compareTo(a)) // delete children before parents
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            strata.getLogger().warn(
                                    "Failed to delete temporary extract file",
                                    StringProperty.of("versionId", versionId),
                                    StringProperty.of("tempExtractPath", path.toString()),
                                    ThrowableProperty.of(e)
                            );
                        }
                    });
        } catch (IOException e) {
            strata.getLogger().warn(
                    "Failed to delete temporary extract directory",
                    StringProperty.of("versionId", versionId),
                    StringProperty.of("tempExtractPath", tempExtractPath.toString()),
                    ThrowableProperty.of(e)
            );
        }

        strata.getLogger().info(
                "Successfully extracted server bundle for version " + versionId,
                StringProperty.of("versionId", versionId),
                StringProperty.of("serverJarPath", serverJarPath.toString()),
                StringProperty.of("librariesPath", librariesPathForVersion.toString())
        );
        return true;
    }

    public String getServerJarPath(String versionId) {
        Path serverJarPath = strata.getCacheDir().toPath().resolve("server-jars").resolve("server-" + versionId + ".jar");
        if (serverJarPath.toFile().exists()) {
            return serverJarPath.toString();
        } else {
            return null;
        }
    }

    public String getLibrariesPath(String versionId) {
        Path librariesPath = strata.getCacheDir().toPath().resolve("server-libraries").resolve("libraries-" + versionId);
        if (librariesPath.toFile().exists()) {
            return librariesPath.toString();
        } else {
            return null;
        }
    }
}
