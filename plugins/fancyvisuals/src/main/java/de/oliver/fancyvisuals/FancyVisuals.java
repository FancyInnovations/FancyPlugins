package de.oliver.fancyvisuals;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancysitula.api.IFancySitula;
import de.oliver.fancyvisuals.analytics.AnalyticsManager;
import de.oliver.fancyvisuals.api.FancyVisualsAPI;
import de.oliver.fancyvisuals.api.nametags.NametagRepository;
import de.oliver.fancyvisuals.actionbar.ActionBarListeners;
import de.oliver.fancyvisuals.actionbar.ActionBarManager;
import de.oliver.fancyvisuals.actionbar.ActionBarRepository;
import de.oliver.fancyvisuals.actionbar.JsonActionBarRepository;
import de.oliver.fancyvisuals.bossbars.BossbarListeners;
import de.oliver.fancyvisuals.bossbars.BossbarManager;
import de.oliver.fancyvisuals.bossbars.BossbarRepository;
import de.oliver.fancyvisuals.bossbars.JsonBossbarRepository;
import de.oliver.fancyvisuals.chat.ChatFormatRepository;
import de.oliver.fancyvisuals.chat.ChatListeners;
import de.oliver.fancyvisuals.chat.JsonChatFormatRepository;
import de.oliver.fancyvisuals.commands.FancyVisualsCMD;
import de.oliver.fancyvisuals.config.FancyVisualsConfig;
import de.oliver.fancyvisuals.config.NametagConfig;
import de.oliver.fancyvisuals.nametags.NametagManager;
import de.oliver.fancyvisuals.nametags.listeners.NametagListeners;
import de.oliver.fancyvisuals.nametags.store.JsonNametagRepository;
import de.oliver.fancyvisuals.nametags.visibility.PlayerNametagScheduler;
import de.oliver.fancyvisuals.playerConfig.JsonPlayerConfigStore;
import de.oliver.fancyvisuals.tablist.JsonTablistRepository;
import de.oliver.fancyvisuals.tablist.TablistListeners;
import de.oliver.fancyvisuals.tablist.TablistManager;
import de.oliver.fancyvisuals.tablist.TablistRepository;
import de.oliver.fancyvisuals.titles.JsonTitleRepository;
import de.oliver.fancyvisuals.titles.TitleListeners;
import de.oliver.fancyvisuals.titles.TitleManager;
import de.oliver.fancyvisuals.titles.TitleRepository;
import de.oliver.fancyvisuals.utils.VaultHelper;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FancyVisuals extends JavaPlugin implements FancyVisualsAPI {

    private static final ExtendedFancyLogger logger = IFancySitula.LOGGER;
    private static FancyVisuals instance;
    private final AnalyticsManager analyticsManager;
    private final FancyVisualsConfig fancyVisualsConfig;
    private final NametagConfig nametagConfig;
    private ExecutorService workerExecutor;

    private JsonPlayerConfigStore playerConfigStore;

    private NametagRepository nametagRepository;
    private PlayerNametagScheduler nametagScheduler;
    private NametagManager nametagManager;
    private TablistRepository tablistRepository;
    private TablistManager tablistManager;
    private ActionBarRepository actionBarRepository;
    private ActionBarManager actionBarManager;
    private BossbarRepository bossbarRepository;
    private BossbarManager bossbarManager;
    private TitleRepository titleRepository;
    private TitleManager titleManager;
    private ChatFormatRepository chatFormatRepository;

    public FancyVisuals() {
        instance = this;
        this.analyticsManager = new AnalyticsManager("981ce185-c961-4618-bf61-71a8ed6c3962", "SxIBSDA2MDVkMGUwOTk3MzQ3NjCmP0UU");
        this.fancyVisualsConfig = new FancyVisualsConfig();
        this.nametagConfig = new NametagConfig();
    }

    public static FancyVisuals get() {
        return instance;
    }

    public static @NotNull ExtendedFancyLogger getFancyLogger() {
        return logger;
    }

    @Override
    public void onLoad() {
        FancyLib fancyLib = new FancyLib(this);
        IFancySitula.LOGGER.setCurrentLevel(LogLevel.INFO);

        // config
        fancyVisualsConfig.load();
        nametagConfig.load();
        IFancySitula.LOGGER.setCurrentLevel(parseLogLevel(fancyVisualsConfig.getLogLevel()));

        // worker executor
        this.workerExecutor = Executors.newFixedThreadPool(
                fancyVisualsConfig.getAmountWorkerThreads(),
                new ThreadFactoryBuilder()
                        .setNameFormat("FancyVisualsWorker-%d")
                        .build()
        );


        // Player config
        playerConfigStore = new JsonPlayerConfigStore();

        // Nametags
        nametagRepository = new JsonNametagRepository();
        nametagScheduler = new PlayerNametagScheduler(
                workerExecutor,
                nametagConfig.getDistributionBucketSize(),
                nametagConfig.getUpdateIntervalMs()
        );
        nametagManager = new NametagManager(nametagRepository, nametagScheduler);

        // Tablist
        tablistRepository = new JsonTablistRepository();
        tablistManager = new TablistManager(this, tablistRepository);

        // Action bar
        actionBarRepository = new JsonActionBarRepository();
        actionBarManager = new ActionBarManager(this, actionBarRepository);

        // Bossbars
        bossbarRepository = new JsonBossbarRepository();
        bossbarManager = new BossbarManager(this, bossbarRepository);

        // Titles
        titleRepository = new JsonTitleRepository();
        titleManager = new TitleManager(this, titleRepository);

        // Chat
        chatFormatRepository = new JsonChatFormatRepository();

        // analytics
        analyticsManager.init();
    }

    @Override
    public void onEnable() {
        PluginManager pluginManager = Bukkit.getPluginManager();

        // Vault
        VaultHelper.loadVault();

        registerCommands();

        pluginManager.registerEvents(new NametagListeners(), this);
        pluginManager.registerEvents(new TablistListeners(), this);
        pluginManager.registerEvents(new ActionBarListeners(), this);
        pluginManager.registerEvents(new BossbarListeners(), this);
        pluginManager.registerEvents(new TitleListeners(), this);
        pluginManager.registerEvents(new ChatListeners(), this);

        // Nametags
        if (nametagConfig.isEnabled()) {
            nametagScheduler.init();
            Bukkit.getOnlinePlayers().forEach(nametagManager::handlePlayerUpdate);
        }

        if (fancyVisualsConfig.isTablistEnabled()) {
            tablistManager.init();
            Bukkit.getOnlinePlayers().forEach(tablistManager::handleJoin);
        }

        if (fancyVisualsConfig.isActionBarEnabled()) {
            actionBarManager.init();
            Bukkit.getOnlinePlayers().forEach(actionBarManager::handleJoin);
        }

        if (fancyVisualsConfig.isBossbarsEnabled()) {
            bossbarManager.init();
            Bukkit.getOnlinePlayers().forEach(bossbarManager::handleJoin);
        }

        if (fancyVisualsConfig.isTitlesEnabled()) {
            titleManager.init();
            Bukkit.getOnlinePlayers().forEach(titleManager::handleJoin);
        }
    }

    @Override
    public void onDisable() {
        if (nametagManager != null) {
            nametagManager.shutdown();
        }
        if (nametagScheduler != null) {
            nametagScheduler.shutdown();
        }
        if (tablistManager != null) {
            tablistManager.clearAll();
            tablistManager.shutdown();
        }
        if (actionBarManager != null) {
            actionBarManager.clearAll();
            actionBarManager.shutdown();
        }
        if (bossbarManager != null) {
            bossbarManager.clearAll();
            bossbarManager.shutdown();
        }
        if (titleManager != null) {
            titleManager.clearAll();
            titleManager.shutdown();
        }
        if (workerExecutor != null) {
            workerExecutor.shutdownNow();
        }
    }

    @Override
    public JavaPlugin getPlugin() {
        return instance;
    }

    public JsonPlayerConfigStore getPlayerConfigStore() {
        return playerConfigStore;
    }

    @Override
    public NametagRepository getNametagRepository() {
        return nametagRepository;
    }

    public NametagConfig getNametagConfig() {
        return nametagConfig;
    }

    public FancyVisualsConfig getFancyVisualsConfig() {
        return fancyVisualsConfig;
    }

    public PlayerNametagScheduler getNametagScheduler() {
        return nametagScheduler;
    }

    public NametagManager getNametagManager() {
        return nametagManager;
    }

    public TablistRepository getTablistRepository() {
        return tablistRepository;
    }

    public TablistManager getTablistManager() {
        return tablistManager;
    }

    public ActionBarRepository getActionBarRepository() {
        return actionBarRepository;
    }

    public ActionBarManager getActionBarManager() {
        return actionBarManager;
    }

    public BossbarRepository getBossbarRepository() {
        return bossbarRepository;
    }

    public BossbarManager getBossbarManager() {
        return bossbarManager;
    }

    public TitleRepository getTitleRepository() {
        return titleRepository;
    }

    public TitleManager getTitleManager() {
        return titleManager;
    }

    public ChatFormatRepository getChatFormatRepository() {
        return chatFormatRepository;
    }

    public void reloadAll() {
        fancyVisualsConfig.load();
        nametagConfig.load();
        IFancySitula.LOGGER.setCurrentLevel(parseLogLevel(fancyVisualsConfig.getLogLevel()));
        VaultHelper.loadVault();

        if (workerExecutor != null) {
            workerExecutor.shutdownNow();
        }
        this.workerExecutor = Executors.newFixedThreadPool(
                fancyVisualsConfig.getAmountWorkerThreads(),
                new ThreadFactoryBuilder()
                        .setNameFormat("FancyVisualsWorker-%d")
                        .build()
        );

        if (nametagManager != null) {
            nametagManager.shutdown();
        }
        if (nametagScheduler != null) {
            nametagScheduler.shutdown();
        }
        if (nametagConfig.isEnabled()) {
            nametagScheduler = new PlayerNametagScheduler(
                    workerExecutor,
                    nametagConfig.getDistributionBucketSize(),
                    nametagConfig.getUpdateIntervalMs()
            );
            nametagManager = new NametagManager(nametagRepository, nametagScheduler);
            nametagScheduler.init();
            Bukkit.getOnlinePlayers().forEach(nametagManager::handlePlayerUpdate);
        }

        if (tablistManager != null) {
            tablistManager.clearAll();
            tablistManager.shutdown();
        }
        if (fancyVisualsConfig.isTablistEnabled()) {
            tablistManager = new TablistManager(this, tablistRepository);
            tablistManager.init();
            Bukkit.getOnlinePlayers().forEach(tablistManager::handleJoin);
        }

        if (actionBarManager != null) {
            actionBarManager.clearAll();
            actionBarManager.shutdown();
        }
        if (fancyVisualsConfig.isActionBarEnabled()) {
            actionBarManager = new ActionBarManager(this, actionBarRepository);
            actionBarManager.init();
            Bukkit.getOnlinePlayers().forEach(actionBarManager::handleJoin);
        }

        if (bossbarManager != null) {
            bossbarManager.clearAll();
            bossbarManager.shutdown();
        }
        if (fancyVisualsConfig.isBossbarsEnabled()) {
            bossbarManager = new BossbarManager(this, bossbarRepository);
            bossbarManager.init();
            Bukkit.getOnlinePlayers().forEach(bossbarManager::handleJoin);
        }

        if (titleManager != null) {
            titleManager.clearAll();
            titleManager.shutdown();
        }
        if (fancyVisualsConfig.isTitlesEnabled()) {
            titleManager = new TitleManager(this, titleRepository);
            titleManager.init();
            Bukkit.getOnlinePlayers().forEach(titleManager::handleJoin);
        }
    }

    private LogLevel parseLogLevel(String value) {
        if (value == null) {
            return LogLevel.INFO;
        }

        try {
            return LogLevel.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LogLevel.INFO;
        }
    }

    private void registerCommands() {
        Command command = new FancyVisualsCMD(this);
        getServer().getCommandMap().register("fancyvisuals", command);
    }
}
