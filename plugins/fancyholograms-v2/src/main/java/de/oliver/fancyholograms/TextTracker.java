package de.oliver.fancyholograms;

import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancyholograms.hologram.version.HologramImpl;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TextTracker implements Runnable {

    private static final double MIN_DISTANCE = 7d;

    @Override
    public void run() {

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location playerLocation = player.getLocation();

            for (Hologram hologram : FancyHolograms.get().getHologramManager().getHolograms()) {
                Location hologramLoc = hologram.getData().getLocation();

                if (hologramLoc == null || !hologramLoc.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName())) {
                    continue;
                }

                double distance = playerLocation.distance(hologramLoc);
                if (Double.isNaN(distance)) {
                    continue;
                }

                HologramImpl impl = (HologramImpl) hologram;
                impl.viewerPrivacy.put(player.getUniqueId(), distance < MIN_DISTANCE);
            }
        }

    }
}
