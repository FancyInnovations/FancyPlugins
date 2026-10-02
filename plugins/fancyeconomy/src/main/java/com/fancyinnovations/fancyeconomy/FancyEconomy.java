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
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.BukkitLamp;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.orphan.Orphans;
import net.milkbowl.vault.economy.Economy;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
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

        usingVault = getServer().getPluginManager().getPlugin("Vault") != null;
        if (usingVault) {
            vaultEconomy = new FancyEconomyVault(CurrencyRegistry.getDefaultCurrency());
            getServer().getServicesManager().register(Economy.class, vaultEconomy, instance, ServicePriority.Highest);
            getLogger().info("Registered Vault economy");
        }
    }

    @Override
    public void onEnable() {
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

        registerCommands();

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
        for (CurrencyPlayer player : CurrencyPlayerManager.getAllPlayers()) {
            player.save(true);
        }

        if (database != null) {
            database.close();
        }
    }

    private void registerCommands() {
        Lamp.Builder<BukkitCommandActor> builder = BukkitLamp.builder(this);
        builder.permissionFactory((annotations, lamp) -> {
            CurrencyPermission marker = annotations.get(CurrencyPermission.class);
            Command command = annotations.get(Command.class);
            if (marker == null || command == null) {
                return null;
            }

            String currencyName = command.value()[0];
            String permission = "fancyeconomy." + currencyName + (marker.admin() ? ".admin" : "");
            return actor -> actor.sender().hasPermission(permission);
        });

        Lamp<BukkitCommandActor> lamp = builder.build();
        lamp.register(new FancyEconomyCMD(), new PayCMD(), new BalanceCMD(), new WithdrawCMD(), new BalanceTopCMD());

        for (Currency currency : CurrencyRegistry.CURRENCIES) {
            lamp.register(Orphans.path(currency.name()).handler(new CurrencyBaseCMD(currency)));
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
