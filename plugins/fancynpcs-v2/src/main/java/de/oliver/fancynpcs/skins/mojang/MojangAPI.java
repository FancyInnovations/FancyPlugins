package de.oliver.fancynpcs.skins.mojang;

import com.google.gson.Gson;
import de.oliver.fancyanalytics.logger.properties.StringProperty;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.skins.mineskin.RatelimitException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executor;

public class MojangAPI {

    private final HttpClient client;
    private final Gson gson = new Gson();

    public MojangAPI(Executor executor) {
        this.client = HttpClient.newBuilder()
                .connectTimeout(Duration.of(3, ChronoUnit.SECONDS))
                .executor(executor)
                .build();
    }

    public SkinData fetchSkin(String uuid, SkinData.SkinVariant variant) throws RatelimitException {
        if (uuid == null || uuid.isBlank()) {
            FancyNpcsPlugin.get().getFancyLogger().warn("Cannot fetch a skin from Mojang API without a UUID");
            return null;
        }

        FancyNpcsPlugin.get().getFancyLogger().debug("Fetching skin from MojangAPI for " + uuid);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false"))
                    .timeout(Duration.of(5, ChronoUnit.SECONDS))
                    .GET()
                    .build();

            HttpResponse<String> resp = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 429) {
                throw new RatelimitException(System.currentTimeMillis() + 1000 * 10); // retry in next run
            } else if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                FancyNpcsPlugin.get().getFancyLogger().warn("Failed to fetch skin from Mojang API for " + uuid + " (status code: " + resp.statusCode() + ")");
                FancyNpcsPlugin.get().getFancyLogger().debug("Body: " + resp.body());
                return null;
            }

            RequestResponse response = gson.fromJson(resp.body(), RequestResponse.class);
            if (response == null || response.properties() == null) {
                FancyNpcsPlugin.get().getFancyLogger().debug(
                        "Failed to parse response from Mojang API",
                        StringProperty.of("uuid", uuid),
                        StringProperty.of("response", resp.body())
                );
                return null;
            }

            RequestResponseProperty textures = response.getProperty("textures");
            if (textures == null || textures.value() == null || textures.signature() == null) {
                FancyNpcsPlugin.get().getFancyLogger().debug("No signed textures property found in Mojang API response for " + uuid);
                return null;
            }

            FancyNpcsPlugin.get().getFancyLogger().debug("Skin fetched from MojangAPI for " + uuid);
            return new SkinData(uuid, variant, textures.value(), textures.signature());
        } catch (RatelimitException e) {
            throw e; // rethrow
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            FancyNpcsPlugin.get().getFancyLogger().warn("Interrupted while fetching skin from Mojang API for " + uuid, ThrowableProperty.of(e));
            return null;
        } catch (Exception e) {
            FancyNpcsPlugin.get().getFancyLogger().warn("Failed to fetch skin from Mojang API for " + uuid, ThrowableProperty.of(e));
            return null;
        }
    }

    record RequestResponse(String id, String name, RequestResponseProperty[] properties) {
        public RequestResponseProperty getProperty(String name) {
            if (properties == null) {
                return null;
            }

            for (RequestResponseProperty property : properties) {
                if (property != null && property.name() != null && property.name().equals(name)) {
                    return property;
                }
            }
            return null;
        }
    }

    record RequestResponseProperty(String name, String value, String signature) {
    }

}
