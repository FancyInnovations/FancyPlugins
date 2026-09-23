package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.Location;
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

public final class NearbyCMD extends FancyContext {
    public static final NearbyCMD INSTANCE = new NearbyCMD();
    private static final DecimalFormat COORDS_FORMAT = new DecimalFormat("#.##");
    private static final DecimalFormat DISTANCE_FORMAT = new DecimalFormat("#.#");

    static {
        COORDS_FORMAT.setMinimumFractionDigits(2);
    }

    private NearbyCMD() {
    }

    @Command("npc nearby")
    @CommandPermission("fancynpcs.command.npc.nearby")
    public void onCommand(
            final BukkitCommandActor actor,
            final @Optional @Nullable @Flag("radius") Long radius,
            final @Optional @Nullable @Flag("type") EntityType type,
            final @Optional @Nullable @Flag("sort") SortType sort
    ) {
        final Player sender = actor.requirePlayer();
        Stream<Npc> stream = plugin.getNpcManagerImpl().getAllNpcs().stream();
        // Getting senderLocation of the sender.
        final Location senderLocation = sender.getLocation();
        // Creating a counter which is increased by 1 for every NPC present in player's world.
        final AtomicInteger totalCount = new AtomicInteger(0);
        // Excluding NPCs from different worlds. This also increments the counter defined above.
        stream = stream.filter(npc -> {
            if (npc.getData().getLocation().getWorld().equals(senderLocation.getWorld())) {
                totalCount.incrementAndGet();
                return true;
            }
            return false;
        });
        // Excluding NPCs not created by the sender, if PLAYER_NPCS_FEATURE_FLAG is enabled and sender is a player.
        if (FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled())
            stream = stream.filter(npc -> npc.getData().getCreator().equals(sender.getUniqueId()));
        // Excluding NPCs that are not in radius, if specified and sender is a player. (radius is calculated from the senderLocation of player)
        if (radius != null)
            stream = stream.filter(npc -> npc.getData().getLocation().distance(senderLocation) <= radius);
        // Excluding NPCs that are not of a specified type, if desired.
        if (type != null)
            stream = stream.filter(npc -> npc.getData().getType() == type);
        // Sorting based on SortType choice. Defaults to SortType.NEAREST. There might be more sort types in the future which should be handled here accordingly.
        switch (sort != null ? sort : SortType.NEAREST) { // This should never produce NPE.
            case NAME -> stream = stream.sorted(Comparator.comparing(npc -> npc.getData().getName()));
            case NAME_REVERSED ->
                    stream = stream.sorted(Comparator.comparing(npc -> ((Npc) npc).getData().getName()).reversed());
            case NEAREST ->
                    stream = stream.sorted(Comparator.comparingDouble(npc -> npc.getData().getLocation().distance(senderLocation)));
            case FARTHEST ->
                    stream = stream.sorted(Comparator.comparingDouble(npc -> ((Npc) npc).getData().getLocation().distance(senderLocation)).reversed());
        }
        translator.translate("npc_nearby_header").send(sender);
        // Using AtomicInteger counter because streams don't expose entry index.
        final AtomicInteger count = new AtomicInteger(0);
        // Iterating over each NPC referenced in the stream. Usage of forEachOrdered should presumably preserve element order.
        stream.forEachOrdered(npc -> {
            translator.translate("npc_nearby_entry")
                    .replace("number", String.valueOf(count.incrementAndGet()))
                    .replace("npc", npc.getData().getName())
                    .replace("distance", DISTANCE_FORMAT.format(npc.getData().getLocation().distance(senderLocation)))
                    .replace("location_x", COORDS_FORMAT.format(npc.getData().getLocation().x()))
                    .replace("location_y", COORDS_FORMAT.format(npc.getData().getLocation().y()))
                    .replace("location_z", COORDS_FORMAT.format(npc.getData().getLocation().z()))
                    .replace("world", npc.getData().getLocation().getWorld().getName())
                    .send(sender);
        });
        translator.translate("npc_nearby_footer")
                .replace("count", String.valueOf(count))
                .replace("count_formatted", "· ".repeat(3 - String.valueOf(count).length()) + count)
                .replace("total", String.valueOf(totalCount))
                .replace("total_formatted", "· ".repeat(3 - String.valueOf(totalCount).length()) + totalCount)
                .send(sender);
    }

    // SortType enum contains all possible sort types for the '/npc nearby' command.
    public enum SortType {
        NAME, NAME_REVERSED, NEAREST, FARTHEST
    }

}
