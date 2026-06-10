package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.GameRule;
import org.bukkit.GameRules;
import org.bukkit.Registry;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public class WorldGamerulesCMD extends FancyContext {

    public static final WorldGamerulesCMD INSTANCE = new WorldGamerulesCMD();

    @Command({"world gamerules list", "gamerule list"})
    @Description("Shows all gamerules of the world")
    @CommandPermission("fancyworlds.commands.world.gamerules.list")
    public void list(
            BukkitCommandActor actor,
            @Flag @Optional FWorld world,
            @Switch("changed_only") @Optional boolean changedOnly
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

        FWorld finalWorld = world;
        plugin.runGlobalTask(() -> {
            translator.translate("commands.world.gamerules.list.header")
                    .withPrefix()
                    .replace("worldName", finalWorld.getName())
                    .send(actor.sender());

            Registry.GAME_RULE.stream().forEach(gameRule -> {
                if (!finalWorld.getBukkitWorld().isGameRule(gameRule.key().value())) {
                    return;
                }

                Object value = finalWorld.getBukkitWorld().getGameRuleValue(gameRule);
                if (value == null) {
                    value = "null";
                }

                Object defaultValue = finalWorld.getBukkitWorld().getGameRuleDefault(gameRule);
                if (defaultValue == null) {
                    defaultValue = "null";
                }

                if (changedOnly && value.equals(defaultValue)) {
                    return;
                }

                translator.translate("commands.world.gamerules.list.entry")
                        .replace("gamerule", gameRule.getKey().getKey())
                        .replace("value", value.toString())
                        .replace("defaultValue", defaultValue.toString())
                        .send(actor.sender());
            });
        });
    }

    @Command({"world gamerules set", "gamerule set"})
    @Description("Sets a gamerule of the world")
    @CommandPermission("fancyworlds.commands.world.gamerules.set")
    public void set(
            BukkitCommandActor actor,
            GameRule gamerule,
            String value,
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

        if (!world.getBukkitWorld().isGameRule(gamerule.key().value())) {
            translator.translate("commands.world.gamerules.set.invalid_gamerule")
                    .withPrefix()
                    .replace("gameruleName", gamerule.getKey().getKey())
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        final FWorld finalWorldForSet = world;
        if (value.equalsIgnoreCase("@default")) {
            plugin.runGlobalTask(() -> {
                Object defaultValue = finalWorldForSet.getBukkitWorld().getGameRuleDefault(gamerule);
                if (defaultValue == null) {
                    translator.translate("commands.world.gamerules.set.failed")
                            .withPrefix()
                            .replace("gameruleName", gamerule.getKey().getKey())
                            .replace("worldName", finalWorldForSet.getName())
                            .send(actor.sender());
                    return;
                }

                finalWorldForSet.getBukkitWorld().setGameRule(gamerule, defaultValue);
                translator.translate("commands.world.gamerules.set.success")
                        .withPrefix()
                        .replace("gameruleName", gamerule.getKey().getKey())
                        .replace("value", defaultValue.toString())
                        .replace("worldName", finalWorldForSet.getName())
                        .send(actor.sender());
            });
            return;
        }

        switch (gamerule.getType().getSimpleName()) {
            case "Boolean" -> {
                Boolean booleanValue = switch (value.toLowerCase()) {
                    case "true", "yes" -> true;
                    case "false", "no" -> false;
                    default -> null;
                };
                if (booleanValue == null) {
                    translator.translate("commands.world.gamerules.set.invalid_value")
                            .withPrefix()
                            .replace("gamerule", gamerule.getKey().getKey())
                            .replace("value", value)
                            .replace("expectedType", "boolean")
                            .send(actor.sender());
                    return;
                }
                plugin.runGlobalTask(() -> {
                    finalWorldForSet.getBukkitWorld().setGameRule(gamerule, booleanValue);
                    translator.translate("commands.world.gamerules.set.success")
                            .withPrefix()
                            .replace("gameruleName", gamerule.getKey().getKey())
                            .replace("value", value)
                            .replace("worldName", finalWorldForSet.getName())
                            .send(actor.sender());
                });
            }
            case "Integer" -> {
                int intValue;
                try {
                    intValue = Integer.parseInt(value);
                } catch (NumberFormatException e) {
                    translator.translate("commands.world.gamerules.set.invalid_value")
                            .withPrefix()
                            .replace("gamerule", gamerule.getKey().getKey())
                            .replace("value", value)
                            .replace("expectedType", "integer")
                            .send(actor.sender());
                    return;
                }
                plugin.runGlobalTask(() -> {
                    finalWorldForSet.getBukkitWorld().setGameRule(gamerule, intValue);
                    translator.translate("commands.world.gamerules.set.success")
                            .withPrefix()
                            .replace("gameruleName", gamerule.getKey().getKey())
                            .replace("value", value)
                            .replace("worldName", finalWorldForSet.getName())
                            .send(actor.sender());
                });
            }
            default -> {
                translator.translate("commands.world.gamerules.set.failed")
                        .withPrefix()
                        .replace("gameruleName", gamerule.getKey().getKey())
                        .replace("worldName", finalWorldForSet.getName())
                        .send(actor.sender());
                throw new UnsupportedOperationException("Unsupported gamerule type: " + gamerule.getType().getSimpleName());
            }
        }
    }
}
