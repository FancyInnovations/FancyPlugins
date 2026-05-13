package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.Location;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Flag;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldSetSpawnCMD extends FancyContext {

    public static final WorldSetSpawnCMD INSTANCE = new WorldSetSpawnCMD();

    @Command({"world set_spawn", "setworldspawn"})
    @Description("Sets the spawn point of the world")
    @CommandPermission("fancyworlds.commands.world.set_spawn")
    public void setSpawn(
            final BukkitCommandActor actor,
            @Flag @Optional FWorld world,
            @Flag @Optional Location location
    ) {
        if (world == null) {
            world = plugin.getWorldService().getWorldByName(actor.requirePlayer().getWorld().getName());
            if (world == null) {
                translator.translate("common.world_not_found")
                        .withPrefix()
                        .replace("worldName", actor.requirePlayer().getWorld().getName())
                        .send(actor.sender());
                return;
            }
        }

        if (location == null) {
            location = actor.requirePlayer().getLocation();
        }

        if (!world.isWorldLoaded()) {
            translator.translate("common.world_not_loaded")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        final FWorld finalWorld = world;
        final Location finalLocation = location;
        plugin.runGlobalTask(() -> {
            finalWorld.getBukkitWorld().setSpawnLocation(finalLocation);

            translator.translate("commands.world.set_spawn.success")
                    .withPrefix()
                    .replace("worldName", finalWorld.getName())
                    .replace("location", String.format("X: %d, Y: %d, Z: %d", finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ()))
                    .send(actor.sender());
        });
    }
}
