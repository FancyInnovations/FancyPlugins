package de.oliver.fancynpcs.api.events;

import de.oliver.fancynpcs.api.Npc;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Is fired when a npc instance is being discarded from memory without being permanently
 * deleted (e.g. during {@code /fancynpcs reload}, which recreates every npc from scratch).
 * <p>
 * Unlike {@link NpcRemoveEvent}, this is not cancellable and does not mean the npc was
 * deleted - it exists so addons can clean up any state (model trackers, hitboxes, ...)
 * that was attached to this specific npc instance / its underlying entity, since a
 * replacement instance is about to take its place.
 */
public class NpcUnloadEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;

    public NpcUnloadEvent(@NotNull Npc npc) {
        this.npc = npc;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the npc instance being discarded
     */
    public @NotNull Npc getNpc() {
        return npc;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }
}
