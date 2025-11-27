package de.oliver.fancynpcs.api.data.property;

import com.google.common.collect.HashMultimap;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public enum NpcVisibility {
    /**
     * Everybody can see an NPC.
     */
    ALL((player, npc) -> true),
    /**
     * The player needs permission to see a specific NPC.
     */
    PERMISSION_REQUIRED(
        (player, npc) -> player.hasPermission("fancynpcs.npc." + npc.getData().getName() + ".see")
    ),
    /**
     * The player needs to be added manually through the API
     */
    MANUAL(ManualNpcVisibility::canSee);

    private final VisibilityPredicate predicate;

    NpcVisibility(VisibilityPredicate predicate) {
        this.predicate = predicate;
    }

    public static Optional<NpcVisibility> byString(String value) {
        return Arrays.stream(NpcVisibility.values())
            .filter(visibility -> visibility.toString().equalsIgnoreCase(value))
            .findFirst();
    }

    public boolean canSee(Player player, Npc npc) {
        return this.predicate.canSee(player, npc);
    }

    @FunctionalInterface
    public interface VisibilityPredicate {
        boolean canSee(Player player, Npc npc);
    }

    /**
     * Handling of NpcVisibility.MANUAL
     */
    public static class ManualNpcVisibility {
        private static final HashMultimap<String, UUID> distantViewers = HashMultimap.create();

        public static boolean canSee(Player player, Npc npc) {
            return npc.isShownFor(player) || distantViewers.containsEntry(npc.getData().getName(), player.getUniqueId());
        }

        public static void addDistantViewer(Npc npc, UUID uuid) {
            addDistantViewer(npc.getData().getName(), uuid);
        }

        public static void addDistantViewer(String npcName, UUID uuid) {
            distantViewers.put(npcName, uuid);
        }

        public static void removeDistantViewer(Npc npc, UUID uuid) {
            removeDistantViewer(npc.getData().getName(), uuid);
        }

        public static void removeDistantViewer(String npcName, UUID uuid) {
            distantViewers.remove(npcName, uuid);
        }

        public static void remove(Npc npc) {
            remove(npc.getData().getName());
        }

        public static void remove(String npcName) {
            distantViewers.removeAll(npcName);
        }

        public static void clear() {
            distantViewers.clear();
        }
    }
}