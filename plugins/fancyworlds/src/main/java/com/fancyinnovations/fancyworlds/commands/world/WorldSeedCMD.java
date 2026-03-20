package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.World;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Flag;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldSeedCMD extends FancyContext {

    public static final WorldSeedCMD INSTANCE = new WorldSeedCMD();

    @Command({"world seed", "seed"})
    @Description("Shows the seed of the world")
    @CommandPermission("fancyworlds.commands.world.seed")
    public void seed(
            final BukkitCommandActor actor,
            @Flag @Optional World world
    ) {
        if (world == null) {
            world = actor.requirePlayer().getWorld();
        }

        long seed = world.getSeed();

        translator.translate("commands.world.seed.result")
                .withPrefix()
                .replace("worldName", world.getName())
                .replace("seed", String.valueOf(seed))
                .send(actor.sender());
    }
}
