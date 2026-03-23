package com.fancyinnovations.fancyworlds.listeners;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

public class WorldUnloadListener extends FancyContext implements Listener {

    private final WorldService service;

    public WorldUnloadListener() {
        this.service = WorldService.get();
    }

    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {
        World world = event.getWorld();
        FWorldImpl fworld = (FWorldImpl) service.getWorldByName(world.getName());
        if (fworld == null) {
            return;
        }

        fworld.setBukkitWorld(null);
    }

}
