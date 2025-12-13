package de.oliver.fancynpcs.api.data.property;

import com.google.common.collect.HashMultimap;
import de.oliver.fancynpcs.api.Npc;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
     * You can use either include list (allowlist) or exclude list (blocklist).
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
        /**
         * Visibility mode for each NPC
         */
        public enum Mode {
            /**
             * Include mode (allowlist): Only players in the list can see the NPC
             */
            INCLUDE,
            /**
             * Exclude mode (blocklist): Players in the list cannot see the NPC
             */
            EXCLUDE
        }

        private static final HashMultimap<String, UUID> playerList = HashMultimap.create();
        private static final Map<String, Mode> npcModes = new HashMap<>();

        /**
         * Check if player can see NPC based on the player list and mode.
         * Logic:
         * - INCLUDE mode: player must be in the list to see
         * - EXCLUDE mode: player in the list cannot see
         */
        public static boolean canSee(Player player, Npc npc) {
            String npcId = npc.getData().getId();
            UUID playerId = player.getUniqueId();

            Mode mode = npcModes.get(npcId);

            if (mode == null) {
                return npc.isShownFor(player);
            }

            boolean isInList = playerList.containsEntry(npcId, playerId);

            return switch (mode) {
                case INCLUDE -> isInList;
                case EXCLUDE -> !isInList;
            };
        }

        /**
         * Set the visibility mode for an NPC
         */
        public static void setMode(Npc npc, Mode mode) {
            setMode(npc.getData().getId(), mode);
        }

        /**
         * Set the visibility mode for an NPC
         */
        public static void setMode(String npcId, Mode mode) {
            if (mode == null) {
                npcModes.remove(npcId);
            } else {
                npcModes.put(npcId, mode);
            }
        }

        /**
         * Get the current visibility mode for an NPC
         */
        public static Mode getMode(Npc npc) {
            return getMode(npc.getData().getId());
        }

        /**
         * Get the current visibility mode for an NPC
         */
        public static Mode getMode(String npcId) {
            return npcModes.get(npcId);
        }

        /**
         * Add a player to the visibility list
         */
        public static void addPlayer(Npc npc, UUID uuid) {
            addPlayer(npc.getData().getId(), uuid);
        }

        /**
         * Add a player to the visibility list by NPC
         */
        public static void addPlayer(String npcId, UUID uuid) {
            playerList.put(npcId, uuid);
        }

        /**
         * Remove a player from the visibility list
         */
        public static void removePlayer(Npc npc, UUID uuid) {
            removePlayer(npc.getData().getId(), uuid);
        }

        /**
         * Remove a player from the visibility list by NPC
         */
        public static void removePlayer(String npcId, UUID uuid) {
            playerList.remove(npcId, uuid);
        }

        /**
         * Remove all visibility settings for an NPC
         */
        public static void remove(Npc npc) {
            remove(npc.getData().getId());
        }

        /**
         * Remove all visibility settings for an NPC
         */
        public static void remove(String npcId) {
            playerList.removeAll(npcId);
            npcModes.remove(npcId);
        }

        /**
         * Clear all visibility settings
         */
        public static void clear() {
            playerList.clear();
            npcModes.clear();
        }
    }
}