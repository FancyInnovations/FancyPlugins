package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Collection;
import java.util.List;

public class WorldListCMD extends FancyContext {

    public static final WorldListCMD INSTANCE = new WorldListCMD();

    @Command({"world list", "worlds"})
    @Description("Shows a list of all loaded worlds")
    @CommandPermission("fancyworlds.commands.world.list")
    public void list(
            final BukkitCommandActor actor
    ) {
        WorldService service = WorldService.get();
        Collection<FWorld> worlds = service.getAllWorlds();

        if (worlds.isEmpty()) {
            translator.translate("commands.world.list.no_worlds")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.world.list.header")
                .withPrefix()
                .replace("worldCount", String.valueOf(worlds.size()))
                .send(actor.sender());

        List<FWorld> loadedWorlds = worlds.stream()
                .filter(FWorld::isWorldLoaded)
                .toList();
        for (FWorld world : loadedWorlds) {
            translator.translate("commands.world.list.entry_loaded")
                    .replace("worldName", world.getName())
                    .replace("playerCount", String.valueOf(world.getBukkitWorld().getPlayerCount()))
                    .replace("entityCount", String.valueOf(world.getBukkitWorld().getEntityCount()))
                    .replace("chunkCount", String.valueOf(world.getBukkitWorld().getChunkCount()))
                    .send(actor.sender());
        }

        List<FWorld> unloadedWorlds = worlds.stream()
                .filter(world -> !world.isWorldLoaded())
                .toList();
        for (FWorld world : unloadedWorlds) {
            translator.translate("commands.world.list.entry_unloaded")
                    .replace("worldName", world.getName())
                    .send(actor.sender());
        }
    }

}
