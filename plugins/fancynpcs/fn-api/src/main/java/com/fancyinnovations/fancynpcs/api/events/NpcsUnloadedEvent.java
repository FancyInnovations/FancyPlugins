package com.fancyinnovations.fancynpcs.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.ApiStatus;

/**
 * Called when all NPCs are unloaded (e.g., during plugin disable or reload).
 *
 * Will be removed, once the NPC loading is coupled with the loading of worlds! Be aware of that!
 */
@ApiStatus.Experimental()
public class NpcsUnloadedEvent extends Event {
    private static final HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
