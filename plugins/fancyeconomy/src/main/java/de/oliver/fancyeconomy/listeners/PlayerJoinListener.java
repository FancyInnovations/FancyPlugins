package de.oliver.fancyeconomy.listeners;

import de.oliver.fancyeconomy.FancyEconomy;
import de.oliver.fancyeconomy.currencies.CurrencyPlayer;
import de.oliver.fancyeconomy.currencies.CurrencyPlayerManager;
import de.oliver.fancylib.MessageHelper;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        if (event.getPlayer().hasPermission("FancyEconomy.admin.notification")) {
            FancyEconomy.getInstance().getScheduler().runTaskAsynchronously(() -> {
                ComparableVersion newestVersion = FancyEconomy.getInstance().getVersionFetcher().fetchNewestVersion();
                ComparableVersion currentVersion = new ComparableVersion(FancyEconomy.getInstance().getDescription().getVersion());
                if (newestVersion != null && newestVersion.compareTo(currentVersion) > 0) {
                    MessageHelper.warning(event.getPlayer(), "You are using an outdated version of the FancyEconomy Plugin");
                    MessageHelper.warning(event.getPlayer(), "[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + FancyEconomy.getInstance().getVersionFetcher().getDownloadUrl() + "'><u>click here</u></click>");
                }
            });
        }

        // load currency player
        CurrencyPlayer currencyPlayer = CurrencyPlayerManager.getPlayer(p.getUniqueId());
        currencyPlayer.setUsername(p.getName());
    }
}
