package com.fancyinnovations.strata.mojang;

import com.fancyinnovations.strata.Strata;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class MojangService {

    private static final String MANIFEST_URL = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    private static final long CACHE_EXPIRATION_MS = 24 * 60 * 60 * 1000; // 24 hours

    private final Strata strata;
    private final HttpClient http;

    public MojangService(Strata strata) {
        this.strata = strata;
        this.http = HttpClient.newHttpClient();
    }

    public PistonVersionDetails getVersion(String version) {
        PistonVersionManifestV2 manifest = fetchPistonVersionManifest();
        if (manifest == null) {
            return null;
        }

        PistonVersionManifestV2.Version ver = manifest.getVersion(version);
        if (ver == null) {
            strata.getLogger().warn(
                    "Version not found in manifest",
                    StringProperty.of("version", version)
            );
            return null;
        }

        return fetchPistonVersionDetails(ver);
    }

    public PistonVersionDetails getLatestRelease() {
        PistonVersionManifestV2 manifest = fetchPistonVersionManifest();
        if (manifest == null) {
            return null;
        }

        PistonVersionManifestV2.Version version = manifest.getVersion(manifest.latest().release());
        if (version == null) {
            strata.getLogger().warn(
                    "Latest release version not found in manifest",
                    StringProperty.of("version", manifest.latest().release())
            );
            return null;
        }

        return fetchPistonVersionDetails(version);
    }

    public PistonVersionDetails getLatestSnapshot() {
        PistonVersionManifestV2 manifest = fetchPistonVersionManifest();
        if (manifest == null) {
            return null;
        }

        PistonVersionManifestV2.Version version = manifest.getVersion(manifest.latest().snapshot());
        if (version == null) {
            strata.getLogger().warn(
                    "Latest snapshot version not found in manifest",
                    StringProperty.of("version", manifest.latest().snapshot())
            );
            return null;
        }

        return fetchPistonVersionDetails(version);
    }

    /**
     * Downloads the server (bundle) jar for the given version details and returns the path to the downloaded file.
     * If the file already exists in the cache, it will return the cached file path instead.
     *
     * @param details The version details for which to download the server jar.
     */
    public void downloadServerBundle(PistonVersionDetails details) {
        String serverJarUrl = details.downloads().server().url();
        String versionId = details.id();

        Path bundlesPath = strata.getCacheDir().toPath().resolve("server-bundles");
        Path serverBundlePath = bundlesPath.resolve("server-bundle-" + versionId + ".jar");

        if (Files.exists(serverBundlePath)) {
            return;
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(serverJarUrl))
                .build();

        HttpResponse<byte[]> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            strata.getLogger().error(
                    "Failed to download server jar",
                    StringProperty.of("version", versionId),
                    StringProperty.of("url", serverJarUrl),
                    ThrowableProperty.of(e)
            );
            return;
        }

        try {
            Files.createDirectories(bundlesPath);
            Files.write(serverBundlePath, response.body());
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to save downloaded server jar to disk",
                    StringProperty.of("version", versionId),
                    StringProperty.of("path", serverBundlePath.toString()),
                    ThrowableProperty.of(e)
            );
        }

        strata.getLogger().info(
                "Downloaded server jar for version " + versionId,
                StringProperty.of("version", versionId),
                StringProperty.of("url", serverJarUrl),
                StringProperty.of("savedPath", serverBundlePath.toString())
        );
    }

    private PistonVersionManifestV2 fetchPistonVersionManifest() {
        // check cache first
        Path manifestCachePath = strata.getCacheDir().toPath().resolve("piston/version_manifest_v2.json");
        if (Files.exists(manifestCachePath)) {

            try {
                long lastModified = Files.getLastModifiedTime(manifestCachePath).toMillis();
                if (System.currentTimeMillis() - lastModified < CACHE_EXPIRATION_MS) { // cache is still valid
                    return Strata.GSON.fromJson(Files.readString(manifestCachePath), PistonVersionManifestV2.class);
                }
            } catch (IOException e) {
                strata.getLogger().error(
                        "Failed to read cached version manifest v2 from disk",
                        StringProperty.of("path", manifestCachePath.toString()),
                        ThrowableProperty.of(e)
                );
            }
        }

        // if cache is not valid or doesn't exist, fetch from Mojang

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(MANIFEST_URL))
                .build();

        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            strata.getLogger().error(
                    "Failed to fetch version manifest v2 from Mojang",
                    StringProperty.of("url", MANIFEST_URL),
                    ThrowableProperty.of(e)
            );
            return null;
        }

        PistonVersionManifestV2 manifest = Strata.GSON.fromJson(response.body(), PistonVersionManifestV2.class);

        try {
            Files.createDirectories(strata.getCacheDir().toPath().resolve("piston"));
            Files.writeString(strata.getCacheDir().toPath().resolve("piston/version_manifest_v2.json"), response.body());
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to cache version manifest v2 to disk",
                    StringProperty.of("path", strata.getCacheDir().toPath().resolve("piston/version_manifest_v2.json").toString()),
                    ThrowableProperty.of(e)
            );
        }

        strata.getLogger().info(
                "Fetched version manifest v2 from Mojang and cached to disk",
                StringProperty.of("url", MANIFEST_URL),
                StringProperty.of("versionCount", String.valueOf(manifest.versions().length))
        );

        return manifest;
    }

    private PistonVersionDetails fetchPistonVersionDetails(PistonVersionManifestV2.Version version) {
        // check cache first
        Path versionCachePath = strata.getCacheDir().toPath().resolve("piston/versions/" + version.id() + ".json");
        if (Files.exists(versionCachePath)) {
            try {
                long lastModified = Files.getLastModifiedTime(versionCachePath).toMillis();
                if (System.currentTimeMillis() - lastModified < CACHE_EXPIRATION_MS) { // cache is still valid
                    return Strata.GSON.fromJson(Files.readString(versionCachePath), PistonVersionDetails.class);
                }
            } catch (IOException e) {
                strata.getLogger().error(
                        "Failed to read cached version details from disk",
                        StringProperty.of("version", version.id()),
                        StringProperty.of("path", versionCachePath.toString()),
                        ThrowableProperty.of(e)
                );
            }
        }

        // if cache is not valid or doesn't exist, fetch from Mojang

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(version.url()))
                .build();

        HttpResponse<String> response;
        try {
            response = http.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            strata.getLogger().error(
                    "Failed to fetch version details from Mojang",
                    StringProperty.of("version", version.id()),
                    StringProperty.of("url", version.url()),
                    ThrowableProperty.of(e)
            );
            return null;
        }

        PistonVersionDetails details = Strata.GSON.fromJson(response.body(), PistonVersionDetails.class);

        try {
            Files.createDirectories(strata.getCacheDir().toPath().resolve("piston/versions"));
            Files.writeString(strata.getCacheDir().toPath().resolve("piston/versions/" + version.id() + ".json"), response.body());
        } catch (IOException e) {
            strata.getLogger().error(
                    "Failed to cache version details to disk",
                    StringProperty.of("version", version.id()),
                    StringProperty.of("path", strata.getCacheDir().toPath().resolve("piston/versions/" + version.id() + ".json").toString()),
                    ThrowableProperty.of(e)
            );
        }

        strata.getLogger().info(
                "Fetched version details for version " + version.id() + " from Mojang and cached to disk",
                StringProperty.of("version", version.id()),
                StringProperty.of("url", version.url())
        );

        return details;
    }
}
