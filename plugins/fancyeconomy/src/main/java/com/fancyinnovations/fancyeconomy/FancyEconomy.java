package com.fancyinnovations.fancyeconomy;

import com.fancyinnovations.fancyeconomy.commands.*;
import com.fancyinnovations.fancyeconomy.currencies.*;
import com.fancyinnovations.fancyeconomy.integrations.FancyEconomyPlaceholderExpansion;
import com.fancyinnovations.fancyeconomy.integrations.FancyEconomyVault;
import com.fancyinnovations.fancyeconomy.listeners.PlayerJoinListener;
import com.fancyinnovations.fancyeconomy.utils.DistributedWorkload;
import de.oliver.fancylib.*;
import de.oliver.fancylib.databases.Database;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FoliaScheduler;
import de.oliver.fancylib.translations.Language;
import de.oliver.fancylib.translations.TextConfig;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancylib.versionFetcher.MasterVersionFetcher;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandAPIPaperConfig;
import dev.jorel.commandapi.arguments.*;
import net.milkbowl.vault.economy.Economy;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;


public class FancyEconomy extends JavaPlugin {

    private static FancyEconomy instance;
    private final FancyScheduler scheduler;
    private final VersionFetcher versionFetcher;
    private final FancyEconomyConfig config;
    private Translator translator;
    private FancyEconomyVault vaultEconomy;
    private Database database;
    private DistributedWorkload<CurrencyPlayer> saveWorkload;
    private boolean usingVault;
    private boolean usingPlaceholderAPI;

    public FancyEconomy() {
        instance = this;
        this.scheduler = ServerSoftware.isFolia()
                ? new FoliaScheduler(instance)
                : new BukkitScheduler(instance);
        config = new FancyEconomyConfig();
        versionFetcher = new MasterVersionFetcher("FancyEconomy");
        saveWorkload = new DistributedWorkload<>(
                "FancyEconomy_save",
                player -> player.save(false),
                player -> false,
                5,
                true
        );
    }

    public static FancyEconomy getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        config.reload();

        CommandAPI.onLoad(new CommandAPIPaperConfig(instance).silentLogs(true));
        registerCommands();

        usingVault = getServer().getPluginManager().getPlugin("Vault") != null;
        if (usingVault) {
            vaultEconomy = new FancyEconomyVault(CurrencyRegistry.getDefaultCurrency());
            getServer().getServicesManager().register(Economy.class, vaultEconomy, instance, ServicePriority.Highest);
            getLogger().info("Registered Vault economy");
        }
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();
        new FancyLib(this);

        scheduler.runTaskAsynchronously(() -> {
            ComparableVersion newestVersion = versionFetcher.fetchNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if (newestVersion == null) {
                getLogger().warning("Could not fetch latest plugin version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyEconomy plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(versionFetcher.getDownloadUrl());
                getLogger().warning("-------------------------------------------------------");
            }
        });

        if (!ServerSoftware.isPaper() && !ServerSoftware.isFolia()) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("Plugin support Paper and Folia.");
            getLogger().warning("Because you are using Bukkit or Spigot,");
            getLogger().warning("the plugin might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        Metrics metrics = new Metrics(instance, 18569);

        TextConfig textConfig = new TextConfig("#E3CA66", "#35ad1d", "#81E366", "#E3CA66", "#E36666", "");
        translator = new Translator(textConfig);

        translator.loadLanguages(getDataFolder().getAbsolutePath());
        Language selectedLanguage = translator.getLanguages().stream()
                .filter(language -> language.getLanguageCode().equalsIgnoreCase(config.getLanguage())
                        || language.getLanguageName().equalsIgnoreCase(config.getLanguage()))
                .findFirst()
                .orElse(translator.getFallbackLanguage());
        translator.setSelectedLanguage(selectedLanguage);

        database = config.getDatabase();
        if (database == null) {
            getLogger().severe("Unsupported database type");
        }
        database.connect();
        createDatabaseTables();

        CurrencyPlayerManager.loadPlayersFromDatabase();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(), instance);
        Currency.WithdrawItem.WithdrawItemClick.INSTANCE.register();

        usingPlaceholderAPI = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
        if (usingPlaceholderAPI) {
            new FancyEconomyPlaceholderExpansion().register();
            getLogger().info("Registered PlaceholoderAPI expansion");
        }

        scheduler.runTaskTimerAsynchronously(60, 60 * 5, saveWorkload);

        scheduler.runTaskTimerAsynchronously(60, 60 * 5, BalanceTop::refreshAll);
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();

        for (CurrencyPlayer player : CurrencyPlayerManager.getAllPlayers()) {
            player.save(true);
        }

        if (database != null) {
            database.close();
        }
    }

