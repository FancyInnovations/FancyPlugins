package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.World;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldInfoCMD extends FancyContext {

    public static final WorldInfoCMD INSTANCE = new WorldInfoCMD();

    @Command("world info")
    @Description("Shows information about a world")
    @CommandPermission("fancyworlds.commands.world.info")
    public void info(BukkitCommandActor actor, FWorld world) {
        World bukkitWorld = world.getBukkitWorld();

        translator.translate("commands.world.info.header")
                .withPrefix()
                .replace("worldName", world.getName())
                .send(actor.sender());

        translator.translate("commands.world.info.status")
                .replace("status", bukkitWorld == null ? "Unloaded" : "Loaded")
                .send(actor.sender());
        translator.translate("commands.world.info.environment")
                .replace("environment", world.getEnvironment().name())
                .send(actor.sender());
        translator.translate("commands.world.info.generator")
                .replace("generator", world.getGenerator())
                .send(actor.sender());
        translator.translate("commands.world.info.structures")
                .replace("structures", world.canGenerateStructures() ? "Yes" : "No")
                .send(actor.sender());

        if (actor.sender().hasPermission("fancyworlds.commands.world.seed")) {
            translator.translate("commands.world.info.seed")
                    .replace("seed", String.valueOf(world.getSeed()))
                    .send(actor.sender());
        }

        if (bukkitWorld != null) {
            translator.translate("commands.world.info.counts")
                    .replace("playerCount", String.valueOf(bukkitWorld.getPlayerCount()))
                    .replace("entityCount", String.valueOf(bukkitWorld.getEntityCount()))
                    .replace("chunkCount", String.valueOf(bukkitWorld.getChunkCount()))
                    .send(actor.sender());
        }
    }
}
