package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Flag;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public final class ListCMD extends FancyContext {
    public static final ListCMD INSTANCE = new ListCMD();
    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");

    static {
        COORDS_FORMAT.setMinimumFractionDigits(2);
    }

    private ListCMD() {
    }

    @Command("npc list")
    @CommandPermission("fancynpcs.command.npc.list")
    public void onCommand(
            final BukkitCommandActor actor,
            final @Optional @Nullable @Flag("type") EntityType type,
            final @Optional @Nullable @Flag("sort") SortType sort
    ) {
        final CommandSender sender = actor.sender();
        Stream<Npc> stream = plugin.getNpcManagerImpl().getAllNpcs().stream();
        // Excluding NPCs not created by the sender, if PLAYER_NPCS_FEATURE_FLAG is enabled and sender is a player.
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && sender instanceof Player player)
            stream = stream.filter(npc -> npc.getData().getCreator().equals(player.getUniqueId()));
        // Excluding NPCs that are not of a specified type, if desired.
        if (type != null)
            stream = stream.filter(npc -> npc.getData().getType() == type);
        // Sorting based on SortType choice. Defaults to SortType.NAME. There might be more sort types in the future which should be handled here accordingly.
        switch (sort != null ? sort : SortType.NAME) {
            case NAME -> stream = stream.sorted(Comparator.comparing(npc -> npc.getData().getName()));
            case NAME_REVERSED ->
                    stream = stream.sorted(Comparator.comparing(npc -> ((Npc) npc).getData().getName()).reversed()); // This needs a cast for some reason.
        }
        translator.translate("npc_list_header").send(sender);
        // Using AtomicInteger counter because streams don't expose entry index.
        final AtomicInteger count = new AtomicInteger(0);
        // Iterating over each NPC referenced in the stream. Usage of forEachOrdered should presumably preserve element order.
        stream.forEachOrdered(npc -> {
            translator.translate("npc_list_entry")
                    .replace("number", String.valueOf(count.incrementAndGet()))
                    .replace("npc", npc.getData().getName())
                    .replace("location_x", COORDS_FORMAT.format(npc.getData().getLocation().x()))
                    .replace("location_y", COORDS_FORMAT.format(npc.getData().getLocation().y()))
                    .replace("location_z", COORDS_FORMAT.format(npc.getData().getLocation().z()))
                    .replace("world", npc.getData().getLocation().getWorld().getName())
                    .send(sender);
        });

        final int totalCount = count.get(); // change this, once we have a page system
        translator.translate("npc_list_footer")
                .replace("count", String.valueOf(count))
                .replace("count_formatted", "· ".repeat(3 - String.valueOf(count).length()) + count)
                .replace("total", String.valueOf(totalCount))
                .replace("total_formatted", "· ".repeat(3 - String.valueOf(totalCount).length()) + totalCount)
                .send(sender);
    }

    /**
     * {@link SortType ListCMD.SortType} enum contains all possible sort types for the {@code /npc list} command.
     */
    public enum SortType {
        NAME, NAME_REVERSED
    }

}
