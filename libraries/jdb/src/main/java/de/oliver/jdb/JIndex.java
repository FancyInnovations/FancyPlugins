package de.oliver.jdb;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public record JIndex(
        String name,
        @SerializedName("base_path") String basePath,
        @SerializedName("index_map") Map<String, String> indexMap // key -> original path
) {

    public static JIndex load(String name, String basePath) {
        File indexFile = new File(basePath, name + ".json");
        if (!indexFile.exists()) {
            return new JIndex(name, basePath, new ConcurrentHashMap<>());
        }

        try (var reader = Files.newBufferedReader(indexFile.toPath())) {
            return JDB.GSON.fromJson(reader, JIndex.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new JIndex(name, basePath, new ConcurrentHashMap<>());
        }
    }

    public void save() {
        File indexFile = new File(basePath, name + ".json");

        String json = JDB.GSON.toJson(this);
        try {
            Files.write(indexFile.toPath(), json.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
