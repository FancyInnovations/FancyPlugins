package com.fancyinnovations.fancyholograms.tests.mocks;

import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.storage.json.JsonAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HologramMock extends Hologram {

    private final Runnable spawnToCallback;
    private final Runnable despawnFromCallback;
    private final Runnable updateForCallback;

    public HologramMock(@NotNull HologramData data, Runnable spawnToCallback, Runnable despawnFromCallback, Runnable updateForCallback) {
        super(data);
        this.spawnToCallback = spawnToCallback;
        this.despawnFromCallback = despawnFromCallback;
        this.updateForCallback = updateForCallback;
    }

    @Override
    public void spawnTo(@NotNull Player player) {
        spawnToCallback.run();
        viewers.add(player.getUniqueId());
    }

    @Override
    public void despawnFrom(@NotNull Player player) {
        despawnFromCallback.run();
        viewers.remove(player.getUniqueId());
    }

    @Override
    public void updateFor(@NotNull Player player) {
        updateForCallback.run();
    }

    @Override
    public Object toJson() {
        return JsonAdapter.hologramDataToJson(data);
    }
}
