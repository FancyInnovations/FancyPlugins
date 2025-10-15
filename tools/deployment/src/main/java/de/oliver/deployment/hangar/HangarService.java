package de.oliver.deployment.hangar;

import com.google.gson.Gson;
import de.oliver.deployment.Configuration;
import de.oliver.deployment.git.GitService;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

public class HangarService {

    private static final Gson GSON = new Gson();

    private final String apiKey;

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

            HttpPost post = new HttpPost("https://hangar.papermc.io/api/v1/projects/" + config.projectName() + "/upload");
            post.addHeader("Authorization", "Bearer " + apiKey);

            // Build multipart entity
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();

            // Add multiple files with the same field name "files"
            builder.addBinaryBody(
                    "files",
                    pluginFile,
                    ContentType.DEFAULT_BINARY,
                    pluginFile.getName()
            );

            // Add JSON field "versionUpload"
            builder.addTextBody(
                    "versionUpload",
                    versionUploadJson,
                    ContentType.APPLICATION_JSON
            );

            post.setEntity(builder.build());

            // Execute the request
            try (CloseableHttpResponse response = client.execute(post)) {
                System.out.println("Status: " + response.getCode());
                System.out.println("Response: " + EntityUtils.toString(response.getEntity()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}
