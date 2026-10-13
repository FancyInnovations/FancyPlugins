package com.fancyinnovations.fancyworlds.dialogs;

import com.fancyinnovations.fancydialogs.api.data.DialogBodyData;
import com.fancyinnovations.fancydialogs.api.data.DialogButton;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogInputs;
import com.fancyinnovations.fancydialogs.api.data.inputs.DialogSelect;
import com.fancyinnovations.fancydialogs.api.dialogs.ConfirmationDialog;
import com.fancyinnovations.fancyworlds.dialogs.WorldsDialogController.Choice;
import com.fancyinnovations.fancyworlds.api.portals.FPortal;
import com.fancyinnovations.fancyworlds.api.worlds.FWorld;
import com.fancyinnovations.fancyworlds.main.FancyWorldsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/** Portal browsing and management dialogs. */
final class PortalDialogs {

    private static final int PAGE_SIZE = 8;

    private final FancyWorldsPlugin plugin;
    private final WorldsDialogController controller;

    PortalDialogs(FancyWorldsPlugin plugin, WorldsDialogController controller) {
        this.plugin = plugin;
        this.controller = controller;
    }

    void openPortalList(Player player, int requestedPage) {
        if (!controller.allowed(player, "portal.menu")) return;

        List<FPortal> portals = plugin.getPortalService().getAllPortals().stream()
                .sorted(Comparator.comparing(FPortal::getName, String.CASE_INSENSITIVE_ORDER))
                .toList();

        int pages = Math.max(1, (portals.size() + PAGE_SIZE - 1) / PAGE_SIZE);
        int page = Math.clamp(requestedPage, 1, pages);

        List<DialogBodyData> body = new ArrayList<>();
        body.add(controller.line(controller.tr("portal_list.count", "count", portals.size(), "page", page, "pages", pages)));
        if (portals.isEmpty()) {
            body.add(controller.line(controller.tr("portal_list.empty")));
        }

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        for (FPortal portal : portals.subList((page - 1) * PAGE_SIZE, Math.min(page * PAGE_SIZE, portals.size()))) {
            String label = controller.tr("portal_list.entry", "name", portal.getName(), "source", portal.getWorldName(), "destination", portal.getDestinationWorldName());

            if (controller.allowed(player, "portal.info")) {
                controller.add(buttons, choices, label, "portal", portal.getID().toString(), page);
            } else {
                body.add(controller.line(label));
            }
        }
        if (page > 1) {
            controller.add(buttons, choices, controller.tr("common.previous"), "portal_page", String.valueOf(page - 1), page);
        }
        if (page < pages) {
            controller.add(buttons, choices, controller.tr("common.next"), "portal_page", String.valueOf(page + 1), page);
        }

        controller.show(player, controller.tr("portal_list.title"), body, DialogInputs.EMPTY, buttons, choices);
    }

    void openPortalDetail(Player player, String id, int page) {
        if (!controller.allowed(player, "portal.menu") || !controller.allowed(player, "portal.info")) return;

        FPortal portal = plugin.getPortalService().getPortalByID(id);
        if (portal == null) {
            controller.send(player, "common.portal_not_found", "portalName", id);
            openPortalList(player, page);
            return;
        }

        List<DialogBodyData> body = List.of(
                controller.line(controller.tr("portal_detail.source", "value", portal.getWorldName())),
                controller.line(controller.tr("portal_detail.bounds", "min", controller.position(portal.getMinimumPosition()), "max", controller.position(portal.getMaximumPosition()))),
                controller.line(controller.tr("portal_detail.volume", "value", portal.getVolume())),
                controller.line(controller.tr("portal_detail.destination", "value", portal.getDestinationWorldName()))
        );

        List<DialogButton> buttons = new ArrayList<>();
        Map<String, Choice> choices = new HashMap<>();
        DialogInputs inputs = DialogInputs.EMPTY;
        if (controller.allowed(player, "portal.set_destination")) {
            List<DialogSelect.Entry> destinations = plugin.getWorldService().getAllWorlds().stream()
                    .sorted(Comparator.comparing(FWorld::getName, String.CASE_INSENSITIVE_ORDER))
                    .map(world -> new DialogSelect.Entry(world.getName(), world.getName(), world.getName().equals(portal.getDestinationWorldName())))
                    .toList();

            if (!destinations.isEmpty()) {
                inputs = new DialogInputs(List.of(), List.of(new DialogSelect("destination", controller.tr("portal_detail.choose_destination"), 1, destinations, Map.of(), 220)), List.of());
                controller.add(buttons, choices, controller.tr("portal_detail.save_destination"), "portal_destination", id, page);
            }
        }

        if (controller.allowed(player, "portal.delete")) {
            controller.add(buttons, choices, controller.tr("portal_detail.delete"), "portal_delete", id, page);
        }

        controller.add(buttons, choices, controller.tr("common.back"), "portal_page", String.valueOf(page), page);

        controller.show(player, controller.tr("portal_detail.title", "name", portal.getName()), body, inputs, buttons, choices);
    }

    void portalAction(Player player, Choice choice, Map<String, String> inputs) {
        if (!controller.allowed(player, "portal.menu")) {
            controller.send(player, "dialogs.common.action_unavailable");
            controller.discard(player);
            return;
        }

        FPortal portal = plugin.getPortalService().getPortalByID(choice.target());
        if (portal == null) {
            controller.send(player, "common.portal_not_found", "portalName", choice.target());
            openPortalList(player, choice.page());
            return;
        }

        if (choice.action().equals("portal_destination")) {
            if (!controller.allowed(player, "portal.set_destination")) {
                portalUnavailable(player, choice);
                return;
            }

            String destination = inputs.get("destination");
            FWorld target = destination == null ? null : plugin.getWorldService().getWorldByName(destination);
            if (target == null) {
                controller.send(player, "dialogs.portal_detail.invalid_destination");
                openPortalDetail(player, choice.target(), choice.page());
                return;
            }

            portal.setDestinationWorldName(target.getName());

            plugin.getPortalService().updatePortal(portal);
            controller.send(player, "commands.portal.set_destination.success", "portalName", portal.getName(), "destinationWorld", target.getName());

            openPortalDetail(player, choice.target(), choice.page());
        } else if (choice.action().equals("portal_delete")) {
            if (!controller.allowed(player, "portal.delete")) {
                portalUnavailable(player, choice);
                return;
            }

            controller.discard(player);

            new ConfirmationDialog(controller.tr("portal_detail.delete_question", "name", portal.getName()))
                    .withTitle(controller.tr("portal_detail.delete"))
                    .withOnConfirm(() -> Bukkit.getScheduler().runTask(plugin, () -> {
                        if (!player.isOnline()) return;

                        if (!controller.allowed(player, "portal.delete")) {
                            controller.send(player, "dialogs.common.action_unavailable");
                            openPortalList(player, choice.page());
                            return;
                        }

                        FPortal current = plugin.getPortalService().getPortalByID(choice.target());
                        if (current != null) {
                            plugin.getPortalService().unregisterPortal(current);
                            controller.send(player, "commands.portal.delete.success", "portalName", current.getName());
                        }

                        openPortalList(player, choice.page());
                    }))
                    .withOnCancel(() -> Bukkit.getScheduler().runTask(plugin, () -> openPortalDetail(player, choice.target(), choice.page())))
                    .ask(player);
        }
    }

    private void portalUnavailable(Player player, Choice choice) {
        controller.send(player, "dialogs.common.action_unavailable");
        openPortalDetail(player, choice.target(), choice.page());
    }
}
