package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.api.worlds.WorldService;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import com.fancyinnovations.fancyworlds.worlds.FWorldImpl;
import com.fancyinnovations.fancyworlds.worlds.FWorldSettingsImpl;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import revxrsal.commands.node.ExecutionContext;

import java.util.Collection;

public class WorldLinkCMD extends FancyContext {

    public static final WorldLinkCMD INSTANCE = new WorldLinkCMD();

    @Command("world link")
    @Description("Links an existing world to FancyWorlds, allowing you to manage it with the plugin")
    @CommandPermission("fancyworlds.commands.world.link")
    public void link(
            final BukkitCommandActor actor,
            final @SuggestWith(LinkableWorldsSuggestionProvider.class) World world
    ) {
        WorldService service = WorldService.get();

        FWorldImpl fworld = (FWorldImpl) service.getWorldByName(world.getName());
        if (fworld != null) {
            translator.translate("commands.world.link.already_linked")
                    .withPrefix()
                    .replace("worldName", world.getName())
                    .send(actor.sender());
            return;
        }

        fworld = new FWorldImpl(
                world.getUID(),
                world.getName(),
                world.getSeed(),
                world.getEnvironment(),
                null,
                world.canGenerateStructures(),
                new FWorldSettingsImpl()
        );
        fworld.setBukkitWorld(world);
        service.registerWorld(fworld);

        translator.translate("commands.world.link.success")
                .withPrefix()
                .replace("worldName", world.getName())
                .send(actor.sender());
    }

    static class LinkableWorldsSuggestionProvider implements SuggestionProvider<BukkitCommandActor> {

        @Override
        public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
            WorldService service = WorldService.get();

            return Bukkit.getWorlds().stream()
                    .map(World::getName)
                    .filter(name -> service.getWorldByName(name) == null)
                    .toList();
        }
    }


}
