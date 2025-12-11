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
     * The player visibility is controlled manually through the API.
     * You can use either include list (allowlist) or exclude list (blocklist), or both.
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
     * Handling of NpcVisibility.MANUAL mode with include and exclude lists
     */
    public static class ManualNpcVisibility {
        private static final HashMultimap<String, UUID> includeList = HashMultimap.create();
        private static final HashMultimap<String, UUID> excludeList = HashMultimap.create();

        /**
         * Check if player can see NPC based on include/exclude lists.
         * Logic:
         * - If include list is not empty: player must be in include list to see
         * - If exclude list has entries: player in exclude list cannot see
         * - Include list takes priority over exclude list
         */
        public static boolean canSee(Player player, Npc npc) {
            String npcName = npc.getData().getName();
            UUID playerId = player.getUniqueId();

            boolean hasIncludeList = includeList.containsKey(npcName);
            boolean hasExcludeList = excludeList.containsKey(npcName);

            // If include list exists and is not empty, player MUST be in it
            if (hasIncludeList) {
                return includeList.containsEntry(npcName, playerId);
            }

            // If only exclude list exists, player must NOT be in it
            if (hasExcludeList) {
                return !excludeList.containsEntry(npcName, playerId);
            }

            // If neither list has entries, use default visibility
            return npc.isShownFor(player);
        }

        // Include list management
        public static void addToIncludeList(Npc npc, UUID uuid) {
            addToIncludeList(npc.getData().getName(), uuid);
        }

        public static void addToIncludeList(String npcName, UUID uuid) {
            includeList.put(npcName, uuid);
        }

        public static void removeFromIncludeList(Npc npc, UUID uuid) {
            removeFromIncludeList(npc.getData().getName(), uuid);
        }

        public static void removeFromIncludeList(String npcName, UUID uuid) {
            includeList.remove(npcName, uuid);
        }

        // Exclude list management
        public static void addToExcludeList(Npc npc, UUID uuid) {
            addToExcludeList(npc.getData().getName(), uuid);
        }

        public static void addToExcludeList(String npcName, UUID uuid) {
            excludeList.put(npcName, uuid);
        }

        public static void removeFromExcludeList(Npc npc, UUID uuid) {
            removeFromExcludeList(npc.getData().getName(), uuid);
        }

        public static void removeFromExcludeList(String npcName, UUID uuid) {
            excludeList.remove(npcName, uuid);
        }

        // Old methods for backward compatibility
        @Deprecated
        public static void addDistantViewer(Npc npc, UUID uuid) {
            addToIncludeList(npc, uuid);
        }

        @Deprecated
        public static void addDistantViewer(String npcName, UUID uuid) {
            addToIncludeList(npcName, uuid);
        }

        @Deprecated
        public static void removeDistantViewer(Npc npc, UUID uuid) {
            removeFromIncludeList(npc, uuid);
        }

        @Deprecated
        public static void removeDistantViewer(String npcName, UUID uuid) {
            removeFromIncludeList(npcName, uuid);
        }

        public static void remove(Npc npc) {
            remove(npc.getData().getName());
        }

        public static void remove(String npcName) {
            includeList.removeAll(npcName);
            excludeList.removeAll(npcName);
        }

        public static void clear() {
            includeList.clear();
            excludeList.clear();
        }
    }
}