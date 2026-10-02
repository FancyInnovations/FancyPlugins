package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import com.fancyinnovations.fancyworlds.utils.WorldFileUtils;
import com.fancyinnovations.fancyworlds.worlds.service.WorldCreationService;
import org.bukkit.World;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;


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
        if (!WorldCreationService.isValidName(name)) {
            translator.translate("commands.world.create.invalid_name")
                    .withPrefix().send(actor.sender());
            return;
        }

        if (WorldService.get().getAllWorlds().stream().anyMatch(world -> world.getName().equalsIgnoreCase(name))) {
            translator.translate("commands.world.create.already_exists")
                    .withPrefix().replace("worldName", name).send(actor.sender());
            return;
        }
        if (WorldFileUtils.findWorldNameOnDisk(name) != null) {
            translator.translate("commands.world.create.disk_exists")
                    .withPrefix().replace("worldName", name).send(actor.sender());
            return;
        }
        translator.translate("commands.world.create.generating")
                .withPrefix()
                .replace("worldName", name)
                .send(actor.sender());

        WorldCreationService.Result result = WorldCreationService.create(name, seed, environment, generator, structures);
        String key = switch (result.status()) {
            case CREATED -> "commands.world.create.success";
            case INVALID_NAME -> "commands.world.create.invalid_name";
            case ALREADY_EXISTS -> "commands.world.create.already_exists";
            case DISK_EXISTS -> "commands.world.create.disk_exists";
            case FAILED -> "commands.world.create.failed";
        };
        translator.translate(key)
                .withPrefix()
                .replace("worldName", name)
                .send(actor.sender());
    }
}
