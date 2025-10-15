package de.oliver.deployment.hangar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.oliver.deployment.Configuration;
import de.oliver.deployment.git.GitService;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpMessage;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HangarService {

    private static final Gson GSON = new Gson();
    private static final String HANGAR_API_URL = "https://hangar.papermc.io/api/v1";

    private final String apiKey;
    private ActiveJWT activeJWT;

    public HangarService(String apiKey) {
        this.apiKey = apiKey;
    }

    public void deployPlugin(Configuration config) throws IOException {
        String changelog = Files.readString(Path.of(config.changeLogPath()));
        changelog = changelog.replaceAll("%COMMIT_HASH%", GitService.getCommitHash());
        changelog = changelog.replaceAll("%COMMIT_MESSAGE%", GitService.getCommitMessage());

        String version = config.readVersion();

        String pluginJarPath = config.pluginJarPath().replace("%VERSION%", version);
        File pluginFile = new File(pluginJarPath);

        HangarVersionUpload versionUpload = new HangarVersionUpload(
                version,
                config.channel(),
                changelog,
                Map.of(
                        "PAPER", Arrays.stream(config.supportedVersions()).toList()
                )
        );
        String versionUploadJson = GSON.toJson(versionUpload);

        // Create HTTP client
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            uploadVersion(client, config.projectName(), versionUpload, List.of(pluginFile.toPath()));
        }
    }

    public void uploadVersion(
            final HttpClient client,
            final String project,
            final HangarVersionUpload versionUpload,
            final List<Path> filePaths
    ) throws IOException {
        // The data needs to be sent as multipart form data
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart("versionUpload", new StringBody(GSON.toJson(versionUpload), ContentType.APPLICATION_JSON));

        // Attach files (one file for each platform where no external url is defined in the version upload data)
        for (final Path filePath : filePaths) {
            builder.addPart("files", new FileBody(filePath.toFile(), ContentType.DEFAULT_BINARY));
        }

        final HttpPost post = new HttpPost("%s/projects/%s/upload".formatted(HANGAR_API_URL, project));
        post.setEntity(builder.build());
        this.addAuthorizationHeader(client, post);

        final boolean success = client.execute(post, response -> {
            if (response.getCode() != 200) {
                System.out.println("Error uploading version {}: {}" + response.getReasonPhrase());
                return false;
            }
            return true;
        });
        if (!success) {
            throw new RuntimeException("Error uploading version");
        }
    }

    private synchronized void addAuthorizationHeader(final HttpClient client, final HttpMessage message) throws IOException {
        if (this.activeJWT != null && !this.activeJWT.hasExpired()) {
            // Add the active JWT
            message.addHeader("Authorization", this.activeJWT.jwt());
            return;
        }

        // Request a new JWT
        final ActiveJWT jwt = client.execute(new HttpPost("%s/authenticate?apiKey=%s".formatted(HANGAR_API_URL, this.apiKey)), response -> {
            if (response.getCode() == 400) {
                System.out.println("Bad JWT request; is the API key correct?");
                return null;
            } else if (response.getCode() != 200) {
                System.out.println("Error requesting JWT:" + response.getReasonPhrase());
                return null;
            }

            final String json = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
            final JsonObject object = GSON.fromJson(json, JsonObject.class);
            final String token = object.getAsJsonPrimitive("token").getAsString();
            final long expiresIn = object.getAsJsonPrimitive("expiresIn").getAsLong();
            return new ActiveJWT(token, System.currentTimeMillis() + expiresIn);
        });

        if (jwt == null) {
            throw new RuntimeException("Error getting JWT");
        }

        this.activeJWT = jwt;
        message.addHeader("Authorization", jwt.jwt());
    }

    /**
     * Represents an active JSON Web Token used for authentication with Hangar.
     *
     * @param jwt       Active JWT
     * @param expiresAt time in milliseconds when the JWT expires
     */
    private record ActiveJWT(String jwt, long expiresAt) {

        public boolean hasExpired() {
            // Make sure we request a new one before it expires
            return System.currentTimeMillis() < this.expiresAt + TimeUnit.SECONDS.toMillis(3);
        }
    }

}
