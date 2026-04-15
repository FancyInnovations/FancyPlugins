package de.oliver.fancyeconomy;

import com.fancyinnovations.config.ConfigHelper;
import de.oliver.fancyeconomy.currencies.Currency;
import de.oliver.fancyeconomy.currencies.CurrencyRegistry;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.databases.MySqlDatabase;
import de.oliver.fancylib.databases.SqliteDatabase;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;


public class FancyEconomyConfig {

    private DatabaseType dbType;
    private String dbHost;
    private String dbPort;
    private String dbDatabase;
    private String dbUsername;
    private String dbPassword;
    private String dbFile;

    private boolean useShortFormat;
    private double minWithdrawAmount;
    private double maxWithdrawAmount;
    private boolean allowNegativeBalance;
    private double maxNegativeBalance;

    public void reload() {
        FancyEconomy.getInstance().reloadConfig();
        FileConfiguration config = FancyEconomy.getInstance().getConfig();

        /*
            Database
         */
        dbType = DatabaseType.getByIdentifier((String) ConfigHelper.getOrDefault(config, "database.type", "sqlite"));
        if (dbType == null) {
            FancyEconomy.getInstance().getLogger().warning("Invalid database type provided in config");
        }

        dbHost = (String) ConfigHelper.getOrDefault(config, "database.mysql.host", "localhost");
        dbPort = (String) ConfigHelper.getOrDefault(config, "database.mysql.port", "3306");
        dbDatabase = (String) ConfigHelper.getOrDefault(config, "database.mysql.database", "fancyeconomy");
        dbUsername = (String) ConfigHelper.getOrDefault(config, "database.mysql.username", "root");
        dbPassword = (String) ConfigHelper.getOrDefault(config, "database.mysql.password", "");
        dbFile = (String) ConfigHelper.getOrDefault(config, "database.sqlite.file_path", "database.db");
        dbFile = "plugins/FancyEconomy/" + dbFile;


        /*
            Currencies
         */
        useShortFormat = (boolean) ConfigHelper.getOrDefault(config, "use_short_format", false);
        minWithdrawAmount = (Double) ConfigHelper.getOrDefault(config, "minimum_withdraw_amount", 0.1d);
        maxWithdrawAmount = (Double) ConfigHelper.getOrDefault(config, "maximum_withdraw_amount", 1_000_000_000d);
        allowNegativeBalance = (boolean) ConfigHelper.getOrDefault(config, "allow_negative_balance", false);
        maxNegativeBalance = (Double) ConfigHelper.getOrDefault(config, "maximum_negative_balance", -10_000d);
        config.setInlineComments("maximum_negative_balance", List.of("set to '0' to remove the limit"));

        String defaultCurrencyName = (String) ConfigHelper.getOrDefault(config, "default_currency", "money");

        if (!config.isConfigurationSection("currencies")) {
            config.set("currencies.money.symbol", "$");
        }

        CurrencyRegistry.CURRENCIES.clear();
        for (String name : config.getConfigurationSection("currencies").getKeys(false)) {
            String symbol = (String) ConfigHelper.getOrDefault(config, "currencies." + name + ".symbol", "$");

            boolean isWithdrawable = (boolean) ConfigHelper.getOrDefault(config, "currencies." + name + ".is_withdrawable", true);

            Material material = Material.getMaterial((String) ConfigHelper.getOrDefault(config, "currencies." + name + ".withdraw_item.material", "PAPER"));
            String displayName = (String) ConfigHelper.getOrDefault(config, "currencies." + name + ".withdraw_item.display_name", "<aqua><b>Money Note</b></aqua> <gray>(Click)</gray>");
            List<String> lore = (List<String>) ConfigHelper.getOrDefault(config, "currencies." + name + ".withdraw_item.lore", Arrays.asList("<dark_aqua><b>*</b> <aqua>Vaule: <white>%amount%", " <dark_aqua><b>*</b> <aqua>Signer: <white>%player%", "", "<yellow>Right Click to redeem %currency%"));

            Currency currency = new Currency(name, symbol, isWithdrawable, new Currency.WithdrawItem(material, displayName, lore));
            CurrencyRegistry.registerCurrency(currency);
        }

        Currency defaultCurrency = CurrencyRegistry.getCurrencyByName(defaultCurrencyName);
        if (defaultCurrency == null) {
            FancyEconomy.getInstance().getLogger().warning("Could not find default currency: '" + defaultCurrencyName + "'");
        } else {
            CurrencyRegistry.setDefaultCurrency(defaultCurrency);
            FancyEconomy.getInstance().getLogger().info("Set default currency to: '" + defaultCurrency.name() + "'");
        }

        FancyEconomy.getInstance().saveConfig();
    }

    public boolean useShortFormat() {
        return useShortFormat;
    }

    public double getMinWithdrawAmount() {
        return minWithdrawAmount;
    }

    public double getMaxWithdrawAmount() {
        return maxWithdrawAmount;
    }

    public boolean allowNegativeBalance() {
        return allowNegativeBalance;
    }

    public double getMaxNegativeBalance() {
        return maxNegativeBalance;
    }

    public Database getDatabase() {
        if (dbType == null) {
            return null;
        }

        Database db = null;

        switch (dbType) {
            case MYSQL -> db = new MySqlDatabase(dbHost, dbPort, dbDatabase, dbUsername, dbPassword);
            case SQLITE -> db = new SqliteDatabase(dbFile);
        }

        return db;
    }

    enum DatabaseType {
        MYSQL("mysql"),
        SQLITE("sqlite");

        private final String identifier;

        DatabaseType(String identifier) {
            this.identifier = identifier;
        }

        public static DatabaseType getByIdentifier(String identifier) {
            for (DatabaseType type : values()) {
                if (type.getIdentifier().equalsIgnoreCase(identifier)) {
                    return type;
                }
            }

            return null;
        }

        public String getIdentifier() {
            return identifier;
        }
    }
}
