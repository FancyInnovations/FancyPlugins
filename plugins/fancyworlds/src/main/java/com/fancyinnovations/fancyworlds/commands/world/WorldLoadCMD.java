package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import com.fancyinnovations.fancyworlds.utils.WorldFileUtils;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.fancyinnovations.fancyworlds.worlds.FWorldSettingsImpl;
import com.fancyinnovations.fancyworlds.worlds.service.WorldOperations;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.*;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collection;
import java.util.UUID;

public class WorldLoadCMD extends FancyContext {

    public static final WorldLoadCMD INSTANCE = new WorldLoadCMD();

    @Command("world load")
    @Description("Loads an existing world from disk and registers it to FancyWorlds")
    @CommandPermission("fancyworlds.commands.world.load")
    public void load(
            final BukkitCommandActor actor,
            @SuggestWith(LoadableWorldsSuggestionProvider.class) String name,
            @Flag @Optional Long seed,
            @Flag @Optional World.Environment environment,
            @Flag @Optional @Suggest({"normal", "flat", "amplified", "large_biomes"}) String generator,
            @Switch(shorthand = 'x') @Optional Boolean structures
    ) {
        WorldService service = WorldService.get();
        String diskName = service.getWorldByName(name) != null && WorldFileUtils.isWorldOnDisk(name)
                ? name
                : WorldFileUtils.findWorldNameOnDisk(name);
        if (diskName == null) {
            translator.translate("commands.world.load.not_found")
                    .withPrefix()
                    .replace("worldName", name)
                    .send(actor.sender());
            return;
        }
        name = diskName;

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

        if (!WorldOperations.load(fworld)) {
            translator.translate("commands.world.load.failed")
                    .withPrefix()
                    .replace("worldName", name)
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.world.load.success")
                .withPrefix()
                .replace("worldName", name)
                .send(actor.sender());
    }

    static class LoadableWorldsSuggestionProvider implements SuggestionProvider<BukkitCommandActor> {

        @Override
        public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
            return WorldFileUtils.availableWorldNames();
        }
    }
}
