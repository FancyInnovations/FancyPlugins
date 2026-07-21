package de.oliver.fancylib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


/*
    From: https://gist.github.com/Jofkos/d0c469528b032d820f42
 */

public class UUIDFetcher {

    private static final ExtendedFancyLogger LOGGER = new ExtendedFancyLogger("UUIDFetcher");
    private static final String UUID_URL = "https://api.minecraftservices.com/minecraft/profile/lookup/name/%s";
    private static final String NAME_URL = "https://api.minecraftservices.com/minecraft/profile/lookup/%s";
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
    private static final ConcurrentMap<String, UUID> uuidCache = new ConcurrentHashMap<>();
    private static final ConcurrentMap<UUID, String> nameCache = new ConcurrentHashMap<>();
    private static final ExecutorService pool = Executors.newFixedThreadPool(4, runnable -> {
        Thread thread = new Thread(runnable, "FancyLib-UUIDFetcher");
        thread.setDaemon(true);
        return thread;
    });

    private String name;
    private UUID id;

    /**
     * Fetches the uuid asynchronously and passes it to the consumer
     *
     * @param name   The name
     * @param action Do what you want to do with the uuid her
     */
    public static void getUUID(String name, Consumer<UUID> action) {
        pool.execute(() -> action.accept(getUUID(name)));
    }


    /**
     * Fetches the uuid synchronously and returns it
     *
     * @param name The name
     */
    public static UUID getUUID(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        name = name.toLowerCase(Locale.ROOT);
        UUID cachedUuid = uuidCache.get(name);
        if (cachedUuid != null) {
            return cachedUuid;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name)).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            UUIDFetcher data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);
            if (data == null || data.id == null || data.name == null) {
                LOGGER.warn("UUID lookup returned an incomplete response for name: " + name);
                return null;
            }

            uuidCache.put(name, data.id);
            nameCache.put(data.id, data.name);

            return data.id;
        } catch (Exception e) {
            LOGGER.error("Could not fetch UUID for name: " + name, ThrowableProperty.of(e));
            return null;
        }
    }

    /**
     * Fetches the name asynchronously and passes it to the consumer
     *
     * @param uuid   The uuid
     * @param action Do what you want to do with the name her
     */
    public static void getName(UUID uuid, Consumer<String> action) {
        pool.execute(() -> action.accept(getName(uuid)));
    }

    /**
     * Fetches the name synchronously and returns it
     *
     * @param uuid The uuid
     * @return The name
     */
    public static String getName(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        String cachedName = nameCache.get(uuid);
        if (cachedName != null) {
            return cachedName;
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            UUIDFetcher currentNameData = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);
            if (currentNameData == null || currentNameData.name == null) {
                LOGGER.warn("Name lookup returned an incomplete response for UUID: " + uuid);
                return null;
            }

            uuidCache.put(currentNameData.name.toLowerCase(Locale.ROOT), uuid);
            nameCache.put(uuid, currentNameData.name);

            return currentNameData.name;
        } catch (Exception e) {
            LOGGER.error("Could not fetch name for uuid: " + uuid, ThrowableProperty.of(e));
            return null;
        }
    }

}

class UUIDTypeAdapter extends TypeAdapter<UUID> {
    public static String fromUUID(UUID value) {
        return value.toString().replace("-", "");
    }

    public static UUID fromString(String input) {
        return UUID.fromString(input.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }

    public void write(JsonWriter out, UUID value) throws IOException {
        out.value(fromUUID(value));
    }

    public UUID read(JsonReader in) throws IOException {
        return fromString(in.nextString());
    }
}
