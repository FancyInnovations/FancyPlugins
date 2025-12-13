package de.oliver.fancynpcs.tracker;

import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcStartLookingEvent;
import de.oliver.fancynpcs.api.events.NpcStopLookingEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collection;

public class TurnToPlayerTracker implements Runnable {

    @Override
    public void run() {
        Collection<Npc> npcs = FancyNpcs.getInstance().getNpcManagerImpl().getAllNpcs();
        int defaultTurnToPlayerDistance = FancyNpcs.getInstance().getFancyNpcConfig().getTurnToPlayerDistance();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Location playerLocation = player.getLocation();

            for (Npc npc : npcs) {
                NpcData npcData = npc.getData();
                Location npcLocation = npcData.getLocation();

                if (npcLocation == null || !npcLocation.getWorld().getName().equalsIgnoreCase(playerLocation.getWorld().getName())) {
                    continue;
                }

                double distance = playerLocation.distance(npcLocation);
                if (Double.isNaN(distance)) {
                    continue;
                }

                // Get NPC-specific turn distance or fall back to default
                int npcTurnDistance = npcData.getTurnToPlayerDistance();
                int effectiveTurnDistance = (npcTurnDistance == -1) ? defaultTurnToPlayerDistance : npcTurnDistance;

                if (npcData.isTurnToPlayer() && distance < effectiveTurnDistance) {
                    // Calculate the base eye height for the entity type
                    double baseEyeHeight = getEntityEyeHeight(npcData.getType());

                    // Adjust the NPC location Y coordinate based on the scale
                    Location adjustedNpcLocation = npcLocation.clone();
                    adjustedNpcLocation.setY(npcLocation.getY() + (baseEyeHeight * npcData.getScale()));

                    // Calculate direction from adjusted NPC eye position to player eye position
                    Location playerEyeLocation = playerLocation.clone();
                    playerEyeLocation.setY(playerLocation.getY() + player.getEyeHeight());

                    Location newLoc = playerEyeLocation.clone();
                    newLoc.setDirection(newLoc.subtract(adjustedNpcLocation).toVector());
                    npc.lookAt(player, newLoc);
                    // Setting NPC to be looking at the player and getting the value previously stored (or not) inside a map.
                    Boolean wasPreviouslyLooking = npc.getIsLookingAtPlayer().put(player.getUniqueId(), true);
                    // Comparing the previous state with current state to prevent event from being called continuously.
                    if (wasPreviouslyLooking == null || !wasPreviouslyLooking) {
                        // Calling NpcStartLookingEvent from the main thread.
                        FancyNpcs.getInstance().getScheduler().runTask(null, () -> {
                            new NpcStartLookingEvent(npc, player).callEvent();
                        });
                    }
                    // Updating state if changed.
                } else if (npcData.isTurnToPlayer() && npc.getIsLookingAtPlayer().getOrDefault(player.getUniqueId(), false)) {
                    npc.getIsLookingAtPlayer().put(player.getUniqueId(), false);
                    // Resetting to initial direction, if configured.
                    if (FancyNpcs.getInstance().getFancyNpcConfig().isTurnToPlayerResetToInitialDirection()) {
                        npc.move(player, false);
                    }
                    // Calling NpcStopLookingEvent from the main thread.
                    FancyNpcs.getInstance().getScheduler().runTask(null, () -> {
                        new NpcStopLookingEvent(npc, player).callEvent();
                    });
                }
            }
        }
    }

    /**
     * Gets the base eye height for different entity types.
     * Based on Minecraft's default entity eye heights.
     *
     * @param type The entity type
     * @return The base eye height in blocks
     */
    private double getEntityEyeHeight(org.bukkit.entity.EntityType type) {
        return switch (type) {
            case PLAYER -> 1.62;
            case ZOMBIE, SKELETON, STRAY, HUSK, DROWNED, WITHER_SKELETON -> 1.74;
            case CREEPER -> 1.7;
            case ENDERMAN -> 2.55;
            case SPIDER, CAVE_SPIDER -> 0.5;
            case PIG -> 0.6;
            case SHEEP -> 0.65;
            case COW, MOOSHROOM -> 1.3;
            case CHICKEN -> 0.4;
            case HORSE, DONKEY, MULE -> 1.52;
            case VILLAGER, ZOMBIE_VILLAGER -> 1.62;
            case IRON_GOLEM -> 2.7;
            case WOLF -> 0.68;
            case CAT, OCELOT -> 0.35;
            case RABBIT -> 0.3;
            case BAT -> 0.45;
            case SQUID, GLOW_SQUID -> 0.4;
            case SILVERFISH -> 0.13;
            case ENDERMITE -> 0.13;
            case BLAZE -> 1.7;
            case GHAST -> 2.0;
            case SLIME, MAGMA_CUBE -> 0.5;
            case WITCH -> 1.62;
            case EVOKER, VINDICATOR, ILLUSIONER, PILLAGER -> 1.62;
            case VEX -> 0.8;
            case GUARDIAN, ELDER_GUARDIAN -> 0.425;
            case SHULKER -> 0.5;
            case PHANTOM -> 0.5;
            case BEE -> 0.3;
            case FOX -> 0.4;
            case PANDA -> 1.13;
            case STRIDER -> 1.7;
            case HOGLIN, ZOGLIN -> 1.4;
            case PIGLIN, PIGLIN_BRUTE, ZOMBIFIED_PIGLIN -> 1.62;
            case AXOLOTL -> 0.3;
            case GOAT -> 0.9;
            case ALLAY -> 0.6;
            case FROG -> 0.25;
            case TADPOLE -> 0.13;
            case WARDEN -> 2.5;
            case CAMEL -> 2.275;
            case SNIFFER -> 1.0;
            case BREEZE -> 1.4;
            case ARMADILLO -> 0.26;
            case BOGGED -> 1.74;
            case ARMOR_STAND -> 1.975;
            case TEXT_DISPLAY, ITEM_DISPLAY, BLOCK_DISPLAY, INTERACTION -> 0.5;
            default -> 1.62;
        };
    }
}
