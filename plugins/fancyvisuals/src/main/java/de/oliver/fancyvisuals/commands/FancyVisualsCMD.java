package de.oliver.fancyvisuals.commands;

import de.oliver.fancyvisuals.FancyVisuals;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FancyVisualsCMD extends Command {

    private static final String PERMISSION = "fancyvisuals.reload";
    private static final String LEGACY_PERMISSION = "fancyvisiuals.reload";

    private final FancyVisuals plugin;

    public FancyVisualsCMD(FancyVisuals plugin) {
        super("fancyvisuals");
        this.plugin = plugin;
        setAliases(List.of("fv", "fancyvisiuals"));
        setPermission(PERMISSION);
        setDescription("FancyVisuals admin command");
        setUsage("/fancyvisuals reload");
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Usage: /" + label + " reload");
            return true;
        }

        String sub = args[0].toLowerCase();
        if ("reload".equals(sub) || "relaod".equals(sub)) {
            if (!hasReloadPermission(sender)) {
                sender.sendMessage("You do not have permission to do that.");
                return true;
            }

            plugin.reloadAll();
            sender.sendMessage("FancyVisuals reloaded.");
            return true;
        }

        sender.sendMessage("Unknown subcommand. Usage: /" + label + " reload");
        return true;
    }

    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
        if (!hasReloadPermission(sender)) {
            return List.of();
        }

        if (args.length == 1) {
            String prefix = args[0].toLowerCase();
            List<String> suggestions = new ArrayList<>(2);
            if ("reload".startsWith(prefix)) {
                suggestions.add("reload");
            }
            if ("relaod".startsWith(prefix)) {
                suggestions.add("relaod");
            }
            return suggestions;
        }

        return List.of();
    }

    private boolean hasReloadPermission(CommandSender sender) {
        return sender.isOp() || sender.hasPermission(PERMISSION) || sender.hasPermission(LEGACY_PERMISSION);
    }
}
