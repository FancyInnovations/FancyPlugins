package com.fancyinnovations.fancyeconomy.commands;

import com.fancyinnovations.fancyeconomy.FancyEconomy;
import com.fancyinnovations.fancyeconomy.currencies.Currency;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyPlayerManager;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyRegistry;
import de.oliver.fancylib.MessageHelper;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.annotation.CommandPermission;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.command.CommandSender;

public class FancyEconomyCMD {

    @Command("fancyeconomy")
    @CommandPermission("fancyeconomy.admin")
    public void info(CommandSender player) {
        MessageHelper.info(player, " --- FancyEconomy Info ---");
        MessageHelper.info(player, "/FancyEconomy reload - plugin config reload");
        MessageHelper.info(player, "/FancyEconomy version - checks for a new version of the plugin");
        MessageHelper.info(player, "/FancyEconomy currencies - shows a list of all currencies");
    }

    @Command("fancyeconomy version")
    @CommandPermission("fancyeconomy.admin")
    public void version(CommandSender player) {
        MessageHelper.info(player, "<i>Checking version, please wait...</i>");
        FancyEconomy.getInstance().getScheduler().runTaskAsynchronously(() -> {
            ComparableVersion newestVersion = FancyEconomy.getInstance().getVersionFetcher().fetchNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(FancyEconomy.getInstance().getDescription().getVersion());
            if (newestVersion == null) {
                MessageHelper.error(player, "Could not find latest version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                MessageHelper.warning(player, "You are using an outdated version of the FancyEconomy Plugin");
                MessageHelper.warning(player, "[!] Please download the newest version (" + newestVersion + "): <click:open_url:'" + FancyEconomy.getInstance().getVersionFetcher().getDownloadUrl() + "'><u>click here</u></click>");
            } else {
                MessageHelper.success(player, "You are using the latest version of the FancyEconomy Plugin (" + currentVersion + ")");
            }
        });
    }

    @Command("fancyeconomy reload")
    @CommandPermission("fancyeconomy.admin")
    public void reload(CommandSender player) {
        //FancyEconomy.getInstance().getTranslator().loadLanguages(); // TODO: Reload translations
        FancyEconomy.getInstance().getFancyEconomyConfig().reload();
        CurrencyPlayerManager.loadPlayersFromDatabase();
        FancyEconomy.getInstance().getTranslator()
                .translate("reloaded-config")
                .send(player);
    }

    @Command("fancyeconomy currencies")
    @CommandPermission("fancyeconomy.admin")
    public void currencies(CommandSender player) {
        Currency defaultCurrency = CurrencyRegistry.getDefaultCurrency();
        FancyEconomy.getInstance().getTranslator()
                .translate("currency-list")
                .send(player);
        for (Currency currency : CurrencyRegistry.CURRENCIES) {
            MessageHelper.info(player, " - " + currency.name() + " (" + currency.symbol() + ")" + (currency == defaultCurrency ? " <gray>[default]" : ""));
        }
    }
}
