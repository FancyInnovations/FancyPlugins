package com.fancyinnovations.fancyworlds.commands.world;

import com.fancyinnovations.fancyworlds.dialogs.WorldsDialogController;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class WorldMenuCMD {

    private final WorldsDialogController dialogs;

    public WorldMenuCMD(WorldsDialogController dialogs) {
        this.dialogs = dialogs;
    }

    @Command("world menu")
    @Description("Opens the world management menu")
    @CommandPermission("fancyworlds.commands.world.menu")
    public void menu(BukkitCommandActor actor) {
        dialogs.openWorldList(actor.requirePlayer(), 1);
    }
}
