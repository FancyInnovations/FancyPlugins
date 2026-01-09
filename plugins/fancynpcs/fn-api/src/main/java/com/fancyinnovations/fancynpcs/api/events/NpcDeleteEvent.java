package com.fancyinnovations.fancynpcs.api.events;

import com.fancyinnovations.fancynpcs.api.Npc;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when an NPC is being deleted.
 * This event is fired before the NPC is removed from the registry and storage.
 */
public class NpcDeleteEvent extends Event implements Cancellable {

    private static final HandlerList handlerList = new HandlerList();
    @NotNull
    private final Npc npc;
    @NotNull
    private final CommandSender sender;
    private boolean isCancelled;

    public NpcDeleteEvent(@NotNull Npc npc, @NotNull CommandSender sender) {
        this.npc = npc;
        this.sender = sender;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    /**
     * @return the NPC that is being deleted
     */
    public @NotNull Npc getNpc() {
        return npc;
    }

    /**
     * @return the sender who initiated the deletion
     */
    public @NotNull CommandSender getSender() {
        return sender;
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
