package de.oliver.fancynpcs.listeners;

import com.plotsquared.core.PlotSquared;
import com.plotsquared.core.location.Location;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotArea;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcCreateEvent;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Comparator;
import java.util.Map;

public class PlayerNpcsListener implements Listener {

    private static final boolean isUsingPlotSquared = FancyNpcs.getInstance().isUsingPlotSquared();
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    /**
     * Checks if the player is the owner of the plot where the NPC is located.
     *
     * @return true if the player is the owner of the plot or if PlotSquared is not used, false otherwise.
     */
    public static boolean checkNpcOwnership(Player player, org.bukkit.Location loc) {
        if (!isUsingPlotSquared) {
            return true;
        }

        if (loc == null || player == null) {
            return false;
        }

        if (player.hasPermission("fancynpcs.admin")) {
            return true;
        }

        Location npcLoc = Location.at(
                loc.getWorld().getName(),
                loc.getBlockX(),
                loc.getBlockY(),
                loc.getBlockZ()
        );

        PlotArea plotArea = PlotSquared.platform()
                .plotAreaManager()
                .getPlotArea(npcLoc);

        if (plotArea == null) {
            return false;
        }

        Plot plot = plotArea.getOwnedPlot(npcLoc);

        return plot != null && plot.isOwner(player.getUniqueId());
    }

    @EventHandler
    public void onNpcCreate(NpcCreateEvent event) {
        if (!(event.getCreator() instanceof Player player)) {
            return;
        }

        boolean isOwner = checkNpcOwnership(player, event.getNpc().getData().getLocation());
        if (!isOwner) {
            translator.translate("player_npcs_create_failure_not_owned_plot").send(player);
            event.setCancelled(true);
            return;
        }

        int maxNpcs = FancyNpcs.getInstance().getFancyNpcConfig().getMaxNpcsPerPermission()
                .entrySet().stream()
                .filter(entry -> player.hasPermission(entry.getKey()))
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getValue)
                .orElse(Integer.MAX_VALUE);

        int npcAmount = 0;
        for (Npc npc : FancyNpcs.getInstance().getNpcManager().getAllNpcs()) {
            if (npc.getData().getCreator().equals(player.getUniqueId()))
                npcAmount++;
        }
        if (npcAmount >= maxNpcs && !player.hasPermission("fancynpcs.admin")) {
            translator.translate("player_npcs_create_failure_limit_reached").send(player);
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onNpcRemove(NpcRemoveEvent event) {
        if (!(event.getSender() instanceof Player player)) {
            FancyNpcs.getInstance().getFancyLogger().warn("NpcRemoveEvent sender is not a Player!");
            return;
        }

        if (!event.getNpc().getData().getCreator().equals(player.getUniqueId()) && !player.hasPermission("fancynpcs.admin")) {
            translator.translate("player_npcs_cannot_modify_npc").send(player);
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onNpcModify(NpcModifyEvent event) {
        if (!(event.getModifier() instanceof Player player)) {
            FancyNpcs.getInstance().getFancyLogger().warn("NpcModifyEvent modifier is not a Player!");
            return;
        }

        if (!(event.getNewValue() instanceof org.bukkit.Location location)) {
            FancyNpcs.getInstance().getFancyLogger().warn("NpcModifyEvent newValue is not a Location!");
            return;
        }

        boolean isOwner = checkNpcOwnership(player, location);
        if (!isOwner) {
            translator.translate("player_npcs_cannot_move_npc").send(player);
            event.setCancelled(true);
        }
    }
}
