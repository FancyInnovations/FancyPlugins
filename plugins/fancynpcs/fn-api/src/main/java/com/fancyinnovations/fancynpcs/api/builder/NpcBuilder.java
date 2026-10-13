package com.fancyinnovations.fancynpcs.api.builder;

import com.fancyinnovations.fancynpcs.api.FancyNpcsPlugin;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.NpcAttribute;
import com.fancyinnovations.fancynpcs.api.NpcData;
import com.fancyinnovations.fancynpcs.api.data.property.NpcVisibility;
import com.fancyinnovations.fancynpcs.api.skins.SkinData;
import com.fancyinnovations.fancynpcs.api.utils.NpcEquipmentSlot;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A builder class for creating NPC instances with a fluent API.
 * This builder follows the same pattern as FancyHolograms builders.
 */
public class NpcBuilder {

    private final NpcData data;
    private boolean persistent = true;

    private NpcBuilder(String name, Location location) {
        this.data = new NpcData(name, null, location);
    }

    /**
     * Creates a new instance of NpcBuilder with the specified name and location.
     *
     * @param name     the name of the NPC
     * @param location the location of the NPC
     * @return a new NpcBuilder instance
     */
    public static NpcBuilder create(String name, Location location) {
        return new NpcBuilder(name, location);
    }

    /**
     * Builds and returns a new Npc instance using the current configuration
     * in the NpcBuilder.
     *
     * @return a new instance of Npc created based on the configured data
     */
    public Npc build() {
        Npc npc = FancyNpcsPlugin.get().getNpcAdapter().apply(data);
        npc.setSaveToFile(persistent);
        npc.create();
        if (data.isSpawnEntity()) {
            npc.spawnForAll();
        }
        return npc;
    }

    /**
     * Builds a new Npc instance using the current configuration in the NpcBuilder
     * and registers it with the NPC manager.
     *
     * @return a new instance of Npc that has been registered
     */
    public Npc buildAndRegister() {
        Npc npc = build();
        FancyNpcsPlugin.get().getNpcManager().registerNpc(npc);
        return npc;
    }

    // Configuration methods

