package com.fancyinnovations.fancynpcs.api.events;

import com.fancyinnovations.fancynpcs.api.Npc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an NPC is being despawned (hidden) from a player.
 * This event is fired before the despawn packets are sent to the player.
 */
public class NpcDespawnEvent extends Event implements Cancellable {
    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @NotNull
    private final Player player;
    private boolean isCancelled;

    public NpcDespawnEvent(@NotNull Npc npc, @NotNull Player player) {
        super(!Bukkit.isPrimaryThread());
        this.npc = npc;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the NPC that is being despawned
     */
    public @NotNull Npc getNpc() {
        return npc;
    }

    /**
     * @return the player from whom the NPC is being hidden
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
