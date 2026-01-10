package com.fancyinnovations.fancynpcs.api;

import com.fancyinnovations.fancynpcs.api.actions.ActionTrigger;
import com.fancyinnovations.fancynpcs.api.actions.NpcAction;
import com.fancyinnovations.fancynpcs.api.data.property.NpcVisibility;
import com.fancyinnovations.fancynpcs.api.skins.SkinData;
import com.fancyinnovations.fancynpcs.api.utils.NpcEquipmentSlot;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class NpcData {

    private final String id;
    private final String name;
    private final UUID creator;
    private String displayName;
    private float displayNameScale = 1.0f; // Scale for display name (1.0 = normal, uses Text Display when != 1)
    private SkinData skin;
    private boolean mirrorSkin;
    private Location location;
    private boolean showInTab;
    private boolean spawnEntity;
    private boolean collidable;
    private boolean glowing;
    private NamedTextColor glowingColor;
    private EntityType type;
    private Map<NpcEquipmentSlot, ItemStack> equipment;
    private Consumer<Player> onClick;
    private Map<ActionTrigger, List<NpcAction.NpcActionData>> actions;
    private boolean turnToPlayer;
    private int turnToPlayerDistance = -1; // -1 means use the default from config
    @Deprecated
    private float interactionCooldown; // Deprecated: use triggerCooldowns instead
    private Map<ActionTrigger, Float> triggerCooldowns; // Cooldown per trigger (in seconds)
    private float scale;
    private int visibilityDistance;
    private Map<NpcAttribute, String> attributes;
    private NpcVisibility visibility;
    private boolean hasChanges = false;
    private Runnable onModify;
    private boolean persistent = true;
    private String filePath;

    public NpcData(
            String id,
            String name,
            UUID creator,
            String displayName,
            SkinData skin,
            Location location,
            boolean showInTab,
            boolean spawnEntity,
            boolean collidable,
            boolean glowing,
            NamedTextColor glowingColor,
            EntityType type,
            Map<NpcEquipmentSlot, ItemStack> equipment,
            boolean turnToPlayer,
            int turnToPlayerDistance,
            Consumer<Player> onClick,
            Map<ActionTrigger, List<NpcAction.NpcActionData>> actions,
            float interactionCooldown,
            float scale,
            int visibilityDistance,
            Map<NpcAttribute, String> attributes,
            boolean mirrorSkin
    ) {
        this.id = id;
        this.name = name;
        this.creator = creator;
        this.displayName = displayName;
        this.skin = skin;
        this.location = location;
        this.showInTab = showInTab;
        this.spawnEntity = spawnEntity;
        this.collidable = collidable;
        this.glowing = glowing;
        this.glowingColor = glowingColor;
        this.type = type;
        this.equipment = equipment;
        this.onClick = onClick;
        this.actions = actions;
        this.turnToPlayer = turnToPlayer;
        this.turnToPlayerDistance = turnToPlayerDistance;
        this.interactionCooldown = interactionCooldown;
        this.triggerCooldowns = new ConcurrentHashMap<>();
        this.scale = scale;
        this.visibilityDistance = visibilityDistance;
        this.attributes = attributes;
        this.mirrorSkin = mirrorSkin;
        this.hasChanges = true;
    }

    /**
     * Creates a default npc with random id
     */
    public NpcData(String name, UUID creator, Location location) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.creator = creator;
        this.location = location;
        this.displayName = name;
        this.type = EntityType.PLAYER;
        this.showInTab = false;
        this.spawnEntity = true;
        this.collidable = true;
        this.glowing = false;
        this.glowingColor = NamedTextColor.WHITE;
        this.onClick = p -> {
        };
        this.actions = new ConcurrentHashMap<>();
        this.turnToPlayer = false;
        this.turnToPlayerDistance = -1; // Use default from config
        this.interactionCooldown = 0;
        this.triggerCooldowns = new ConcurrentHashMap<>();
        this.scale = 1;
        this.visibilityDistance = -1;
        this.equipment = new ConcurrentHashMap<>();
        this.attributes = new ConcurrentHashMap<>();
        this.mirrorSkin = false;
        this.visibility = NpcVisibility.ALL;
        this.hasChanges = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UUID getCreator() {
        return creator == null ? UUID.fromString("00000000-0000-0000-0000-000000000000") : creator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public NpcData setDisplayName(String displayName) {
        if (!Objects.equals(this.displayName, displayName)) {
            this.displayName = displayName;
            setHasChanges(true);
        }
        return this;
    }

    public float getDisplayNameScale() {
        return displayNameScale;
    }

    public NpcData setDisplayNameScale(float displayNameScale) {
        if (this.displayNameScale != displayNameScale) {
            this.displayNameScale = displayNameScale;
            setHasChanges(true);
        }
        return this;
    }

    public SkinData getSkinData() {
        return skin;
    }

    /**
     * Sets the skin data of the npc
     * Use this method, if you have a loaded skin data object (with texture and signature), otherwise use {@link #setSkin(String, SkinData.SkinVariant)}
     *
     * @param skinData the skin data
     */
    public NpcData setSkinData(SkinData skinData) {
        if (!Objects.equals(this.skin, skinData)) {
            this.skin = skinData;
            setHasChanges(true);
        }
        return this;
    }

    /**
     * Loads the skin data and sets it as the skin of the npc
     *
     * @param skin    a valid UUID, username, URL or file path
     * @param variant the skin variant
     */
    public NpcData setSkin(String skin, SkinData.SkinVariant variant) {
        SkinData data = FancyNpcsPlugin.get().getSkinManager().getByIdentifier(skin, variant);
        data.setIdentifier(skin);
        return setSkinData(data);
    }

    /**
     * Loads the skin data and sets it as the skin of the npc
     *
     * @param skin a valid UUID, username, URL or file path
     */
    public NpcData setSkin(String skin) {
        return setSkin(skin, SkinData.SkinVariant.AUTO);
    }

    public Location getLocation() {
        return location;
    }

    public NpcData setLocation(Location location) {
        if (!Objects.equals(this.location, location)) {
            this.location = location;
            setHasChanges(true);
        }
        return this;
    }

    public boolean isShowInTab() {
        return showInTab;
    }

    public NpcData setShowInTab(boolean showInTab) {
        if (this.showInTab != showInTab) {
            this.showInTab = showInTab;
            setHasChanges(true);
        }
        return this;
    }

    public boolean isSpawnEntity() {
        return spawnEntity;
    }

    public NpcData setSpawnEntity(boolean spawnEntity) {
        if (this.spawnEntity != spawnEntity) {
            this.spawnEntity = spawnEntity;
            setHasChanges(true);
        }
        return this;
    }

    public boolean isCollidable() {
        return collidable;
    }

    public NpcData setCollidable(boolean collidable) {
        if (this.collidable != collidable) {
            this.collidable = collidable;
            setHasChanges(true);
        }
        return this;
    }

    public boolean isGlowing() {
        return glowing;
    }

    public NpcData setGlowing(boolean glowing) {
        if (this.glowing != glowing) {
            this.glowing = glowing;
            setHasChanges(true);
        }
        return this;
    }

    public NamedTextColor getGlowingColor() {
        return glowingColor;
    }

    public NpcData setGlowingColor(NamedTextColor glowingColor) {
        if (!Objects.equals(this.glowingColor, glowingColor)) {
            this.glowingColor = glowingColor;
            setHasChanges(true);
        }
        return this;
    }

    public EntityType getType() {
        return type;
    }

    public NpcData setType(EntityType type) {
        if (!Objects.equals(this.type, type)) {
            this.type = type;
            attributes.clear();
            setHasChanges(true);
        }
        return this;
    }

    public Map<NpcEquipmentSlot, ItemStack> getEquipment() {
        return equipment;
    }

    public NpcData setEquipment(Map<NpcEquipmentSlot, ItemStack> equipment) {
        if (!Objects.equals(this.equipment, equipment)) {
            this.equipment = equipment;
            setHasChanges(true);
        }
        return this;
    }

    public NpcData addEquipment(NpcEquipmentSlot slot, ItemStack item) {
        equipment.put(slot, item);
        setHasChanges(true);
        return this;
    }

    public Consumer<Player> getOnClick() {
        return onClick;
    }

    public NpcData setOnClick(Consumer<Player> onClick) {
        if (!Objects.equals(this.onClick, onClick)) {
            this.onClick = onClick;
            setHasChanges(true);
        }
        return this;
    }

    public Map<ActionTrigger, List<NpcAction.NpcActionData>> getActions() {
        return actions;
    }

    public NpcData setActions(Map<ActionTrigger, List<NpcAction.NpcActionData>> actions) {
        if (!Objects.equals(this.actions, actions)) {
            this.actions = actions;
            setHasChanges(true);
        }
        return this;
    }

    public List<NpcAction.NpcActionData> getActions(ActionTrigger trigger) {
        return actions.getOrDefault(trigger, new ArrayList<>());
    }

    public NpcData setActions(ActionTrigger trigger, List<NpcAction.NpcActionData> actions) {
        this.actions.put(trigger, actions);
        setHasChanges(true);
        return this;
    }

    public NpcData addAction(ActionTrigger trigger, int order, NpcAction action, String value) {
        List<NpcAction.NpcActionData> a = actions.getOrDefault(trigger, new ArrayList<>());
        a.add(new NpcAction.NpcActionData(order, action, value));
        actions.put(trigger, a);
        setHasChanges(true);
        return this;
    }

    public NpcData removeAction(ActionTrigger trigger, NpcAction action) {
        List<NpcAction.NpcActionData> a = actions.getOrDefault(trigger, new ArrayList<>());
        a.removeIf(ad -> ad.action().equals(action));
        actions.put(trigger, a);
        setHasChanges(true);
        return this;
    }

    public boolean isTurnToPlayer() {
        return turnToPlayer;
    }

    public NpcData setTurnToPlayer(boolean turnToPlayer) {
        if (this.turnToPlayer != turnToPlayer) {
            this.turnToPlayer = turnToPlayer;
            setHasChanges(true);
        }
        return this;
    }

    /**
     * Gets the turn-to-player distance for this NPC.
     * 
     * @return the custom distance value, or -1 if using the default from config
     */
    public int getTurnToPlayerDistance() {
        return turnToPlayerDistance;
    }
    
    /**
     * Sets the turn-to-player distance for this NPC.
     *
     * @param distance the custom distance value, or -1 to use the default from config
     * @return this NpcData instance for method chaining
     */
    public NpcData setTurnToPlayerDistance(int distance) {
        if (this.turnToPlayerDistance != distance) {
            this.turnToPlayerDistance = distance;
            setHasChanges(true);
        }
        return this;
    }

    /**
     * @deprecated Use {@link #getTriggerCooldown(ActionTrigger)} instead
     */
    @Deprecated
    public float getInteractionCooldown() {
        return interactionCooldown;
    }

    /**
     * @deprecated Use {@link #setTriggerCooldown(ActionTrigger, float)} instead
     */
    @Deprecated
    public NpcData setInteractionCooldown(float interactionCooldown) {
        if (this.interactionCooldown != interactionCooldown) {
            this.interactionCooldown = interactionCooldown;
            setHasChanges(true);
        }
        return this;
    }

    /**
     * Gets the cooldown for a specific trigger in seconds.
     * If ANY_CLICK has a cooldown set, it applies to all click types.
     *
     * @param trigger the action trigger
     * @return the cooldown in seconds, or 0 if no cooldown is set
     */
    public float getTriggerCooldown(ActionTrigger trigger) {
        if (triggerCooldowns == null) {
            triggerCooldowns = new ConcurrentHashMap<>();
        }
        // ANY_CLICK cooldown overrides specific click cooldowns
        Float anyClickCooldown = triggerCooldowns.get(ActionTrigger.ANY_CLICK);
        if (anyClickCooldown != null && anyClickCooldown > 0 &&
            (trigger == ActionTrigger.LEFT_CLICK || trigger == ActionTrigger.RIGHT_CLICK)) {
            return anyClickCooldown;
        }
        return triggerCooldowns.getOrDefault(trigger, 0f);
    }

    /**
     * Sets the cooldown for a specific trigger.
     * Setting ANY_CLICK cooldown will apply to all click types (overrides LEFT_CLICK and RIGHT_CLICK).
     *
     * @param trigger the action trigger
     * @param cooldown the cooldown in seconds (0 to disable)
     * @return this NpcData instance for method chaining
     */
    public NpcData setTriggerCooldown(ActionTrigger trigger, float cooldown) {
        if (triggerCooldowns == null) {
            triggerCooldowns = new ConcurrentHashMap<>();
        }
        if (cooldown <= 0) {
            triggerCooldowns.remove(trigger);
        } else {
            triggerCooldowns.put(trigger, cooldown);
        }
        setHasChanges(true);
        return this;
    }

    /**
     * Gets all trigger cooldowns.
     *
     * @return map of trigger to cooldown in seconds
     */
    public Map<ActionTrigger, Float> getTriggerCooldowns() {
        if (triggerCooldowns == null) {
            triggerCooldowns = new ConcurrentHashMap<>();
        }
        return triggerCooldowns;
    }

    /**
     * Sets all trigger cooldowns.
     *
     * @param triggerCooldowns map of trigger to cooldown in seconds
     * @return this NpcData instance for method chaining
     */
    public NpcData setTriggerCooldowns(Map<ActionTrigger, Float> triggerCooldowns) {
        this.triggerCooldowns = triggerCooldowns != null ? triggerCooldowns : new ConcurrentHashMap<>();
        setHasChanges(true);
        return this;
    }

    public float getScale() {
        return scale;
    }

    public NpcData setScale(float scale) {
        if (this.scale != scale) {
            this.scale = scale;
            setHasChanges(true);
        }
        return this;
    }

    public int getVisibilityDistance() {
        return visibilityDistance;
    }

    public NpcData setVisibilityDistance(int visibilityDistance) {
        if (this.visibilityDistance != visibilityDistance) {
            this.visibilityDistance = visibilityDistance;
            setHasChanges(true);
        }
        return this;
    }

    public Map<NpcAttribute, String> getAttributes() {
        return attributes;
    }

    public void addAttribute(NpcAttribute attribute, String value) {
        attributes.put(attribute, value);
        setHasChanges(true);
    }

    public void applyAllAttributes(Npc npc) {
        for (NpcAttribute attribute : attributes.keySet()) {
            attribute.apply(npc, attributes.get(attribute));
        }
    }

    public boolean isMirrorSkin() {
        return mirrorSkin;
    }

    public NpcData setMirrorSkin(boolean mirrorSkin) {
        if (this.mirrorSkin != mirrorSkin) {
            this.mirrorSkin = mirrorSkin;
            setHasChanges(true);
        }
        return this;
    }

    public NpcVisibility getVisibility() {
        return visibility == null ? NpcVisibility.ALL : visibility;
    }

    public NpcData setVisibility(NpcVisibility visibility) {
        if (!Objects.equals(this.visibility, visibility)) {
            this.visibility = visibility;
            setHasChanges(true);
        }
        return this;
    }

    /**
     * @return Whether the NPC needs to send an update to players
     * @deprecated Use {@link #hasChanges()} instead
     */
    @Deprecated
    public boolean isDirty() {
        return hasChanges;
    }

    /**
     * @param dirty Whether the NPC needs to send an update to players
     * @deprecated Use {@link #setHasChanges(boolean)} instead
     */
    @Deprecated
    public void setDirty(boolean dirty) {
        setHasChanges(dirty);
    }

    /**
     * @return Whether the NPC needs to send an update to players
     */
    public boolean hasChanges() {
        return hasChanges;
    }

    /**
     * @param hasChanges Whether the NPC needs to send an update to players
     */
    public void setHasChanges(boolean hasChanges) {
        this.hasChanges = hasChanges;
        if (hasChanges && onModify != null) {
            onModify.run();
        }
    }

    public void setOnModify(Runnable onModify) {
        this.onModify = onModify;
    }

    public boolean isPersistent() {
        return persistent;
    }

    public NpcData setPersistent(boolean persistent) {
        this.persistent = persistent;
        return this;
    }

    /**
     * Gets the file path where this NPC is stored.
     * The path is relative to the data directory (plugins/FancyNpcs/data/npcs/).
     *
     * @return the file path (without .json extension), or null if not set
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the file path where this NPC should be stored.
     * The path is relative to the data directory (plugins/FancyNpcs/data/npcs/).
     *
     * @param filePath the file path (without .json extension)
     * @return this NpcData instance for method chaining
     */
    public NpcData setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }
}
