package com.fancyinnovations.fancyholograms.commands.oldCommands;

import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface Subcommand {

    List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args);

    boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args);

}
