package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.Difficulty;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.Flag;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldDifficultyCMD extends FancyContext {

    public static final WorldDifficultyCMD INSTANCE = new WorldDifficultyCMD();

    @Command({"world difficulty set", "difficulty set"})
    @Description("Sets the difficulty of a world")
    @CommandPermission("fancyworlds.commands.world.difficulty")
    public void setSpawn(
            final BukkitCommandActor actor,
            Difficulty difficulty,
            @Flag @Optional FWorld world
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

        world.getBukkitWorld().setDifficulty(difficulty);

        translator.translate("commands.world.difficulty.set.success")
                .withPrefix()
                .replace("worldName", world.getName())
                .replace("difficulty", difficulty.name())
                .send(actor.sender());
    }

    @Command({"world difficulty current", "difficulty current"})
    @Description("Shows the current difficulty of a world")
    @CommandPermission("fancyworlds.commands.world.difficulty")
    public void current(
            final BukkitCommandActor actor,
            @Flag @Optional FWorld world
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

        Difficulty difficulty = world.getBukkitWorld().getDifficulty();

        translator.translate("commands.world.difficulty.current")
                .withPrefix()
                .replace("worldName", world.getName())
                .replace("difficulty", difficulty.name())
                .send(actor.sender());
    }
}
