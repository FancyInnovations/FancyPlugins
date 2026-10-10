package de.oliver.quicke2e.steps.ops;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.oliver.quicke2e.config.Context;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;

public class OPsService {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final HttpClient client;

    public OPsService() {
        this.client = HttpClient.newHttpClient();
    }

    public void makePlayersOP(Context context) {
        OPConfig[] opConfig = new OPConfig[context.configuration().opPlayers().length];
        for (int i = 0; i < context.configuration().opPlayers().length; i++) {
            String username = context.configuration().opPlayers()[i];
            String uuid = getUUID(username);
            opConfig[i] = new OPConfig(
                    uuid,
                    username,
                    4,
                    true
            );
        }

        String json = GSON.toJson(opConfig);

        Path opsFilePath = context.serverEnvPath().resolve("ops.json");
        try {
            Files.writeString(opsFilePath, json);
        } catch (java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUUID(String username) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(java.net.URI.create(String.format("https://api.mojang.com/users/profiles/minecraft/%s", username)))
                .build();

        HttpResponse<String> resp = client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).join();
        MojangUuidResponse mojangUuidResponse = GSON.fromJson(resp.body(), MojangUuidResponse.class);

        return transformUUID(mojangUuidResponse.id());
    }

    private String transformUUID(String hexUUID) {
        return hexUUID.replaceFirst(
                "([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{12})",
                "$1-$2-$3-$4-$5"
        );
    }

}
