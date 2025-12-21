package com.fancyinnovations.fancyholograms.controller;

import com.fancyinnovations.fancyholograms.api.HologramController;
import com.fancyinnovations.fancyholograms.api.data.DisplayHologramData;
import com.fancyinnovations.fancyholograms.api.data.HologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import com.google.common.cache.CacheBuilder;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HologramControllerImpl implements HologramController {

    @Override
    public void showHologramTo(@NotNull final Hologram hologram, @NotNull final Player... players) {
        for (Player player : players) {
            boolean isVisible = hologram.isViewer(player);
            boolean shouldSee = shouldSeeHologram(hologram, player);

            if (isVisible || !shouldSee) {
                continue;
            }

            hologram.spawnTo(player);
        }
    }

    @Override
    public void hideHologramFrom(@NotNull final Hologram hologram, @NotNull final Player... players) {
        for (Player player : players) {
            boolean isVisible = hologram.isViewer(player);
            boolean shouldSee = shouldSeeHologram(hologram, player);

            if (!isVisible || shouldSee) {
                continue;
            }

            hologram.despawnFrom(player);
        }
    }

    @Override
    @ApiStatus.Internal
    public void updateHologramData(@NotNull final Hologram hologram, @NotNull final Player... players) {
        for (Player player : players) {
            boolean isVisible = hologram.isViewer(player);
            boolean shouldSee = shouldSeeHologram(hologram, player);

            if (!isVisible || !shouldSee) {
                continue;
            }

            hologram.updateFor(player);
            hologram.getData().getTraitTrait().onUpdate(player);
        }
    }

    @Override
    public boolean shouldSeeHologram(@NotNull final Hologram hologram, @NotNull final Player player) {
        if (!meetsVisibilityConditions(hologram, player)) {
            return false;
        }

        return isWithinVisibilityDistance(hologram, player);
    }

    @Override
    public void refreshHologram(@NotNull final Hologram hologram, @NotNull final Player... players) {
        hideHologramFrom(hologram, players);
        showHologramTo(hologram, players);
    }

    private boolean meetsVisibilityConditions(@NotNull final Hologram hologram, @NotNull final Player player) {
        return hologram.getData().getVisibility().canSee(player, hologram);
    }

    private boolean isWithinVisibilityDistance(@NotNull final Hologram hologram, @NotNull final Player player) {
        if (!hologram.getData().getWorldName().equals(player.getWorld().getName())) {
            return false;
        }

        final Location location = hologram.getData().getLocation();
        if (location.getWorld() == null) {
            return false;
        }

        int visibilityDistance = hologram.getData().getVisibilityDistance();
        double distanceSquared = location.distanceSquared(player.getLocation());

        return distanceSquared <= visibilityDistance * visibilityDistance;
    }

    public void initRefreshTask() {
        FancyHologramsPlugin.get().getHologramThread().scheduleWithFixedDelay(() -> {
            for (Hologram hologram : FancyHologramsPlugin.get().getRegistry().getAll()) {
                refreshHologram(hologram, Bukkit.getOnlinePlayers().toArray(new Player[0]));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void initUpdateTask() {
        final var updateTimes = CacheBuilder.newBuilder()
                .expireAfterAccess(Duration.ofMinutes(5))
                .<String, Long>build();

        FancyHologramsPlugin.get().getHologramThread().scheduleWithFixedDelay(() -> {
            final var time = System.currentTimeMillis();

            for (final var hologram : FancyHologramsPlugin.get().getRegistry().getAll()) {
                HologramData data = hologram.getData();
                if (data.hasChanges()) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        if (!shouldSeeHologram(hologram, onlinePlayer)) {
                            continue;
                        }

                        hologram.updateFor(onlinePlayer);
                    }

                    data.setHasChanges(false);

                    if (data instanceof TextHologramData) {
                        updateTimes.put(hologram.getData().getName(), time);
                    }
                }
            }
        }, 50, 1000, TimeUnit.MILLISECONDS);

        final int hologramUpdateIntervalMs = FancyHologramsPlugin.get().getFHConfiguration().getHologramUpdateInterval();

        FancyHologramsPlugin.get().getHologramThread().scheduleWithFixedDelay(() -> {
            final var time = System.currentTimeMillis();

            for (final var hologram : FancyHologramsPlugin.get().getRegistry().getAll()) {
                if (hologram.getData() instanceof TextHologramData textData) {
                    final var interval = textData.getTextUpdateInterval();
                    if (interval < 1) {
                        continue; // doesn't update
                    }

                    final var lastUpdate = updateTimes.asMap().get(textData.getName());
                    if (lastUpdate != null && time < (lastUpdate + interval)) {
                        continue;
                    }

                    if (lastUpdate == null || time > (lastUpdate + interval)) {
                        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                            if (!shouldSeeHologram(hologram, onlinePlayer)) {
                                continue;
                            }

                            hologram.updateFor(onlinePlayer);
                        }

                        updateTimes.put(textData.getName(), time);
                    }
                }
            }
        }, 50, hologramUpdateIntervalMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Syncs a hologram with its linked NPC, if any.
     *
     * @param hologram The hologram to sync.
     */
    public void syncHologramWithNpc(@NotNull final Hologram hologram) {
        String linkedNpcName = hologram.getData().getLinkedNpcName();
        if (linkedNpcName == null) {
            return;
        }

        Npc npc = FancyNpcsPlugin.get().getNpcManager().getNpc(linkedNpcName);
        if (npc == null) {
            return;
        }

        npc.getData().setDisplayName("<empty>");
        npc.getData().setShowInTab(false);
        npc.updateForAll();

        float npcScale = npc.getData().getScale();

        if (hologram.getData() instanceof DisplayHologramData displayData) {
            displayData.setScale(new Vector3f(npcScale));
        }

        Location location = npc.getData().getLocation().clone().add(0, (npc.getEyeHeight() * npcScale) + (0.5 * npcScale), 0);

        for (Map.Entry<NpcAttribute, String> entry : npc.getData().getAttributes().entrySet()) {
            NpcAttribute attribute = entry.getKey();
            String value = entry.getValue();

            if (npc.getData().getType() == EntityType.PLAYER) {
                final var poseAttr = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(npc.getData().getType(), "pose");
                if (poseAttr != null) {
                    final var pose = npc.getData().getAttributes().get(poseAttr);
                    if (pose != null) {
                        switch (pose.toLowerCase()) {
                            case "sitting" -> location.subtract(0, 0.7 * npcScale, 0);
                            case "sleeping" -> location.subtract(0, 0.4 * npcScale, 0);
                            case "crouching" -> location.subtract(0, 0.1 * npcScale, 0);
                        }
                    }
                }
            }
        }

        hologram.getData().setLocation(location);
    }
}
