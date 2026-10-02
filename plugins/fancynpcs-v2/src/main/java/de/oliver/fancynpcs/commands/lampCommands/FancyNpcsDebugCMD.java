package de.oliver.fancynpcs.commands.lampCommands;

import de.oliver.fancynpcs.skins.mineskin.MineSkinQueue;
import de.oliver.fancynpcs.skins.mojang.MojangQueue;
import de.oliver.fancynpcs.tests.FancyNpcsTests;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class FancyNpcsDebugCMD extends FancyContext {

    public static final FancyNpcsDebugCMD INSTANCE = new FancyNpcsDebugCMD();
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private FancyNpcsDebugCMD() {
    }

    @Command("fancynpcs run_tests")
    @CommandPermission("fancynpcs.command.fancynpcs.run_tests")
    public void onTest(final BukkitCommandActor actor) {
        final Player player = actor.requirePlayer();
        FancyNpcsTests tests = new FancyNpcsTests();
        boolean tested = tests.runAllTests(player);

        if (tested) {
            translator.translate("fancynpcs_test_success")
                    .withPrefix()
                    .replace("player", player.getName())
                    .replace("time", dateTimeFormatter.format(new Date().toInstant().atZone(ZoneId.of("Europe/Berlin"))))
                    .replace("count", String.valueOf(tests.getTestCount()))
                    .send(player);
        } else {
            translator.translate("fancynpcs_test_failure")
                    .withPrefix()
                    .replace("player", player.getName())
                    .replace("time", dateTimeFormatter.format(new Date().toInstant().atZone(ZoneId.of("Europe/Berlin"))))
                    .send(player);
        }
    }

    @Command("fancynpcs skin_system restart_schedulers")
    @CommandPermission("fancynpcs.command.fancynpcs.skin_system.restart_schedulers")
    public void onSkinSchedulerRestart(final BukkitCommandActor actor) {
        final Player player = actor.requirePlayer();
        MineSkinQueue.get().getScheduler().cancel(true);
        MojangQueue.get().getScheduler().cancel(true);

        MineSkinQueue.get().run();
        MojangQueue.get().run();

        translator.translate("fancynpcs_skin_system_restart_schedulers_success").withPrefix().send(player);
    }

    @Command("fancynpcs skin_system scheduler_status")
    @CommandPermission("fancynpcs.command.fancynpcs.skin_system.scheduler_status")
    public void onSkinSchedulerStatus(final BukkitCommandActor actor) {
        final Player player = actor.requirePlayer();
        String mineSkinStatus = MineSkinQueue.get().getScheduler().toString();
        logger.info("MineSkinAPI Status: " + mineSkinStatus);
        translator.translate("fancynpcs_skin_system_scheduler_status")
                .withPrefix()
                .replace("scheduler", "MineSkinAPI")
                .replace("status", mineSkinStatus)
                .send(player);

        String mojangStatus = MojangQueue.get().getScheduler().toString();
        logger.info("MojangAPI Status: " + mojangStatus);
        translator.translate("fancynpcs_skin_system_scheduler_status")
                .withPrefix()
                .replace("scheduler", "MojangAPI")
                .replace("status", mojangStatus)
                .send(player);
    }

    @Command("fancynpcs skin_system clear_queues")
    @CommandPermission("fancynpcs.command.fancynpcs.skin_system.clear_queues")
    public void onClearSkinQueues(final BukkitCommandActor actor) {
        final Player player = actor.requirePlayer();
        MineSkinQueue.get().clear();
        MojangQueue.get().clear();

        translator.translate("fancynpcs_skin_system_clear_queues_success").withPrefix().send(player);
    }

    @Command("fancynpcs skin_system clear_cache")
    @CommandPermission("fancynpcs.command.fancynpcs.skin_system.clear_cache")
    public void onInvalidateCache(final BukkitCommandActor actor) {
        final Player player = actor.requirePlayer();
        plugin.getSkinManagerImpl().getMemCache().clear();
        plugin.getSkinManagerImpl().getFileCache().clear();

        translator.translate("fancynpcs_skin_system_clear_cache_success").withPrefix().send(player);
    }

    @Command("fancynpcs skin_system clear_uuid_cache")
    @CommandPermission("fancynpcs.command.fancynpcs.skin_system.clear_uuid_cache")
    public void onInvalidateUUidCache(final BukkitCommandActor actor) {
        final Player player = actor.requirePlayer();
        plugin.getSkinManagerImpl().getUuidCache().clearCache();
        translator.translate("fancynpcs_skin_system_clear_uuid_cache_success").withPrefix().send(player);
    }
}
