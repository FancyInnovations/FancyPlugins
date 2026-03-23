package com.fancyinnovations.fancyworlds.listeners;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.fancyinnovations.fancyworlds.worlds.FWorldSettingsImpl;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

public class WorldLoadListener extends FancyContext implements Listener {

    private final WorldService service;

    public WorldLoadListener() {
        this.service = WorldService.get();
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        World world = event.getWorld();
        FWorldImpl fworld = (FWorldImpl) service.getWorldByName(world.getName());
        if (fworld == null) {
            if (!config.automaticallyLinkWorlds()) {
                logger.debug("The world '" + world.getName() + "' is not registered to FancyWorlds and automatic linking is disabled. Skipping.");
                return;
            }

            fworld = new FWorldImpl(
                    world.getUID(),
                    world.getName(),
                    world.getSeed(),
                    world.getEnvironment(),
                    null,
                    world.canGenerateStructures(),
                    new FWorldSettingsImpl()
            );
            service.registerWorld(fworld);

            logger.info("The world '" + world.getName() + "' has been registered to FancyWorlds");
        }

        fworld.setBukkitWorld(world);

        logger.info("The world '" + world.getName() + "' has been linked to FancyWorlds");
    }

}
