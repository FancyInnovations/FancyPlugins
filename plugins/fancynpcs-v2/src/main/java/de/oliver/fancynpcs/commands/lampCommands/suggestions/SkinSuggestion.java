package de.oliver.fancynpcs.commands.lampCommands.suggestions;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.skins.SkinUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public final class SkinSuggestion implements SuggestionProvider<BukkitCommandActor> {

    @Override
    public @NotNull Collection<String> getSuggestions(@NotNull ExecutionContext<BukkitCommandActor> context) {
        ArrayList<String> result = new ArrayList<>();
        result.add("@none");
        result.add("@mirror");

        Bukkit.getOnlinePlayers().stream().map(Player::getName).forEach(result::add);

        File[] files = new File(FancyNpcs.getInstance().getDataFolder(), "skins").listFiles();
        if (files != null) {
            for (File file : files) if (SkinUtils.isFile(file.getName())) result.add(file.getName());
        }

        return result;
    }
}
