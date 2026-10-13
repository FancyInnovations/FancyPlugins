package de.oliver.fancyvisuals.nametags.store;

import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancyvisuals.FancyVisuals;
import de.oliver.fancyvisuals.api.Context;
import de.oliver.fancyvisuals.api.nametags.Nametag;
import de.oliver.fancyvisuals.api.nametags.NametagStore;
import de.oliver.jdb.JDB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonNametagStore implements NametagStore {

    private final Context context;
    private final JDB jdb;

    public JsonNametagStore(JDB jdb, Context context) {
        this.jdb = jdb;
        this.context = context;
    }

    @Override
    public void setNametag(@NotNull String id, @NotNull Nametag nametag) {
        try {
            jdb.set(context.getName() + "/" + id, nametag);
        } catch (IOException e) {
            FancyVisuals.getFancyLogger().error("Failed to set nametag for id " + id, ThrowableProperty.of(e));
        }
    }

    @Override
    public @Nullable Nametag getNametag(@NotNull String id) {
        Nametag nametag = null;

        try {
            nametag = jdb.get(context.getName() + "/" + id, Nametag.class);
        } catch (IOException e) {
            FancyVisuals.getFancyLogger().error("Failed to get nametag for id " + id, ThrowableProperty.of(e));
        }

        return nametag;
    }

    @Override
    public void removeNametag(@NotNull String id) {
        jdb.delete(context.getName() + "/" + id);
    }

    @Override
    public @NotNull List<Nametag> getNametags() {
        List<Nametag> nametags = new ArrayList<>();

        try {
            jdb.getAll(context.getName(), Nametag.class);
        } catch (IOException e) {
            FancyVisuals.getFancyLogger().error("Failed to get all nametags", ThrowableProperty.of(e));
        }

        return nametags;
    }
}
