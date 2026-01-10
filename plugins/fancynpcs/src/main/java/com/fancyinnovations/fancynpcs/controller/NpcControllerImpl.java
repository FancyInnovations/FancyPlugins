package com.fancyinnovations.fancynpcs.controller;

import com.fancyinnovations.fancynpcs.api.FancyNpcsPlugin;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.NpcController;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class NpcControllerImpl implements NpcController {

    @Override
    public void showNpcTo(@NotNull final Npc npc, @NotNull final Player... players) {
        for (Player player : players) {
            boolean isVisible = npc.isShownFor(player);
            boolean shouldSee = shouldSeeNpc(npc, player);

            if (isVisible || !shouldSee) {
                continue;
            }

            npc.spawn(player);

            // Respawn the NPC to fix visibility issues on Folia
            if (ServerSoftware.isFolia() && FancyNpcsPlugin.get().getFeatureFlagConfig().getFeatureFlag("enable-folia-visibility-fix").isEnabled()) {
                FancyNpcsPlugin.get().getNpcThread().schedule(() -> {
                    npc.remove(player);
                    npc.spawn(player);
                }, 100, TimeUnit.MILLISECONDS);
            }
        }
    }

    @Override
    public void hideNpcFrom(@NotNull final Npc npc, @NotNull final Player... players) {
        for (Player player : players) {
            boolean isVisible = npc.isShownFor(player);
            boolean shouldSee = shouldSeeNpc(npc, player);

            if (!isVisible || shouldSee) {
                continue;
            }

            npc.remove(player);
        }
    }

    @Override
    public boolean shouldSeeNpc(@NotNull final Npc npc, @NotNull final Player player) {
        if (!meetsVisibilityConditions(npc, player)) {
            return false;
        }

        return isWithinVisibilityDistance(npc, player);
    }

    private boolean meetsVisibilityConditions(@NotNull final Npc npc, @NotNull final Player player) {
        return npc.getData().getVisibility().canSee(player, npc);
    }

    private boolean isWithinVisibilityDistance(@NotNull final Npc npc, @NotNull final Player player) {
        if (npc.getData().getLocation() == null) {
            return false;
        }

        if (player.getLocation().getWorld() != npc.getData().getLocation().getWorld()) {
            return false;
        }

        if (!npc.getData().isSpawnEntity()) {
            return false;
        }

        int visibilityDistance = (npc.getData().getVisibilityDistance() > -1)
                ? npc.getData().getVisibilityDistance()
                : FancyNpcsPlugin.get().getFancyNpcConfig().getVisibilityDistance();

        if (visibilityDistance == 0) {
            return false;
        }

        if (visibilityDistance != Integer.MAX_VALUE) {
            double distanceSquared = npc.getData().getLocation().distanceSquared(player.getLocation());
            if (distanceSquared > visibilityDistance * visibilityDistance) {
                return false;
            }
        }

        // Check if we should skip invisible NPCs
        if (FancyNpcsPlugin.get().getFancyNpcConfig().isSkipInvisibleNpcs()) {
            var invisibleAttr = FancyNpcsPlugin.get().getAttributeManager()
                    .getAttributeByName(npc.getData().getType(), "invisible");
            if (invisibleAttr != null) {
                String invisibleValue = npc.getData().getAttributes().getOrDefault(invisibleAttr, "false");
                if (invisibleValue.equalsIgnoreCase("true")
                        && !npc.getData().isGlowing()
                        && npc.getData().getEquipment().isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void updateNpcData(@NotNull final Npc npc, @NotNull final Player... players) {
        for (Player player : players) {
            boolean isVisible = npc.isShownFor(player);
            boolean shouldSee = shouldSeeNpc(npc, player);

            if (!isVisible || !shouldSee) {
                continue;
            }

            npc.update(player);
        }
    }

    @Override
    public void refreshNpc(@NotNull final Npc npc, @NotNull final Player... players) {
        hideNpcFrom(npc, players);
        showNpcTo(npc, players);
    }

    @Override
    public void refreshAllNpcs(@NotNull final Player player) {
        for (Npc npc : FancyNpcsPlugin.get().getNpcManager().getAllNpcs()) {
            refreshNpc(npc, player);
        }
    }

}
