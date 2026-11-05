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
     *
     * <p>Viewers are keyed by the stable NPC id ({@link de.oliver.fancynpcs.api.NpcData#getId()}),
     * so renaming an NPC (which only changes {@link de.oliver.fancynpcs.api.NpcData#getName()})
     * keeps manual visibility intact.</p>
     */
    public static class ManualNpcVisibility {
        private static final HashMultimap<String, UUID> distantViewers = HashMultimap.create();

        public static boolean canSee(Player player, Npc npc) {
            return npc.isShownFor(player) || distantViewers.containsEntry(npc.getData().getId(), player.getUniqueId());
        }

        public static void addDistantViewer(Npc npc, UUID uuid) {
            addDistantViewer(npc.getData().getId(), uuid);
        }

        /**
         * @param npcId the stable NPC id (see {@link de.oliver.fancynpcs.api.NpcData#getId()}), not the mutable name
         */
        public static void addDistantViewer(String npcId, UUID uuid) {
            distantViewers.put(npcId, uuid);
        }

        public static void removeDistantViewer(Npc npc, UUID uuid) {
            removeDistantViewer(npc.getData().getId(), uuid);
        }

        /**
         * @param npcId the stable NPC id (see {@link de.oliver.fancynpcs.api.NpcData#getId()}), not the mutable name
         */
        public static void removeDistantViewer(String npcId, UUID uuid) {
            distantViewers.remove(npcId, uuid);
        }

        public static void remove(Npc npc) {
            remove(npc.getData().getId());
        }

        /**
         * @param npcId the stable NPC id (see {@link de.oliver.fancynpcs.api.NpcData#getId()}), not the mutable name
         */
        public static void remove(String npcId) {
            distantViewers.removeAll(npcId);
        }

        public static void clear() {
            distantViewers.clear();
        }
    }
}