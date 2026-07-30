package de.oliver.fancynpcs.api.events;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired after a NPC has been despawned for a player (e.g. the player moved out of visibility range)
 */
public class NpcDespawnEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @NotNull
    private final Player player;

    public NpcDespawnEvent(@NotNull Npc npc, @NotNull Player player) {
        super(true);
        this.npc = npc;
        this.player = player;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the npc that was despawned
     */
    public @NotNull Npc getNpc() {
        return npc;
    }

    /**
     * @return the player from whom the npc was despawned
     */
    public @NotNull Player getPlayer() {
        return player;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
