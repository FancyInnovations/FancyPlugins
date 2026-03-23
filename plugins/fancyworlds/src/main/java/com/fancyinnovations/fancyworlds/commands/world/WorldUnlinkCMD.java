package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldUnlinkCMD extends FancyContext {

    public static final WorldUnlinkCMD INSTANCE = new WorldUnlinkCMD();

    @Command("world unlink")
    @Description("Unlinks a world from FancyWorlds, removing it from the plugin's management. The world will not be deleted and will still exist on the server, but it will no longer be managed by FancyWorlds.")
    @CommandPermission("fancyworlds.commands.world.unlink")
    public void unlink(
            final BukkitCommandActor actor,
            final FWorld world
    ) {
        WorldService service = WorldService.get();
        service.unregisterWorld(world);

        translator.translate("commands.world.unlink.success")
                .withPrefix()
                .replace("worldName", world.getName())
                .send(actor.sender());
    }
}
