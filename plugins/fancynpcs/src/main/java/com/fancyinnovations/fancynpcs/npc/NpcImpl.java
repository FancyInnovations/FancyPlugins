package com.fancyinnovations.fancynpcs.npc;

import de.oliver.fancylib.RandomUtils;
import com.fancyinnovations.fancynpcs.api.FancyNpcsPlugin;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.NpcAttribute;
import com.fancyinnovations.fancynpcs.api.NpcData;
import com.fancyinnovations.fancynpcs.api.events.NpcDespawnEvent;
import com.fancyinnovations.fancynpcs.api.events.NpcSpawnEvent;
import com.fancyinnovations.fancynpcs.api.utils.NpcEquipmentSlot;
import com.fancyinnovations.fancynpcs.api.skins.SkinData;
import de.oliver.fancysitula.api.entities.FS_Display;
import de.oliver.fancysitula.api.entities.FS_Entity;
import de.oliver.fancysitula.api.entities.FS_Mannequin;
import de.oliver.fancysitula.api.entities.FS_Player;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.entities.FS_TextDisplay;
import de.oliver.fancysitula.api.packets.FS_ClientboundAddEntityPacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundAnimatePacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundCreateOrUpdateTeamPacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundPacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundPlayerInfoUpdatePacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundUpdateAttributesPacket;
import de.oliver.fancysitula.api.packets.FS_Color;
import de.oliver.fancysitula.api.teams.FS_CollisionRule;
import de.oliver.fancysitula.api.teams.FS_NameTagVisibility;
import de.oliver.fancysitula.api.utils.FS_EquipmentSlot;
import de.oliver.fancysitula.api.utils.FS_GameProfile;
import de.oliver.fancysitula.api.utils.FS_GameType;
import de.oliver.fancysitula.factories.FancySitula;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.lushplugins.chatcolorhandler.ModernChatColorHandler;

