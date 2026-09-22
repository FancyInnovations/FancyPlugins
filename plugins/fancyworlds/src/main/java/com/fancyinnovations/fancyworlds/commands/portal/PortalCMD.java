package com.fancyinnovations.fancyworlds.commands.portal;

import com.fancyinnovations.fancyworlds.api.portals.FPortal;
import com.fancyinnovations.fancyworlds.api.portals.PortalPosition;
import com.fancyinnovations.fancyworlds.api.portals.PortalService;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.portals.FPortalImpl;
import com.fancyinnovations.fancyworlds.portals.PortalWand;
import com.fancyinnovations.fancyworlds.portals.selection.PortalSelection;
import com.fancyinnovations.fancyworlds.portals.selection.PortalSelectionManager;
import com.fancyinnovations.fancyworlds.utils.FancyContext;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.regex.Pattern;

public class PortalCMD extends FancyContext {

    private static final Pattern VALID_NAME = Pattern.compile("[A-Za-z0-9_-]{1,64}");

    private final PortalSelectionManager selectionManager;
    private final PortalWand portalWand;

    public PortalCMD(PortalSelectionManager selectionManager, PortalWand portalWand) {
        this.selectionManager = selectionManager;
        this.portalWand = portalWand;
    }

    @Command("portal wand")
    @Description("Gives you the portal selection wand")
    @CommandPermission("fancyworlds.commands.portal.wand")
    public void wand(BukkitCommandActor actor) {
        Player player = actor.requirePlayer();
        ItemStack wand = portalWand.createItem();
        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(wand);
        for (ItemStack item : leftovers.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }

        translator.translate("commands.portal.wand.success")
                .withPrefix()
                .send(player);
    }

    @Command("portal create")
    @Description("Creates a portal from your wand selection")
    @CommandPermission("fancyworlds.commands.portal.create")
    public void create(BukkitCommandActor actor, String name, FWorld destinationWorld) {
        Player player = actor.requirePlayer();
        PortalService service = PortalService.get();

        if (!VALID_NAME.matcher(name).matches()) {
            translator.translate("commands.portal.create.invalid_name")
                    .withPrefix()
                    .replace("portalName", name)
                    .send(player);
            return;
        }

        if (service.getPortalByName(name) != null) {
            translator.translate("commands.portal.create.already_exists")
                    .withPrefix()
                    .replace("portalName", name)
                    .send(player);
            return;
        }

        PortalSelection selection = selectionManager.getSelection(player.getUniqueId());
        if (selection == null || !selection.isComplete()) {
            translator.translate("commands.portal.create.incomplete_selection")
                    .withPrefix()
                    .send(player);
            return;
        }
        if (!selection.isInOneWorld()) {
            translator.translate("commands.portal.create.different_worlds")
                    .withPrefix()
                    .send(player);
            return;
        }

        FPortal portal = new FPortalImpl(
                null,
                name,
                selection.first().worldName(),
                selection.first().position(),
                selection.second().position(),
                destinationWorld.getName()
        );
        FPortal overlappingPortal = service.getPortalsInWorld(portal.getWorldName()).stream()
                .filter(portal::overlaps)
                .findFirst()
                .orElse(null);
        if (overlappingPortal != null) {
            translator.translate("commands.portal.create.overlaps")
                    .withPrefix()
                    .replace("portalName", overlappingPortal.getName())
                    .send(player);
            return;
        }

        service.registerPortal(portal);
        selectionManager.clearSelection(player.getUniqueId());
        translator.translate("commands.portal.create.success")
                .withPrefix()
                .replace("portalName", portal.getName())
                .replace("worldName", portal.getWorldName())
                .replace("destinationWorld", portal.getDestinationWorldName())
                .replace("volume", String.valueOf(portal.getVolume()))
                .send(player);
    }

    @Command({"portal list", "portals"})
    @Description("Lists all portals")
    @CommandPermission("fancyworlds.commands.portal.list")
    public void list(BukkitCommandActor actor) {
        Collection<FPortal> portals = PortalService.get().getAllPortals();
        if (portals.isEmpty()) {
            translator.translate("commands.portal.list.none")
                    .withPrefix()
                    .send(actor.sender());
            return;
        }

        translator.translate("commands.portal.list.header")
                .withPrefix()
                .replace("portalCount", String.valueOf(portals.size()))
                .send(actor.sender());
        portals.stream()
                .sorted(Comparator.comparing(FPortal::getName, String.CASE_INSENSITIVE_ORDER))
                .forEach(portal -> translator.translate("commands.portal.list.entry")
                        .replace("portalName", portal.getName())
                        .replace("worldName", portal.getWorldName())
                        .replace("destinationWorld", portal.getDestinationWorldName())
                        .send(actor.sender()));
    }

    @Command("portal info")
    @Description("Shows information about a portal")
    @CommandPermission("fancyworlds.commands.portal.info")
    public void info(BukkitCommandActor actor, FPortal portal) {
        PortalPosition minimum = portal.getMinimumPosition();
        PortalPosition maximum = portal.getMaximumPosition();
        translator.translate("commands.portal.info.result")
                .withPrefix()
                .replace("portalName", portal.getName())
                .replace("worldName", portal.getWorldName())
                .replace("destinationWorld", portal.getDestinationWorldName())
                .replace("minimum", formatPosition(minimum))
                .replace("maximum", formatPosition(maximum))
                .replace("volume", String.valueOf(portal.getVolume()))
                .send(actor.sender());
    }

    @Command({"portal delete", "portal remove"})
    @Description("Deletes a portal")
    @CommandPermission("fancyworlds.commands.portal.delete")
    public void delete(BukkitCommandActor actor, FPortal portal) {
        PortalService.get().unregisterPortal(portal);
        translator.translate("commands.portal.delete.success")
                .withPrefix()
                .replace("portalName", portal.getName())
                .send(actor.sender());
    }

    @Command({"portal set_destination", "portal destination"})
    @Description("Changes the destination world of a portal")
    @CommandPermission("fancyworlds.commands.portal.set_destination")
    public void setDestination(BukkitCommandActor actor, FPortal portal, FWorld destinationWorld) {
        portal.setDestinationWorldName(destinationWorld.getName());
        PortalService.get().updatePortal(portal);
        translator.translate("commands.portal.set_destination.success")
                .withPrefix()
                .replace("portalName", portal.getName())
                .replace("destinationWorld", portal.getDestinationWorldName())
                .send(actor.sender());
    }

    private String formatPosition(PortalPosition position) {
        return "%d, %d, %d".formatted(position.x(), position.y(), position.z());
    }
}
