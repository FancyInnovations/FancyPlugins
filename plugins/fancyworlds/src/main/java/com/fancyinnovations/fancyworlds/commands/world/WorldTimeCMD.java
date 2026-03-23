package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.GameRule;
import org.bukkit.Registry;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldTimeCMD extends FancyContext {

    public static final WorldTimeCMD INSTANCE = new WorldTimeCMD();

    @Command({"world time set", "time set"})
    @Description("Sets the time of the world. You can use 'day', 'noon', 'night', 'midnight' or a specific time value.")
    @CommandPermission("fancyworlds.commands.world.time.set")
    public void set(
            BukkitCommandActor actor,
            String time,
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

        if (!world.isWorldLoaded()) {
            translator.translate("common.world_not_loaded")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        switch (time.toLowerCase()) {
            case "day":
                world.getBukkitWorld().setTime(1000);
                break;
            case "noon":
                world.getBukkitWorld().setTime(6000);
                break;
            case "night":
                world.getBukkitWorld().setTime(13000);
                break;
            case "midnight":
                world.getBukkitWorld().setTime(18000);
                break;
            default:
                try {
                    long timeValue = Long.parseLong(time);
                    world.getBukkitWorld().setTime(timeValue);
                } catch (NumberFormatException e) {
                    translator.translate("commands.world.time.set.invalid_time")
                            .withPrefix()
                            .replace("time", time)
                            .send(actor.sender());
                    return;
                }
        }

        translator.translate("commands.world.time.set.success")
                .withPrefix()
                .replace("worldName", world.getName())
                .replace("time", time)
                .send(actor.sender());
    }

    @Command({"world time current", "time current"})
    @Description("Shows the current time of the world")
    @CommandPermission("fancyworlds.commands.world.time.current")
    public void current(
            BukkitCommandActor actor,
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

        if (!world.isWorldLoaded()) {
            translator.translate("common.world_not_loaded")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        long currentTime = world.getBukkitWorld().getTime();
        translator.translate("commands.world.time.current")
                .withPrefix()
                .replace("worldName", world.getName())
                .replace("time", String.valueOf(currentTime))
                .send(actor.sender());
    }

    @Command("day")
    @Description("Sets the time of the world to day")
    @CommandPermission("fancyworlds.commands.world.time.set")
    public void setDay(
            BukkitCommandActor actor,
            @Flag @Optional FWorld world
    ) {
        set(actor, "day", world);
    }

    @Command("noon")
    @Description("Sets the time of the world to noon")
    @CommandPermission("fancyworlds.commands.world.time.set")
    public void setNoon(
            BukkitCommandActor actor,
            @Flag @Optional FWorld world
    ) {
        set(actor, "noon", world);
    }

    @Command("night")
    @Description("Sets the time of the world to night")
    @CommandPermission("fancyworlds.commands.world.time.set")
    public void setNight(
            BukkitCommandActor actor,
            @Flag @Optional FWorld world
    ) {
        set(actor, "night", world);
    }

    @Command("midnight")
    @Description("Sets the time of the world to midnight")
    @CommandPermission("fancyworlds.commands.world.time.set")
    public void setMidnight(
            BukkitCommandActor actor,
            @Flag @Optional FWorld world
    ) {
        set(actor, "midnight", world);
    }
}
