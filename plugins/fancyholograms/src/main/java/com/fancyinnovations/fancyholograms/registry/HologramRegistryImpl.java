package com.fancyinnovations.fancyholograms.registry;

import com.fancyinnovations.fancyholograms.api.HologramRegistry;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class HologramRegistryImpl implements HologramRegistry {

    private final Map<String, Hologram> holograms;

    public HologramRegistryImpl() {
        this.holograms = new ConcurrentHashMap<>();
    }

    @Override
    public boolean register(Hologram hologram) {
        FancyHologramsPlugin.get().getController().refreshHologram(hologram, Bukkit.getOnlinePlayers());

        boolean registered = holograms.putIfAbsent(hologram.getData().getName(), hologram) != null;

        hologram.getData().getTraitTrait().load();

        hologram.getData().getTraitTrait().onRegister();

        return registered;
    }

    @Override
    public boolean unregister(Hologram hologram) {
        boolean removed = holograms.remove(hologram.getData().getName(), hologram);

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            hologram.despawnFrom(onlinePlayer);
        }

        FancyHologramsPlugin.get().getStorage().delete(hologram.getData());

        hologram.getData().getTraitTrait().onUnregister();

        return removed;
    }

    @Override
    public boolean contains(String name) {
        return holograms.containsKey(name);
    }

    @Override
    public Optional<Hologram> get(String name) {
        return Optional.ofNullable(holograms.get(name));
    }

    @Override
    public Hologram mustGet(String name) {
        if (!contains(name)) {
            throw new IllegalArgumentException("Hologram with name " + name + " does not exist!");
        }

        return holograms.get(name);
    }

    @Override
    public Collection<Hologram> getAll() {
        return Collections.unmodifiableCollection(holograms.values());
    }

    @Override
    public Collection<Hologram> getAllPersistent() {
        return getAll()
                .stream()
                .filter(hologram -> hologram.getData().isPersistent())
                .toList();
    }

    @Override
    public void clear() {
        for (Hologram hologram : holograms.values()) {
            hologram.getData().getTraitTrait().onUnregister();
        }

        holograms.clear();
    }
}
