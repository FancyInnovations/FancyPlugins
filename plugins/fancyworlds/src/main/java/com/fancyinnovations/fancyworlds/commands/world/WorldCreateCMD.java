package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import com.fancyinnovations.fancyworlds.utils.WorldFileUtils;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.fancyinnovations.fancyworlds.worlds.FWorldSettingsImpl;
import org.bukkit.World;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

public class WorldCreateCMD extends FancyContext {

    public static final WorldCreateCMD INSTANCE = new WorldCreateCMD();

    @Command("world create")
    @Description("Creates a new world and registers it to FancyWorlds")
    @CommandPermission("fancyworlds.commands.world.create")
    public void create(
            final BukkitCommandActor actor,
            String name,
            @Flag @Optional Long seed,
            @Flag @Optional World.Environment environment,
            @Flag @Optional @Suggest({"normal", "flat", "amplified", "large_biomes"}) String generator,
            @Switch(shorthand = 'x') @Optional Boolean structures
    ) {
        WorldService service = WorldService.get();
        if (service.getWorldByName(name) != null) {
            translator.translate("commands.world.create.already_exists")
                    .withPrefix()
                    .replace("worldName", name)
                    .send(actor.sender());
            return;
        }

        if (WorldFileUtils.isWorldOnDisk(name)) {
            translator.translate("commands.world.create.disk_exists")
                    .withPrefix()
                    .replace("worldName", name)
                    .send(actor.sender());
            return;
        }

        FWorldImpl fworld = new FWorldImpl(
                UUID.randomUUID(),
                name,
                seed,
                environment,
                generator,
                structures,
                new FWorldSettingsImpl()
        );

        translator.translate("commands.world.create.generating")
                .withPrefix()
                .replace("worldName", name)
                .send(actor.sender());

        World world = fworld.toWorldCreator().createWorld();
        if (world == null) {
            translator.translate("commands.world.create.failed")
                    .withPrefix()
                    .replace("worldName", name)
                    .send(actor.sender());
            return;
        }

        fworld.setBukkitWorld(world);
        service.registerWorld(fworld);
        translator.translate("commands.world.create.success")
                .withPrefix()
                .replace("worldName", name)
                .send(actor.sender());
    }
}
