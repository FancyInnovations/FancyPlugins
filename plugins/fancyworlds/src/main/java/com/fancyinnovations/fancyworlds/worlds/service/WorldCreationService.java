package com.fancyinnovations.fancyworlds.worlds.service;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.WorldFileUtils;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.fancyinnovations.fancyworlds.worlds.FWorldSettingsImpl;
import org.bukkit.World;

import java.util.UUID;
import java.util.regex.Pattern;

public final class WorldCreationService {

    private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_-]{1,64}");

    private WorldCreationService() {
    }

    public static Result create(String name, Long seed, World.Environment environment, String generator, Boolean structures) {
        return create(name, seed, environment, generator, structures, true);
    }

    public static Result create(String name, Long seed, World.Environment environment, String generator, Boolean structures, boolean validateName) {
        if (validateName && (name == null || !VALID_NAME.matcher(name).matches())) {
            return new Result(Status.INVALID_NAME, null);
        }

        WorldService service = WorldService.get();
        if (service.getWorldByName(name) != null) {
            return new Result(Status.ALREADY_EXISTS, null);
        }
        if (WorldFileUtils.isWorldOnDisk(name)) {
            return new Result(Status.DISK_EXISTS, null);
        }

        FWorldImpl fworld = new FWorldImpl(UUID.randomUUID(), name, seed, environment, generator, structures, new FWorldSettingsImpl());
        // WorldLoadEvent fires during createWorld; make it find this object instead of auto-linking a duplicate.
        service.registerWorld(fworld);
        World world;
        try {
            world = fworld.toWorldCreator().createWorld();
        } catch (RuntimeException ex) {
            service.unregisterWorld(fworld);
            return new Result(Status.FAILED, null);
        }

        if (world == null) {
            service.unregisterWorld(fworld);
            return new Result(Status.FAILED, null);
        }

        fworld.setBukkitWorld(world);
        service.registerWorld(fworld);

        return new Result(Status.CREATED, fworld);
    }

    public enum Status {
        CREATED,
        INVALID_NAME,
        ALREADY_EXISTS,
        DISK_EXISTS,
        FAILED,
    }

    public record Result(Status status, FWorldImpl world) {
    }
}
