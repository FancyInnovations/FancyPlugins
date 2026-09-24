package com.fancyinnovations.fancyworlds.commands.portal;

import com.fancyinnovations.fancyworlds.dialogs.WorldsDialogController;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class PortalMenuCMD {

    private final WorldsDialogController dialogs;

    public PortalMenuCMD(WorldsDialogController dialogs) {
        this.dialogs = dialogs;
    }

    @Command("portal menu")
    @Description("Opens the portal management menu")
    @CommandPermission("fancyworlds.commands.portal.menu")
    public void menu(BukkitCommandActor actor) {
        dialogs.openPortalList(actor.requirePlayer(), 1);
    }
}
