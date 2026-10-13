package de.oliver.fancyvisuals.nametags.fake;

import de.oliver.fancyvisuals.api.nametags.Nametag;
import de.oliver.fancyvisuals.api.nametags.NametagStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakeNametagStore implements NametagStore {

    private final Map<String, Nametag> nametags;

    public FakeNametagStore() {
        this.nametags = new ConcurrentHashMap<>();
    }

    @Override
    public void setNametag(@NotNull String id, @NotNull Nametag nametag) {
        nametags.put(id, nametag);
    }

    @Override
    public @Nullable Nametag getNametag(@NotNull String id) {
        return nametags.getOrDefault(id, null);
    }

    @Override
    public void removeNametag(@NotNull String id) {
        nametags.remove(id);
    }

    @Override
    public @NotNull List<Nametag> getNametags() {
        return List.copyOf(nametags.values());
    }
}