import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class NpcImpl extends Npc {

    private static final char[] LOCAL_NAME_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};

    // Mannequin support detection (1.21.9+)
    private static final boolean MANNEQUIN_SUPPORTED;
    static {
        boolean supported = false;
        try {
            EntityType.valueOf("MANNEQUIN");
            supported = true;
        } catch (IllegalArgumentException e) {
            // Mannequin not available on this version
        }
        MANNEQUIN_SUPPORTED = supported;
    }

    private final String localName;
    private final UUID entityUuid;
    private FS_Entity fsEntity;
    private FS_Entity sittingVehicle;
    private boolean usingMannequin = false;

    // Text Display for multi-line display names (used when display name contains newlines)
    private FS_TextDisplay displayNameTextDisplay;
    private boolean usingTextDisplayForName = false;

    public NpcImpl(NpcData data) {
        super(data);
        this.localName = generateLocalName();
        this.entityUuid = UUID.randomUUID();
        initEntity();
    }

    private void initEntity() {
        if (data.getType() == EntityType.PLAYER) {
            // Use Mannequin for player-type NPCs on 1.21.9+ for better performance
            // Mannequin doesn't require tab list entries, reducing packet overhead
            if (MANNEQUIN_SUPPORTED) {
                this.fsEntity = new FS_Mannequin();
                this.usingMannequin = true;
            } else {
                this.fsEntity = new FS_Player();
                this.usingMannequin = false;
            }
        } else {
            this.fsEntity = new FS_Entity(data.getType());
            this.usingMannequin = false;
        }

        fsEntity.setUuid(entityUuid);

        if (data.getLocation() != null) {
            fsEntity.setLocation(data.getLocation());
            fsEntity.setHeadYaw(data.getLocation().getYaw());
        }
    }

    @Override
    protected String generateLocalName() {
        StringBuilder localName = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            localName.append('&').append(LOCAL_NAME_CHARS[(int) RandomUtils.randomInRange(0, LOCAL_NAME_CHARS.length)]);
        }
        return ChatColor.translateAlternateColorCodes('&', localName.toString());
    }

    @Override
    public void create() {
        // Reinitialize the entity with the correct type
        // This is needed because NpcData defaults to PLAYER type,
        // and the type may be changed after the NpcImpl constructor is called
        initEntity();

        // Clear team state for entities that use UUID-based team membership:
        // - Non-PLAYER entities always use UUID
        // - Mannequin entities (even though type is PLAYER) also use UUID
        // UUID changes when entity is recreated, so team must be recreated.
        if (data.getType() != EntityType.PLAYER || usingMannequin) {
            isTeamCreated.clear();
        }
    }

    @Override
    public void spawn(Player player) {
        if (data.getLocation() == null || fsEntity == null) {
            return;
        }

        if (!data.getLocation().getWorld().getName().equalsIgnoreCase(player.getWorld().getName())) {
            return;
        }

        if (!new NpcSpawnEvent(this, player).callEvent()) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        syncWithData();

        // Build packets list - PlayerInfoUpdate must be received before AddEntity for skin rendering
        List<FS_ClientboundPacket> packets = new ArrayList<>();

        // For PLAYER NPCs (not Mannequin), add PlayerInfo packet first
        // Mannequin entities handle skin via DATA_PROFILE entity data, not PlayerInfo
        if (data.getType() == EntityType.PLAYER && !usingMannequin) {
            FS_ClientboundPacket playerInfoPacket = createPlayerInfoPacket(player, true);
            if (playerInfoPacket != null) {
                packets.add(playerInfoPacket);
            }
        }

        // Add entity spawn packet
        FS_ClientboundAddEntityPacket addEntityPacket = FancySitula.PACKET_FACTORY.createAddEntityPacket(
                fsEntity.getId(),
                fsEntity.getUuid(),
                fsEntity.getType(),
                fsEntity.getX(),
                fsEntity.getY(),
                fsEntity.getZ(),
                fsEntity.getYaw(),
                fsEntity.getPitch(),
                fsEntity.getHeadYaw(),
                fsEntity.getVelocityX(),
                fsEntity.getVelocityY(),
                fsEntity.getVelocityZ(),
                fsEntity.getData()
        );
        packets.add(addEntityPacket);

        isVisibleForPlayer.put(player.getUniqueId(), true);

        // Schedule removal from playerlist after delay (not needed for Mannequin)
        int removeDelay = FancyNpcsPlugin.get().getFancyNpcConfig().getRemoveNpcsFromPlayerlistDelay();
        if (data.getType() == EntityType.PLAYER && !usingMannequin && !data.isShowInTab() && removeDelay > 0) {
            FancyNpcsPlugin.get().getNpcThread().schedule(() -> {
                FancySitula.PACKET_FACTORY.createPlayerInfoRemovePacket(List.of(fsEntity.getUuid())).send(fsPlayer);
            }, removeDelay, TimeUnit.MILLISECONDS);
        }

        // Send bundled packets atomically (crucial for player NPCs)
        FancySitula.PACKET_FACTORY.createBundlePacket(packets).send(fsPlayer);

        // Send entity data after bundle
        FancySitula.ENTITY_FACTORY.setEntityDataFor(fsPlayer, fsEntity);

        // Send scale attribute (always send so scale 1.0 can reset from other values)
        if (isLivingEntity(data.getType())) {
            List<FS_ClientboundUpdateAttributesPacket.AttributeSnapshot> attributes = new ArrayList<>();
            attributes.add(new FS_ClientboundUpdateAttributesPacket.AttributeSnapshot("minecraft:scale", data.getScale()));

            // Hide health from indicator mods for fake players (not Mannequin)
            // Mannequin entities don't have this issue as they don't use PlayerInfo
            if (data.getType() == EntityType.PLAYER && !usingMannequin) {
                attributes.add(new FS_ClientboundUpdateAttributesPacket.AttributeSnapshot("minecraft:generic.max_health", 1.0));
            }

            FancySitula.PACKET_FACTORY.createUpdateAttributesPacket(fsEntity.getId(), attributes).send(fsPlayer);
        }

        // Sync Text Display state BEFORE update() so team packet uses correct settings
        syncTextDisplayName(player);
        update(player);

        // Spawn Text Display for multi-line display names
        spawnTextDisplay(fsPlayer);
    }

    /**
     * Creates a PlayerInfoUpdate packet using FancySitula
     */
    private FS_ClientboundPacket createPlayerInfoPacket(Player viewer, boolean isSpawning) {
        if (data.getType() != EntityType.PLAYER || fsEntity == null) {
            return null;
        }

        EnumSet<FS_ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.noneOf(FS_ClientboundPlayerInfoUpdatePacket.Action.class);

        if (isSpawning) {
            actions.add(FS_ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        }
        actions.add(FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);
        if (data.isShowInTab()) {
            actions.add(FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED);
        }

        FS_GameProfile profile;
        if (!data.isMirrorSkin()) {
            Map<String, FS_GameProfile.Property> properties = new HashMap<>();
            if (data.getSkinData() != null && data.getSkinData().hasTexture()) {
                properties.put("textures", new FS_GameProfile.Property(
                        "textures",
                        data.getSkinData().getTextureValue(),
                        data.getSkinData().getTextureSignature()
                ));
            }
            profile = new FS_GameProfile(fsEntity.getUuid(), localName, properties);
        } else {
            profile = FS_GameProfile.fromBukkit(viewer.getPlayerProfile());
            profile.setUUID(fsEntity.getUuid());
            profile.setName(localName);
        }

        Component displayNameComponent = ModernChatColorHandler.translate(data.getDisplayName(), viewer);

        FS_ClientboundPlayerInfoUpdatePacket.Entry entry = new FS_ClientboundPlayerInfoUpdatePacket.Entry(
                fsEntity.getUuid(),
                profile,
                data.isShowInTab(),
                0,
                FS_GameType.CREATIVE,
                displayNameComponent
        );

        return FancySitula.PACKET_FACTORY.createPlayerInfoUpdatePacket(actions, List.of(entry));
    }

    @Override
    public void remove(Player player) {
        if (fsEntity == null) {
            return;
        }

        if (!new NpcDespawnEvent(this, player).callEvent()) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        // For PLAYER NPCs (not Mannequin), remove from player list
        // Mannequin entities are not in the player list
        if (data.getType() == EntityType.PLAYER && !usingMannequin) {
            FancySitula.PACKET_FACTORY.createPlayerInfoRemovePacket(List.of(fsEntity.getUuid())).send(fsPlayer);
        }

        // Remove the entity
        FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, fsEntity);

        // Remove sitting vehicle if present
        if (sittingVehicle != null) {
            FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, sittingVehicle);
        }

        // Remove Text Display for multi-line display names
        removeTextDisplay(fsPlayer);

        isVisibleForPlayer.put(player.getUniqueId(), false);
        // NOTE: Do NOT reset isTeamCreated here - team state is preserved across remove/spawn cycles
        // just like the original implementation. The team will be updated (not recreated) on next spawn.
    }

    @Override
    public void lookAt(Player player, Location location) {
        if (fsEntity == null || !isVisibleForPlayer.getOrDefault(player.getUniqueId(), false)) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        Location loc = data.getLocation();

        FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                fsEntity.getId(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                location.getYaw(),
                location.getPitch(),
                false
        ).send(fsPlayer);

        FancySitula.PACKET_FACTORY.createRotateHeadPacket(fsEntity.getId(), location.getYaw()).send(fsPlayer);
    }

    @Override
    public void update(Player player, boolean swingArm) {
        if (fsEntity == null || !isVisibleForPlayer.getOrDefault(player.getUniqueId(), false)) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        syncWithData();

        // Sync Text Display state BEFORE team packet so correct settings are used
        boolean wasUsingTextDisplay = usingTextDisplayForName;
        syncTextDisplayName(player);

        // Note: Custom name for non-player entities is handled differently
        // The display name is shown via team packet, not entity custom name
        // Setting custom name here would require vanilla Component conversion which is complex

        sendTeamPacket(fsPlayer, player);

        // Send PlayerInfo updates for PLAYER NPCs (not Mannequin)
        // Mannequin skin is handled via DATA_PROFILE entity data
        if (data.getType() == EntityType.PLAYER && !usingMannequin) {
            sendPlayerInfo(fsPlayer, player, false);
        }

        // Handle Text Display spawning/updating after team packet
        if (usingTextDisplayForName) {
            if (!wasUsingTextDisplay) {
                // Switched to Text Display - spawn it
                spawnTextDisplay(fsPlayer);
            } else {
                // Already using Text Display - update its data
                FancySitula.ENTITY_FACTORY.setEntityDataFor(fsPlayer, displayNameTextDisplay);
            }
        }

        // Handle equipment for player-like entities (FS_Player and FS_Mannequin)
        if (fsEntity instanceof FS_Player fsPlayerEntity) {
            if (data.getEquipment() != null && !data.getEquipment().isEmpty()) {
                updateEquipment(fsPlayerEntity);
                FancySitula.ENTITY_FACTORY.setEntityEquipmentFor(fsPlayer, fsPlayerEntity);
            }
        } else if (fsEntity instanceof FS_Mannequin fsMannequin) {
            if (data.getEquipment() != null && !data.getEquipment().isEmpty()) {
                updateMannequinEquipment(fsMannequin);
                Map<FS_EquipmentSlot, ItemStack> equipment = fsMannequin.getEquipment();
                if (!equipment.isEmpty()) {
                    FancySitula.PACKET_FACTORY.createSetEquipmentPacket(fsEntity.getId(), new HashMap<>(equipment)).send(fsPlayer);
                }
            }
        } else if (data.getEquipment() != null && !data.getEquipment().isEmpty()) {
            Map<FS_EquipmentSlot, ItemStack> equipment = new HashMap<>();
            for (Map.Entry<NpcEquipmentSlot, ItemStack> entry : data.getEquipment().entrySet()) {
                FS_EquipmentSlot slot = convertEquipmentSlot(entry.getKey());
                if (slot != null) {
                    equipment.put(slot, entry.getValue());
                }
            }
            if (!equipment.isEmpty()) {
                FancySitula.PACKET_FACTORY.createSetEquipmentPacket(fsEntity.getId(), equipment).send(fsPlayer);
            }
        }

        // Send scale packet first (before attributes that might fail)
        // Always send so scale 1.0 can reset from other values
        if (isLivingEntity(data.getType())) {
            List<FS_ClientboundUpdateAttributesPacket.AttributeSnapshot> attributes = new ArrayList<>();
            attributes.add(new FS_ClientboundUpdateAttributesPacket.AttributeSnapshot("minecraft:scale", data.getScale()));

            // Hide health from indicator mods for fake players (not Mannequin)
            if (data.getType() == EntityType.PLAYER && !usingMannequin) {
                attributes.add(new FS_ClientboundUpdateAttributesPacket.AttributeSnapshot("minecraft:generic.max_health", 1.0));
            }

            FancySitula.PACKET_FACTORY.createUpdateAttributesPacket(fsEntity.getId(), attributes).send(fsPlayer);
        }

        // Handle sitting pose using FancySitula
        NpcAttribute poseAttr = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(EntityType.PLAYER, "pose");
        if (poseAttr != null && data.getAttributes().containsKey(poseAttr)) {
            String pose = data.getAttributes().get(poseAttr);
            if (pose.equals("sitting")) {
                setSitting(fsPlayer);
            } else if (sittingVehicle != null) {
                FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, sittingVehicle);
                sittingVehicle = null;
            }
        }

        // Apply attributes using FancySitula packet-based system
        NpcAttributeHandler.applyAllAttributes(fsEntity, data, fsPlayer);

        FancySitula.ENTITY_FACTORY.setEntityDataFor(fsPlayer, fsEntity);

        // Handle baby attribute for ageable mobs (needs separate packet with specific accessor)
        if (isAgeableMob(data.getType())) {
            NpcAttribute babyAttr = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(data.getType(), "baby");
            if (babyAttr != null && data.getAttributes().containsKey(babyAttr)) {
                boolean isBaby = Boolean.parseBoolean(data.getAttributes().get(babyAttr));
                sendBabyAttribute(fsPlayer, isBaby);
            }
        }

        if (data.isSpawnEntity() && data.getLocation() != null) {
            move(player, swingArm);
        }
    }

    private void syncWithData() {
        if (fsEntity == null) {
            return;
        }

        Location loc = data.getLocation();
        if (loc != null) {
            fsEntity.setLocation(loc);
            fsEntity.setHeadYaw(loc.getYaw());
        }

        fsEntity.setNoGravity(true);
        fsEntity.setSilent(true);

        // For non-player entities, set custom name via entity data
        // Player entities use team packets instead
        // Multi-line or scaled display names use Text Display instead of native nametag
        if (data.getType() != EntityType.PLAYER) {
            String displayName = data.getDisplayName();
            float displayNameScale = data.getDisplayNameScale();
            boolean useNativeCustomName = displayName != null &&
                    !displayName.equalsIgnoreCase("<empty>") &&
                    !isMultiLineDisplayName(displayName) &&
                    displayNameScale == 1.0f; // Only use native name when scale is default

            if (useNativeCustomName) {
                // Single line, default scale - use native custom name
                Component nameComponent = ModernChatColorHandler.translate(displayName);
                fsEntity.setCustomName(Optional.of(nameComponent));
                fsEntity.setCustomNameVisible(true);
            } else {
                // Multi-line, scaled, or empty - hide native name (Text Display will be used)
                fsEntity.setCustomName(Optional.empty());
                fsEntity.setCustomNameVisible(false);
            }
        }

        // Apply shared flags based on attributes and settings
        byte sharedFlags = 0;

        // Glowing flag (0x40)
        if (data.isGlowing()) {
            sharedFlags |= 0x40;
        }

        // Check for on_fire attribute (0x01)
        NpcAttribute onFireAttr = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(data.getType(), "on_fire");
        if (onFireAttr != null && data.getAttributes().containsKey(onFireAttr)) {
            if (Boolean.parseBoolean(data.getAttributes().get(onFireAttr))) {
                sharedFlags |= 0x01;
            }
        }

        // Check for invisible attribute (0x20)
        NpcAttribute invisibleAttr = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(data.getType(), "invisible");
        if (invisibleAttr != null && data.getAttributes().containsKey(invisibleAttr)) {
            if (Boolean.parseBoolean(data.getAttributes().get(invisibleAttr))) {
                sharedFlags |= 0x20;
            }
        }

        fsEntity.setSharedFlags(sharedFlags);

        // Check for shaking attribute (ticks frozen)
        NpcAttribute shakingAttr = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(data.getType(), "shaking");
        if (shakingAttr != null && data.getAttributes().containsKey(shakingAttr)) {
            if (Boolean.parseBoolean(data.getAttributes().get(shakingAttr))) {
                fsEntity.setTicksFrozen(140); // Enough ticks to show shaking effect
            } else {
                fsEntity.setTicksFrozen(0);
            }
        }

        // For Mannequin entities, set the profile for skin support
        if (fsEntity instanceof FS_Mannequin fsMannequin) {
            // Set profile from skin data
            Map<String, FS_GameProfile.Property> properties = new HashMap<>();
            FS_GameProfile profile;

            if (data.getSkinData() != null && data.getSkinData().isTexturePackSkin()) {
                // Use resource pack texture assets (1.21.11+ Mannequin feature)
                // The identifier contains the texture asset path (e.g., "myserver:skins/npc_guard")
                profile = new FS_GameProfile(fsEntity.getUuid(), localName, properties);
                profile.setSkinTextureAsset(data.getSkinData().getIdentifier());
                profile.setCapeTextureAsset(data.getSkinData().getCapeTextureAsset());
                profile.setElytraTextureAsset(data.getSkinData().getElytraTextureAsset());
                // Set model type based on variant
                profile.setModelType(data.getSkinData().getVariant() == SkinData.SkinVariant.SLIM ? "SLIM" : "DEFAULT");
            } else if (data.getSkinData() != null && data.getSkinData().hasTexture()) {
                // Use traditional Mojang-signed texture properties
                properties.put("textures", new FS_GameProfile.Property(
                        "textures",
                        data.getSkinData().getTextureValue(),
                        data.getSkinData().getTextureSignature()
                ));
                profile = new FS_GameProfile(fsEntity.getUuid(), localName, properties);
            } else {
                // No skin data
                profile = new FS_GameProfile(fsEntity.getUuid(), localName, properties);
            }

            fsMannequin.setProfile(profile);

            // Set immovable to true for NPCs
            fsMannequin.setImmovable(true);

            // Set skin customization (all layers visible by default)
            fsMannequin.setSkinCustomization((byte) 0x7F);

            // Apply Mannequin pose from attributes
            // The pose attribute is registered for PLAYER type when Mannequin is available
            NpcAttribute poseAttr = FancyNpcsPlugin.get().getAttributeManager().getAttributeByName(EntityType.PLAYER, "pose");
            if (poseAttr != null && data.getAttributes().containsKey(poseAttr)) {
                String pose = data.getAttributes().get(poseAttr);
                fsMannequin.setPose(pose);
            }

            // Always hide Mannequin's native description - we use Text Display for all display names
            // The native description appears at a fixed position that can't be controlled,
            // so Text Display gives us proper positioning above the entity's head
            fsMannequin.setDescription(Optional.empty());
        }
    }

    private void updateEquipment(FS_Player fsPlayerEntity) {
        if (data.getEquipment() == null || data.getEquipment().isEmpty()) {
            return;
        }

        for (Map.Entry<NpcEquipmentSlot, ItemStack> entry : data.getEquipment().entrySet()) {
            FS_EquipmentSlot slot = convertEquipmentSlot(entry.getKey());
            if (slot != null) {
                fsPlayerEntity.setEquipment(slot, entry.getValue());
            }
        }
    }

    private void updateMannequinEquipment(FS_Mannequin fsMannequin) {
        if (data.getEquipment() == null || data.getEquipment().isEmpty()) {
            return;
        }

        for (Map.Entry<NpcEquipmentSlot, ItemStack> entry : data.getEquipment().entrySet()) {
            FS_EquipmentSlot slot = convertEquipmentSlot(entry.getKey());
            if (slot != null) {
                fsMannequin.setEquipment(slot, entry.getValue());
            }
        }
    }

    @Override
    protected void refreshEntityData(Player player) {
        if (fsEntity == null || !isVisibleForPlayer.getOrDefault(player.getUniqueId(), false)) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        FancySitula.ENTITY_FACTORY.setEntityDataFor(fsPlayer, fsEntity);
    }

    @Override
    public void move(Player player, boolean swingArm) {
        if (fsEntity == null || !isVisibleForPlayer.getOrDefault(player.getUniqueId(), false)) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);
        Location loc = data.getLocation();

        FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                fsEntity.getId(),
                loc.getX(),
                loc.getY(),
                loc.getZ(),
                loc.getYaw(),
                loc.getPitch(),
                false
        ).send(fsPlayer);

        FancySitula.PACKET_FACTORY.createRotateHeadPacket(fsEntity.getId(), loc.getYaw()).send(fsPlayer);

        if (swingArm && data.getType() == EntityType.PLAYER) {
            FancySitula.PACKET_FACTORY.createAnimatePacket(
                    fsEntity.getId(),
                    FS_ClientboundAnimatePacket.SWING_MAIN_ARM
            ).send(fsPlayer);
        }

        // Update Text Display position (for multi-line display names)
        updateTextDisplayPosition(fsPlayer);
    }

    @Override
    public float getEyeHeight() {
        return data.getType() == EntityType.PLAYER ? 1.62f : 1.0f;
    }

    @Override
    public int getEntityId() {
        return fsEntity != null ? fsEntity.getId() : -1;
    }

    private void sendPlayerInfo(FS_RealPlayer fsPlayer, Player viewer, boolean isSpawning) {
        FS_ClientboundPacket packet = createPlayerInfoPacket(viewer, isSpawning);
        if (packet != null) {
            packet.send(fsPlayer);
        }
    }

    private void sendTeamPacket(FS_RealPlayer fsPlayer, Player viewer) {
        if (fsEntity == null) {
            return;
        }

        String teamName = "npc-" + localName.substring(0, Math.min(localName.length(), 12));
        // For team membership: Players use name, all other entities (including Mannequin) use UUID
        String entityName = (data.getType() == EntityType.PLAYER && !usingMannequin) ? localName : fsEntity.getUuid().toString();

        FS_Color glowColor = convertToFSColor(data.getGlowingColor());
        Component displayNameComponent = data.getDisplayName().equalsIgnoreCase("<empty>")
                ? Component.empty()
                : ModernChatColorHandler.translate(data.getDisplayName(), viewer);

        // For non-player entities: empty prefix (custom name via entity data)
        // For Mannequin: empty prefix (uses Text Display for display names)
        // For fake player (FS_Player): display name as prefix unless using Text Display for scaling or multi-line
        Component teamPrefix;
        float displayNameScale = data.getDisplayNameScale();
        boolean usingTextDisplayForScaling = displayNameScale != 1.0f;

        if (data.getType() != EntityType.PLAYER) {
            teamPrefix = Component.empty();
        } else if (usingMannequin || usingTextDisplayForScaling || usingTextDisplayForName) {
            // Mannequin uses Text Display, not team prefix
            // Scaled or multi-line display names use Text Display instead of team prefix
            teamPrefix = Component.empty();
        } else {
            teamPrefix = displayNameComponent;
        }

        // Hide team nametag when using Text Display (for Mannequin, scaled names, multi-line, or empty names)
        FS_NameTagVisibility visibility;
        if (usingMannequin || data.getDisplayName().equalsIgnoreCase("<empty>") || usingTextDisplayForScaling || usingTextDisplayForName) {
            visibility = FS_NameTagVisibility.NEVER;
        } else {
            visibility = FS_NameTagVisibility.ALWAYS;
        }

        FS_CollisionRule collision = data.isCollidable()
                ? FS_CollisionRule.ALWAYS
                : FS_CollisionRule.NEVER;

        boolean isTeamCreatedForPlayer = isTeamCreated.getOrDefault(viewer.getUniqueId(), false);

        if (!isTeamCreatedForPlayer) {
            FS_ClientboundCreateOrUpdateTeamPacket.CreateTeam createTeam = new FS_ClientboundCreateOrUpdateTeamPacket.CreateTeam(
                    displayNameComponent,
                    false,
                    false,
                    visibility,
                    collision,
                    glowColor,
                    teamPrefix,
                    Component.empty(),
                    List.of(entityName)
            );
            FancySitula.PACKET_FACTORY.createCreateOrUpdateTeamPacket(teamName, createTeam).send(fsPlayer);
            isTeamCreated.put(viewer.getUniqueId(), true);
        } else {
            FS_ClientboundCreateOrUpdateTeamPacket.UpdateTeam updateTeam = new FS_ClientboundCreateOrUpdateTeamPacket.UpdateTeam(
                    displayNameComponent,
                    false,
                    false,
                    visibility,
                    collision,
                    glowColor,
                    teamPrefix,
                    Component.empty()
            );
            FancySitula.PACKET_FACTORY.createCreateOrUpdateTeamPacket(teamName, updateTeam).send(fsPlayer);
        }
    }

    private void setSitting(FS_RealPlayer fsPlayer) {
        if (fsEntity == null) {
            return;
        }

        if (sittingVehicle == null) {
            sittingVehicle = new FS_Entity(EntityType.TEXT_DISPLAY);
        }

        Location loc = data.getLocation();
        sittingVehicle.setLocation(loc);

        FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, sittingVehicle);

        FancySitula.PACKET_FACTORY.createSetPassengersPacket(
                sittingVehicle.getId(),
                List.of(fsEntity.getId())
        ).send(fsPlayer);
    }

    private FS_EquipmentSlot convertEquipmentSlot(NpcEquipmentSlot slot) {
        return switch (slot) {
            case MAINHAND -> FS_EquipmentSlot.MAINHAND;
            case OFFHAND -> FS_EquipmentSlot.OFFHAND;
            case HEAD -> FS_EquipmentSlot.HEAD;
            case CHEST -> FS_EquipmentSlot.CHEST;
            case LEGS -> FS_EquipmentSlot.LEGS;
            case FEET -> FS_EquipmentSlot.FEET;
            case BODY -> FS_EquipmentSlot.BODY;
            case SADDLE -> FS_EquipmentSlot.SADDLE;
        };
    }

    private FS_Color convertToFSColor(NamedTextColor color) {
        if (color == null) return FS_Color.WHITE;
        return switch (color.toString()) {
            case "black" -> FS_Color.BLACK;
            case "dark_blue" -> FS_Color.DARK_BLUE;
            case "dark_green" -> FS_Color.DARK_GREEN;
            case "dark_aqua" -> FS_Color.DARK_AQUA;
            case "dark_red" -> FS_Color.DARK_RED;
            case "dark_purple" -> FS_Color.DARK_PURPLE;
            case "gold" -> FS_Color.GOLD;
            case "gray" -> FS_Color.GRAY;
            case "dark_gray" -> FS_Color.DARK_GRAY;
            case "blue" -> FS_Color.BLUE;
            case "green" -> FS_Color.GREEN;
            case "aqua" -> FS_Color.AQUA;
            case "red" -> FS_Color.RED;
            case "light_purple" -> FS_Color.LIGHT_PURPLE;
            case "yellow" -> FS_Color.YELLOW;
            default -> FS_Color.WHITE;
        };
    }

    private boolean isLivingEntity(EntityType type) {
        return switch (type) {
            case ARMOR_STAND, ITEM_FRAME, GLOW_ITEM_FRAME, PAINTING, END_CRYSTAL,
                 ENDER_PEARL, EXPERIENCE_BOTTLE, EYE_OF_ENDER, FIREWORK_ROCKET,
                 FISHING_BOBBER, ARROW, SPECTRAL_ARROW, SNOWBALL, EGG, TRIDENT,
                 LLAMA_SPIT, SMALL_FIREBALL, FIREBALL, DRAGON_FIREBALL, WITHER_SKULL,
                 SHULKER_BULLET, TNT, FALLING_BLOCK, ITEM, AREA_EFFECT_CLOUD,
                 LIGHTNING_BOLT, EXPERIENCE_ORB, MARKER, BLOCK_DISPLAY, ITEM_DISPLAY,
                 TEXT_DISPLAY, INTERACTION, MINECART, CHEST_MINECART, COMMAND_BLOCK_MINECART,
                 FURNACE_MINECART, HOPPER_MINECART, SPAWNER_MINECART, TNT_MINECART,
                 LEASH_KNOT, EVOKER_FANGS -> false;
            default -> type.getEntityClass() != null && org.bukkit.entity.LivingEntity.class.isAssignableFrom(type.getEntityClass());
        };
    }

    public UUID getUuid() {
        return fsEntity != null ? fsEntity.getUuid() : null;
    }

    public String getLocalName() {
        return localName;
    }

    private boolean isAgeableMob(EntityType type) {
        return type.getEntityClass() != null && org.bukkit.entity.Ageable.class.isAssignableFrom(type.getEntityClass());
    }

    private void sendBabyAttribute(FS_RealPlayer fsPlayer, boolean isBaby) {
        if (fsEntity == null) return;

        // AgeableMob's DATA_BABY_ID accessor
        FS_ClientboundSetEntityDataPacket.EntityDataAccessor babyAccessor =
                new FS_ClientboundSetEntityDataPacket.EntityDataAccessor(
                        "net.minecraft.world.entity.AgeableMob",
                        "DATA_BABY_ID"
                );

        List<FS_ClientboundSetEntityDataPacket.EntityData> entityData = List.of(
                new FS_ClientboundSetEntityDataPacket.EntityData(babyAccessor, isBaby)
        );

        FancySitula.PACKET_FACTORY.createSetEntityDataPacket(fsEntity.getId(), entityData).send(fsPlayer);
    }

    /**
     * Checks if the display name contains multiple lines.
     * Multi-line display names use Text Display entities instead of native nametags.
     */
    private boolean isMultiLineDisplayName(String displayName) {
        if (displayName == null || displayName.equalsIgnoreCase("<empty>")) {
            return false;
        }
        // Check for newline characters (supports \n, <br>, <newline>)
        return displayName.contains("\n") || displayName.contains("<br>") || displayName.contains("<newline>");
    }

    /**
     * Gets the current pose of the Mannequin, or null if not using Mannequin.
     */
    private String getCurrentMannequinPose() {
        if (!usingMannequin || !(fsEntity instanceof FS_Mannequin fsMannequin)) {
            return null;
        }
        return fsMannequin.getPose();
    }

    /**
     * Calculates the Y offset for display name based on entity type and pose.
     * For multi-line Text Displays, this positions them correctly above the entity.
     */
    private float getDisplayNameYOffset() {
        EntityType entityType = data.getType();

        // Handle Mannequin poses
        if (usingMannequin) {
            String pose = getCurrentMannequinPose();
            if (pose != null) {
                return switch (pose.toUpperCase()) {
                    case "STANDING" -> 2.1f;
                    case "CROUCHING", "SNEAKING" -> 1.6f;
                    case "SWIMMING", "FALL_FLYING" -> 0.7f;
                    case "SLEEPING" -> 0.4f;
                    default -> 2.1f;
                };
            }
            return 2.1f;
        }

        // Handle Player type (fake players)
        if (entityType == EntityType.PLAYER) {
            return 2.1f;
        }

        // Entity-specific heights (approximate hitbox heights + small offset for nametag)
        return switch (entityType) {
            // Tall entities
            case IRON_GOLEM -> 2.9f;
            case ENDERMAN -> 3.0f;
            case WITHER -> 3.7f;
            case ENDER_DRAGON -> 4.0f;
            case RAVAGER -> 2.4f;
            case ELDER_GUARDIAN -> 2.2f;
            case WARDEN -> 3.0f;
            case CAMEL -> 2.6f;

            // Medium entities (player-sized)
            case ZOMBIE, SKELETON, HUSK, STRAY, DROWNED, ZOMBIFIED_PIGLIN,
                 PIGLIN, PIGLIN_BRUTE, WITCH, EVOKER, VINDICATOR, ILLUSIONER,
                 PILLAGER, WANDERING_TRADER, VILLAGER, ZOMBIE_VILLAGER,
                 BLAZE, BREEZE, WITHER_SKELETON -> 2.1f;

            // Small-medium entities
            case CREEPER, SPIDER, CAVE_SPIDER, WOLF, OCELOT, CAT,
                 FOX, PANDA, POLAR_BEAR, SHEEP, PIG, COW, MOOSHROOM,
                 HORSE, DONKEY, MULE, LLAMA, TRADER_LLAMA, GOAT -> 1.5f;

            // Small entities
            case CHICKEN, RABBIT, PARROT, BAT, BEE, SILVERFISH,
                 ENDERMITE, VEX, ALLAY, FROG, TADPOLE, ARMADILLO -> 0.8f;

            // Very small
            case SLIME, MAGMA_CUBE -> 1.0f; // Varies by size

            // Aquatic
            case SQUID, GLOW_SQUID, DOLPHIN, TURTLE -> 1.0f;
            case GUARDIAN -> 1.2f;
            case AXOLOTL, COD, SALMON, PUFFERFISH, TROPICAL_FISH -> 0.6f;

            // Default for unknown entities
            default -> 1.5f;
        };
    }

    /**
     * Initializes or updates the Text Display entity for display names.
     * Uses Text Display for:
     * - Multi-line display names (all entities)
     * - All Mannequin display names (native description position can't be controlled)
     * - Scaled display names (displayNameScale != 1.0)
     */
    private void syncTextDisplayName(Player viewer) {
        String displayName = data.getDisplayName();
        float displayNameScale = data.getDisplayNameScale();

        // Check if we need a Text Display:
        // 1. Multi-line display names always use Text Display
        // 2. Mannequin always uses Text Display (native description position can't be controlled)
        // 3. Scaled display names always use Text Display (native nametags can't be scaled)
        boolean hasDisplayName = displayName != null && !displayName.equalsIgnoreCase("<empty>");
        boolean needsTextDisplay = (hasDisplayName && isMultiLineDisplayName(displayName)) ||
                (usingMannequin && hasDisplayName) ||
                (hasDisplayName && displayNameScale != 1.0f);

        if (!needsTextDisplay) {
            // Clean up Text Display if it exists but is no longer needed
            if (displayNameTextDisplay != null && usingTextDisplayForName) {
                FS_RealPlayer fsPlayer = new FS_RealPlayer(viewer);
                FancySitula.PACKET_FACTORY.createRemoveEntitiesPacket(List.of(displayNameTextDisplay.getId())).send(fsPlayer);
            }
            usingTextDisplayForName = false;
            displayNameTextDisplay = null;
            return;
        }

        // Initialize Text Display if needed
        if (displayNameTextDisplay == null) {
            displayNameTextDisplay = new FS_TextDisplay();
        }
        usingTextDisplayForName = true;

        // Replace <br> and <newline> with actual newlines for display
        String processedName = displayName
                .replace("<br>", "\n")
                .replace("<newline>", "\n");

        // Convert to Adventure Component with centered text alignment
        Component textComponent = ModernChatColorHandler.translate(processedName, viewer);

        // Configure the Text Display to look like vanilla nametags
        displayNameTextDisplay.setText(textComponent);
        displayNameTextDisplay.setBillboard(FS_Display.Billboard.CENTER); // Always face player
        displayNameTextDisplay.setShadow(true);
        displayNameTextDisplay.setUseDefaultBackground(true); // Use vanilla-style semi-transparent background
        displayNameTextDisplay.setLineWidth(200); // Default line width
        displayNameTextDisplay.setTextOpacity((byte) -1); // Fully opaque

        // Position above the entity based on entity type, pose, and scale
        float yOffset = getDisplayNameYOffset();

        // Account for entity scale
        float entityScale = data.getScale();
        if (entityScale > 0 && entityScale != 1.0f) {
            yOffset *= entityScale;
        }

        // Add additional offset for vanilla-like nametag positioning (slightly above the head)
        // Vanilla nametags appear about 0.2 blocks above the entity's top
        yOffset += 0.25f;

        Location loc = data.getLocation();
        if (loc != null) {
            displayNameTextDisplay.setLocation(loc.getX(), loc.getY() + yOffset, loc.getZ());
        }

        // Apply display name scale (base scale is 0.5 for vanilla-like appearance)
        // When displayNameScale is 1.0 (default), the Text Display uses 0.5 scale (vanilla nametag size)
        // When displayNameScale is 2.0, Text Display uses 1.0 scale (twice vanilla size)
        float baseScale = 0.5f * displayNameScale;
        displayNameTextDisplay.setScale(new Vector3f(baseScale, baseScale, baseScale));
        // Use translation to center the text display visually
        displayNameTextDisplay.setTranslation(new Vector3f(0, 0, 0));
    }

    /**
     * Spawns the Text Display entity for the viewer.
     */
    private void spawnTextDisplay(FS_RealPlayer fsPlayer) {
        if (!usingTextDisplayForName || displayNameTextDisplay == null) {
            return;
        }

        // Spawn the Text Display entity
        FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, displayNameTextDisplay);
    }

    /**
     * Removes the Text Display entity for the viewer.
     */
    private void removeTextDisplay(FS_RealPlayer fsPlayer) {
        if (displayNameTextDisplay == null) {
            return;
        }
        FancySitula.PACKET_FACTORY.createRemoveEntitiesPacket(List.of(displayNameTextDisplay.getId())).send(fsPlayer);
    }

    /**
     * Updates the Text Display position when the NPC moves or changes pose.
     */
    private void updateTextDisplayPosition(FS_RealPlayer fsPlayer) {
        if (!usingTextDisplayForName || displayNameTextDisplay == null) {
            return;
        }

        float yOffset = getDisplayNameYOffset();

        // Account for entity scale
        float scale = data.getScale();
        if (scale > 0 && scale != 1.0f) {
            yOffset *= scale;
        }

        // Add additional offset for vanilla-like nametag positioning
        yOffset += 0.25f;

        Location loc = data.getLocation();
        if (loc != null) {
            FancySitula.PACKET_FACTORY.createTeleportEntityPacket(
                    displayNameTextDisplay.getId(),
                    loc.getX(),
                    loc.getY() + yOffset,
                    loc.getZ(),
                    0, 0, false
            ).send(fsPlayer);
        }
    }
}