    private void registerCommands() {
        CommandAPI.registerCommand(FancyEconomyCMD.class);
        CommandAPI.registerCommand(PayCMD.class);
        CommandAPI.registerCommand(BalanceCMD.class);
        CommandAPI.registerCommand(WithdrawCMD.class);
        CommandAPI.registerCommand(BalanceTopCMD.class);

        ArgumentSuggestions<CommandSender> allPlayersSuggestion = ArgumentSuggestions.strings(commandSenderSuggestionInfo -> CurrencyPlayerManager.getAllPlayerNames());


        for (Currency currency : CurrencyRegistry.CURRENCIES) {
            CurrencyBaseCMD baseCMD = new CurrencyBaseCMD(currency);

            // info command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .executesPlayer((sender, args) -> {
                        baseCMD.info(sender);
                    })
                    .register();

            // balance command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new LiteralArgument("balance").setListed(false)
                    )
                    .executesPlayer((sender, args) -> {
                        baseCMD.balance(sender);
                    })
                    .register();

            // balance others command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new LiteralArgument("balance").setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion))
                    .executesPlayer((sender, args) -> {
                        baseCMD.balance(sender, (String) args.get(0));
                    })
                    .register();

            // pay command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new LiteralArgument("pay").setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.pay(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();

            // withdraw command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new LiteralArgument("withdraw").setListed(false)
                    )
                    .withArguments(new DoubleArgument("amount"))
                    .executesPlayer((sender, args) -> {
                        baseCMD.withdraw(sender, (Double) args.get(0));
                    })
                    .register();

            // balancetop command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new LiteralArgument("top").setListed(false)
                    )
                    .executesPlayer((sender, args) -> {
                        baseCMD.balancetop(sender);
                    })
                    .register();

            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name())
                    .withArguments(
                            new LiteralArgument("top").setListed(false)
                    )
                    .withArguments(new IntegerArgument("page", 1))
                    .executesPlayer((sender, args) -> {
                        baseCMD.balancetop(sender, (Integer) args.get(0));
                    })
                    .register();

            // set command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name() + ".admin")
                    .withArguments(
                            new LiteralArgument("set").setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.set(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();

            // add command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name() + ".admin")
                    .withArguments(
                            new LiteralArgument("add").setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.add(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();

            // remove command
            new CommandAPICommand(currency.name())
                    .withPermission("fancyeconomy." + currency.name() + ".admin")
                    .withArguments(
                            new LiteralArgument("remove").setListed(false)
                    )
                    .withArguments(new StringArgument("targetName").includeSuggestions(allPlayersSuggestion), new DoubleArgument("amount", 0.01))
                    .executesPlayer((sender, args) -> {
                        baseCMD.remove(sender, (String) args.get(0), (Double) args.get(1));
                    })
                    .register();
        }
    }

    private void createDatabaseTables() {
        database.executeNonQuery("""
                CREATE TABLE IF NOT EXISTS players(
                    uuid VARCHAR(255) PRIMARY KEY,
                    username VARCHAR(255)
                )""");

        database.executeNonQuery("""
                CREATE TABLE IF NOT EXISTS balances(
                    uuid VARCHAR(255),
                    currency VARCHAR(255),
                    balance DOUBLE,
                
                    PRIMARY KEY(uuid, currency),
                    FOREIGN KEY (uuid) REFERENCES players(uuid)
                )""");
    }

    public FancyScheduler getScheduler() {
        return scheduler;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public Translator getTranslator() {
        return translator;
    }

    public FancyEconomyConfig getFancyEconomyConfig() {
        return config;
    }

    public Database getDatabase() {
        return database;
    }

    public DistributedWorkload<CurrencyPlayer> getSaveWorkload() {
        return saveWorkload;
    }

    public boolean isUsingVault() {
        return usingVault;
    }

    public boolean isUsingPlaceholderAPI() {
        return usingPlaceholderAPI;
    }
}
