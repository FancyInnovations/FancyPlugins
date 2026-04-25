package com.fancyinnovations.fancyeconomy;

import com.fancyinnovations.config.ConfigHelper;
import com.fancyinnovations.fancyeconomy.currencies.Currency;
import com.fancyinnovations.fancyeconomy.currencies.CurrencyRegistry;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.databases.MySqlDatabase;
import de.oliver.fancylib.databases.PostgreSqlDatabase;
import de.oliver.fancylib.databases.SqliteDatabase;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;


public class FancyEconomyConfig {

    private DatabaseType dbType;
    private String mysqlHost;
    private String mysqlPort;
    private String mysqlDatabase;
    private String mysqlUsername;
    private String mysqlPassword;
    private String postgresqlHost;
    private String postgresqlPort;
    private String postgresqlDatabase;
    private String postgresqlUsername;
    private String postgresqlPassword;
    private String sqliteFile;

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

        mysqlHost = (String) ConfigHelper.getOrDefault(config, "database.mysql.host", "localhost");
        mysqlPort = (String) ConfigHelper.getOrDefault(config, "database.mysql.port", "3306");
        mysqlDatabase = (String) ConfigHelper.getOrDefault(config, "database.mysql.database", "fancyeconomy");
        mysqlUsername = (String) ConfigHelper.getOrDefault(config, "database.mysql.username", "root");
        mysqlPassword = (String) ConfigHelper.getOrDefault(config, "database.mysql.password", "");

        postgresqlHost = (String) ConfigHelper.getOrDefault(config, "database.postgresql.host", "localhost");
        postgresqlPort = (String) ConfigHelper.getOrDefault(config, "database.postgresql.port", "5432");
        postgresqlDatabase = (String) ConfigHelper.getOrDefault(config, "database.postgresql.database", "fancyeconomy");
        postgresqlUsername = (String) ConfigHelper.getOrDefault(config, "database.postgresql.username", "root");
        postgresqlPassword = (String) ConfigHelper.getOrDefault(config, "database.postgresql.password", "");

        sqliteFile = (String) ConfigHelper.getOrDefault(config, "database.sqlite.file_path", "database.db");
        sqliteFile = "plugins/FancyEconomy/" + sqliteFile;


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
            case MYSQL -> db = new MySqlDatabase(mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword);
            case POSTGRESQL ->
                    db = new PostgreSqlDatabase(postgresqlHost, postgresqlPort, postgresqlDatabase, postgresqlUsername, postgresqlPassword);
            case SQLITE -> db = new SqliteDatabase(sqliteFile);
        }

        return db;
    }

    enum DatabaseType {
        MYSQL("mysql"),
        POSTGRESQL("postgresql"),
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
