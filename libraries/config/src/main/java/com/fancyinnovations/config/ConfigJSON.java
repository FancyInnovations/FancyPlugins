package com.fancyinnovations.config;

import com.google.gson.*;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConfigJSON {

    private final ExtendedFancyLogger logger;
    private final File configFile;
    private final Map<String, ConfigField<?>> fields;
    private final Map<String, Object> values;
    private final Gson gson;

    public ConfigJSON(ExtendedFancyLogger logger, String configFilePath) {
        this.logger = logger;
        this.configFile = new File(configFilePath);
        this.fields = new ConcurrentHashMap<>();
        this.values = new ConcurrentHashMap<>();
        this.gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .create();
    }

    public void addField(ConfigField<?> field) {
        fields.put(field.path(), field);
    }

    public Map<String, ConfigField<?>> getFields() {
        return fields;
    }

    public <T> T get(String path) {
        ConfigField<?> field = fields.get(path);
        if (field == null) {
            return null;
        }

        if (field.forceDefault()) {
            return (T) field.defaultValue();
        }

        Object value = values.computeIfAbsent(path, k -> field.defaultValue());
        return (T) field.type().cast(value);
    }

    public void reload() {
        if (!configFile.exists()) {
            try {
                File parent = configFile.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                if (!configFile.createNewFile()) {
                    logger.error("Failed to create config file: " + configFile.getAbsolutePath());
                    return;
                }
            } catch (IOException e) {
                logger.error("Error creating config file: " + configFile.getAbsolutePath(), ThrowableProperty.of(e));
                return;
            }

            JsonObject root = new JsonObject();
            for (ConfigField<?> field : fields.values()) {
                setDefault(root, field);
            }

            saveJson(root);
            return;
        }

        JsonObject root;
        try (FileReader reader = new FileReader(configFile)) {
            JsonElement parsed = JsonParser.parseReader(reader);
            if (parsed == null || !parsed.isJsonObject()) {
                root = new JsonObject();
            } else {
                root = parsed.getAsJsonObject();
            }
        } catch (Exception e) {
            logger.error("Error reading config file: " + configFile.getAbsolutePath(), ThrowableProperty.of(e));
            root = new JsonObject();
        }

        boolean dirty = false;

        for (Map.Entry<String, ConfigField<?>> entry : fields.entrySet()) {
            String path = entry.getKey();
            ConfigField<?> field = entry.getValue();

            if (field.forRemoval()) {
                if (isSet(root, path)) {
                    logger.debug("Removing path '" + path + "' from config");
                    removePath(root, path);
                    dirty = true;
                }
                continue;
            }

            JsonElement elem = getElement(root, path);
            if (elem != null && !elem.isJsonNull()) {
                try {
                    Object deserialized = gson.fromJson(elem, (Type) field.type());
                    if (deserialized != null && field.type().isInstance(deserialized)) {
                        values.put(path, deserialized);
                    } else {
                        // Attempt numeric conversions: gson may deserialize numbers as Double
                        Object converted = tryConvertNumber(deserialized, field.type());
                        if (converted != null) {
                            values.put(path, converted);
                        } else {
                            logger.warn("Value for path '" + path + "' is not of type '" + field.type().getSimpleName() + "'");
                            setDefault(root, field);
                            dirty = true;
                        }
                    }
                } catch (JsonSyntaxException | ClassCastException ex) {
                    logger.warn("Failed to parse value for path '" + path + "': " + ex.getMessage());
                    setDefault(root, field);
                    dirty = true;
                }
            } else {
                logger.debug("Path '" + path + "' not found in config");
                setDefault(root, field);
                dirty = true;
            }
        }

        if (dirty) {
            saveJson(root);
        }
    }

    private void setDefault(JsonObject root, ConfigField<?> field) {
        logger.debug("Setting default value for path '" + field.path() + "': " + field.defaultValue());
        JsonElement elem = gson.toJsonTree(field.defaultValue(), (Type) field.type());
        setElement(root, field.path(), elem);
        // JSON does not support inline comments; descriptions are not stored.
    }

    private void saveJson(JsonObject root) {
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(root, writer);
        } catch (IOException e) {
            logger.error("Error saving config file: " + configFile.getAbsolutePath(), ThrowableProperty.of(e));
        }
    }

    /**
     * Utility: get JsonElement at dot-separated path, or null if absent
     */
    private JsonElement getElement(JsonObject root, String path) {
        String[] parts = path.split("\\.");
        JsonElement current = root;
        for (String p : parts) {
            if (!current.isJsonObject()) return null;
            JsonObject obj = current.getAsJsonObject();
            if (!obj.has(p)) return null;
            current = obj.get(p);
        }
        return current;
    }

    /**
     * Utility: set JsonElement at dot-separated path, creating intermediate objects
     */
    private void setElement(JsonObject root, String path, JsonElement value) {
        String[] parts = path.split("\\.");
        JsonObject current = root;
        for (int i = 0; i < parts.length - 1; i++) {
            String p = parts[i];
            if (!current.has(p) || !current.get(p).isJsonObject()) {
                JsonObject child = new JsonObject();
                current.add(p, child);
                current = child;
            } else {
                current = current.getAsJsonObject(p);
            }
        }
        current.add(parts[parts.length - 1], value);
    }

    private boolean isSet(JsonObject root, String path) {
        return getElement(root, path) != null;
    }

    /**
     * Remove a path (dot-separated) from the JSON object
     */
    private void removePath(JsonObject root, String path) {
        String[] parts = path.split("\\.");
        JsonObject current = root;
        for (int i = 0; i < parts.length - 1; i++) {
            String p = parts[i];
            if (!current.has(p) || !current.get(p).isJsonObject()) {
                return;
            }
            current = current.getAsJsonObject(p);
        }
        current.remove(parts[parts.length - 1]);
    }

    /**
     * Try to convert numeric values (e.g., Double) to the requested numeric target type
     */
    private Object tryConvertNumber(Object value, Class<?> target) {
        if (!(value instanceof Number)) return null;
        Number num = (Number) value;
        if (target == Integer.class || target == int.class) {
            return num.intValue();
        } else if (target == Long.class || target == long.class) {
            return num.longValue();
        } else if (target == Double.class || target == double.class) {
            return num.doubleValue();
        } else if (target == Float.class || target == float.class) {
            return num.floatValue();
        } else if (target == Short.class || target == short.class) {
            return num.shortValue();
        } else if (target == Byte.class || target == byte.class) {
            return num.byteValue();
        }
        return null;
    }
}