package de.oliver.fancyvisuals.utils;

import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.api.Context;
import de.oliver.jdb.JDB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonContextStore<T> {

    private final JDB jdb;
    private final Context context;
    private final Class<T> type;

    public JsonContextStore(JDB jdb, Context context, Class<T> type) {
        this.jdb = jdb;
        this.context = context;
        this.type = type;
    }

    public void set(@NotNull String id, @NotNull T value) {
        try {
            jdb.set(context.getName() + "/" + id, value);
        } catch (IOException e) {
            FancyVisuals.getFancyLogger().error("Failed to save context data for id " + id, ThrowableProperty.of(e));
        }
    }

    public @Nullable T get(@NotNull String id) {
        try {
            return jdb.get(context.getName() + "/" + id, type);
        } catch (IOException e) {
            FancyVisuals.getFancyLogger().error("Failed to load context data for id " + id, ThrowableProperty.of(e));
            return null;
        }
    }

    public void remove(@NotNull String id) {
        jdb.delete(context.getName() + "/" + id);
    }

    public @NotNull List<T> getAll() {
        try {
            return jdb.getAll(context.getName(), type);
        } catch (IOException e) {
            FancyVisuals.getFancyLogger().error("Failed to load context data list", ThrowableProperty.of(e));
            return new ArrayList<>();
        }
    }
}
