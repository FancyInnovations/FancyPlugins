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

public class WorldLoadCMD extends FancyContext {

    public static final WorldLoadCMD INSTANCE = new WorldLoadCMD();

    @Command("world load")
    @Description("Loads an existing world from disk and registers it to FancyWorlds")
    @CommandPermission("fancyworlds.commands.world.load")
    public void load(
            final BukkitCommandActor actor,
            String name,
            @Flag @Optional Long seed,
            @Flag @Optional World.Environment environment,
            @Flag @Optional @Suggest({"normal", "flat", "amplified", "large_biomes"}) String generator,
            @Switch(shorthand = 'x') @Optional Boolean structures
    ) {
        if (!WorldFileUtils.isWorldOnDisk(name)) {
            translator.translate("commands.world.load.not_found")
                    .withPrefix()
                    .replace("worldName", name)
                    .send(actor.sender());
            return;
        }

        WorldService service = WorldService.get();
        FWorldImpl fworld = (FWorldImpl) service.getWorldByName(name);
        if (fworld != null && fworld.isWorldLoaded()) {
            translator.translate("commands.world.load.already_loaded")
                    .withPrefix()
                    .replace("worldName", name)
                    .send(actor.sender());
            return;
        }

        if (fworld == null) {
            fworld = new FWorldImpl(
                    UUID.randomUUID(),
                    name,
                    seed,
                    environment,
                    generator,
                    structures,
                    new FWorldSettingsImpl()
            );
        }

        translator.translate("commands.world.load.loading")
                .withPrefix()
                .replace("worldName", name)
                .send(actor.sender());

        final boolean registeredDuringLoad = service.getWorldByName(name) == null;
        if (registeredDuringLoad) {
            service.registerWorld(fworld);
        }

        final FWorldImpl worldToLoad = fworld;
        plugin.getWorldPlatformView().createWorld(worldToLoad).thenAccept(world -> {
            worldToLoad.setBukkitWorld(world);
            if (!registeredDuringLoad) {
                service.registerWorld(worldToLoad);
            }
            translator.translate("commands.world.load.success")
                    .withPrefix()
                    .replace("worldName", name)
                    .send(actor.sender());
        }).exceptionally(throwable -> {
            if (registeredDuringLoad) {
                service.unregisterWorld(worldToLoad);
            }
            translator.translate("commands.world.load.failed")
                    .withPrefix()
                    .replace("worldName", name)
                    .send(actor.sender());
            return null;
        });
    }
}