    /**
     * Sets the entity type of the NPC.
     *
     * @param type the entity type
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder type(EntityType type) {
        data.setType(type);
        return this;
    }

    /**
     * Sets the display name of the NPC.
     *
     * @param displayName the display name
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder displayName(String displayName) {
        data.setDisplayName(displayName);
        return this;
    }

    /**
     * Sets the skin of the NPC using an identifier.
     * The identifier can be a UUID, username, URL, or file path.
     *
     * @param identifier the skin identifier
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder skin(String identifier) {
        return skin(identifier, SkinData.SkinVariant.AUTO);
    }

    /**
     * Sets the skin of the NPC using an identifier and variant.
     * The identifier can be a UUID, username, URL, or file path.
     *
     * @param identifier the skin identifier
     * @param variant    the skin variant (AUTO or SLIM)
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder skin(String identifier, SkinData.SkinVariant variant) {
        data.setSkin(identifier, variant);
        return this;
    }

    /**
     * Sets the skin of the NPC using a pre-loaded SkinData object.
     *
     * @param skinData the skin data
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder skin(SkinData skinData) {
        data.setSkinData(skinData);
        return this;
    }

    /**
     * Sets whether the NPC should mirror the skin of players who view it.
     *
     * @param mirrorSkin true to mirror player skins, false otherwise
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder mirrorSkin(boolean mirrorSkin) {
        data.setMirrorSkin(mirrorSkin);
        return this;
    }

    /**
     * Sets whether the NPC should be glowing.
     *
     * @param glowing true to enable glowing, false to disable
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder glowing(boolean glowing) {
        data.setGlowing(glowing);
        return this;
    }

    /**
     * Sets whether the NPC should be glowing with a specific color.
     *
     * @param glowing true to enable glowing, false to disable
     * @param color   the glowing color
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder glowing(boolean glowing, ChatColor color) {
        data.setGlowing(glowing);
        if (color != null) {
            data.setGlowingColor(convertChatColorToNamed(color));
        }
        return this;
    }

    /**
     * Sets whether the NPC should be glowing with a specific color.
     *
     * @param glowing true to enable glowing, false to disable
     * @param color   the glowing color
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder glowing(boolean glowing, NamedTextColor color) {
        data.setGlowing(glowing);
        if (color != null) {
            data.setGlowingColor(color);
        }
        return this;
    }

    /**
     * Sets whether the NPC should appear in the player tab list.
     *
     * @param showInTab true to show in tab, false otherwise
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder showInTab(boolean showInTab) {
        data.setShowInTab(showInTab);
        return this;
    }

    /**
     * Sets whether the NPC should be collidable.
     *
     * @param collidable true to enable collision, false to disable
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder collidable(boolean collidable) {
        data.setCollidable(collidable);
        return this;
    }

    /**
     * Sets whether the NPC should turn to look at nearby players.
     *
     * @param turnToPlayer true to enable turning, false to disable
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder turnToPlayer(boolean turnToPlayer) {
        data.setTurnToPlayer(turnToPlayer);
        return this;
    }

    /**
     * Sets whether the NPC should turn to look at nearby players within a specific distance.
     *
     * @param turnToPlayer true to enable turning, false to disable
     * @param distance     the maximum distance in blocks (use -1 for config default)
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder turnToPlayer(boolean turnToPlayer, float distance) {
        data.setTurnToPlayer(turnToPlayer);
        data.setTurnToPlayerDistance((int) distance);
        return this;
    }

    /**
     * Sets the scale of the NPC.
     *
     * @param scale the scale factor
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder scale(float scale) {
        data.setScale(scale);
        return this;
    }

    /**
     * Sets the visibility mode for the NPC.
     *
     * @param visibility the visibility mode
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder visibility(NpcVisibility visibility) {
        data.setVisibility(visibility);
        return this;
    }

    /**
     * Sets the visibility distance for the NPC.
     *
     * @param distance the visibility distance in blocks (use -1 for config default)
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder visibilityDistance(int distance) {
        data.setVisibilityDistance(distance);
        return this;
    }

    /**
     * Sets an equipment item in a specific slot.
     *
     * @param slot the equipment slot
     * @param item the item to equip
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder equipment(NpcEquipmentSlot slot, ItemStack item) {
        if (data.getEquipment() == null) {
            data.setEquipment(new ConcurrentHashMap<>());
        }
        data.addEquipment(slot, item);
        return this;
    }

    /**
     * Sets a custom attribute on the NPC.
     *
     * @param attribute the attribute to set
     * @param value     the attribute value
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder attribute(NpcAttribute attribute, String value) {
        if (data.getAttributes() == null) {
            data.setEquipment(new ConcurrentHashMap<>());
        }
        data.addAttribute(attribute, value);
        return this;
    }

    /**
     * Sets the interaction cooldown for the NPC.
     *
     * @param millis the cooldown in milliseconds
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder interactionCooldown(long millis) {
        data.setInteractionCooldown(millis / 1000f);
        return this;
    }

    /**
     * Sets the creator UUID of the NPC.
     *
     * @param creator the creator's UUID
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder creator(UUID creator) {
        // Note: NpcData constructor requires creator, so we need to recreate the data
        // Since the constructor is package-private, we'll use reflection or accept this limitation
        // For now, this is a no-op as the creator is set in the constructor
        // In the future, consider adding a setter in NpcData
        return this;
    }

    /**
     * Sets whether the NPC entity should be spawned.
     *
     * @param spawnEntity true to spawn the entity, false otherwise
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder spawnEntity(boolean spawnEntity) {
        data.setSpawnEntity(spawnEntity);
        return this;
    }

    /**
     * Sets whether the NPC should be saved to storage.
     * Default is true.
     *
     * @param persistent true if the NPC should be persistent, false otherwise
     * @return this NpcBuilder for chaining
     */
    public NpcBuilder persistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    /**
     * Converts a Bukkit ChatColor to a Kyori NamedTextColor.
     * This is a best-effort conversion for common colors.
     *
     * @param chatColor the ChatColor to convert
     * @return the corresponding NamedTextColor, or WHITE if no match
     */
    private NamedTextColor convertChatColorToNamed(ChatColor chatColor) {
        return switch (chatColor) {
            case BLACK -> NamedTextColor.BLACK;
            case DARK_BLUE -> NamedTextColor.DARK_BLUE;
            case DARK_GREEN -> NamedTextColor.DARK_GREEN;
            case DARK_AQUA -> NamedTextColor.DARK_AQUA;
            case DARK_RED -> NamedTextColor.DARK_RED;
            case DARK_PURPLE -> NamedTextColor.DARK_PURPLE;
            case GOLD -> NamedTextColor.GOLD;
            case GRAY -> NamedTextColor.GRAY;
            case DARK_GRAY -> NamedTextColor.DARK_GRAY;
            case BLUE -> NamedTextColor.BLUE;
            case GREEN -> NamedTextColor.GREEN;
            case AQUA -> NamedTextColor.AQUA;
            case RED -> NamedTextColor.RED;
            case LIGHT_PURPLE -> NamedTextColor.LIGHT_PURPLE;
            case YELLOW -> NamedTextColor.YELLOW;
            default -> NamedTextColor.WHITE;
        };
    }
}
