package de.oliver.fancynpcs.npc;

import de.oliver.fancylib.RandomUtils;
import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.events.NpcSpawnEvent;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import de.oliver.fancysitula.api.entities.FS_Entity;
import de.oliver.fancysitula.api.entities.FS_Player;
import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundAnimatePacket;
import de.oliver.fancysitula.api.packets.FS_ClientboundCreateOrUpdateTeamPacket;
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

import java.util.*;
import java.util.concurrent.TimeUnit;

public class NpcImpl extends Npc {

    private static final char[] LOCAL_NAME_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'k', 'l', 'm', 'n', 'o', 'r'};

    private final String localName;
    private FS_Entity fsEntity;
    private FS_Entity sittingVehicle;

    public NpcImpl(NpcData data) {
        super(data);
        this.localName = generateLocalName();
        initEntity();
    }

    private void initEntity() {
        if (data.getType() == EntityType.PLAYER) {
            this.fsEntity = new FS_Player();
        } else {
            this.fsEntity = new FS_Entity(data.getType());
        }

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

        if (data.getType() == EntityType.PLAYER) {
            sendPlayerInfo(fsPlayer, player, true);
        }

        FancySitula.ENTITY_FACTORY.spawnEntityFor(fsPlayer, fsEntity);

        // Send scale attribute immediately after spawn
        if (isLivingEntity(data.getType()) && data.getScale() != 1.0) {
            List<FS_ClientboundUpdateAttributesPacket.AttributeSnapshot> attributes = List.of(
                    new FS_ClientboundUpdateAttributesPacket.AttributeSnapshot("minecraft:scale", data.getScale())
            );
            FancySitula.PACKET_FACTORY.createUpdateAttributesPacket(fsEntity.getId(), attributes).send(fsPlayer);
        }

        isVisibleForPlayer.put(player.getUniqueId(), true);

        int removeDelay = FancyNpcsPlugin.get().getFancyNpcConfig().getRemoveNpcsFromPlayerlistDelay();
        if (data.getType() == EntityType.PLAYER && !data.isShowInTab() && removeDelay > 0) {
            FancyNpcsPlugin.get().getNpcThread().schedule(() -> {
                FancySitula.PACKET_FACTORY.createPlayerInfoRemovePacket(List.of(fsEntity.getUuid())).send(fsPlayer);
            }, removeDelay, TimeUnit.MILLISECONDS);
        }

        update(player);
    }

    @Override
    public void remove(Player player) {
        if (fsEntity == null) {
            return;
        }

        FS_RealPlayer fsPlayer = new FS_RealPlayer(player);

        if (data.getType() == EntityType.PLAYER) {
            FancySitula.PACKET_FACTORY.createPlayerInfoRemovePacket(List.of(fsEntity.getUuid())).send(fsPlayer);
        }

        FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, fsEntity);

        if (sittingVehicle != null) {
            FancySitula.ENTITY_FACTORY.despawnEntityFor(fsPlayer, sittingVehicle);
        }

        isVisibleForPlayer.put(player.getUniqueId(), false);
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

        // Note: Custom name for non-player entities is handled differently
        // The display name is shown via team packet, not entity custom name
        // Setting custom name here would require vanilla Component conversion which is complex

        sendTeamPacket(fsPlayer, player);

        if (data.getType() == EntityType.PLAYER) {
            sendPlayerInfo(fsPlayer, player, false);
        }

        if (fsEntity instanceof FS_Player fsPlayerEntity) {
            if (data.getEquipment() != null && !data.getEquipment().isEmpty()) {
                updateEquipment(fsPlayerEntity);
                FancySitula.ENTITY_FACTORY.setEntityEquipmentFor(fsPlayer, fsPlayerEntity);
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
        if (isLivingEntity(data.getType()) && data.getScale() != 1.0) {
            List<FS_ClientboundUpdateAttributesPacket.AttributeSnapshot> attributes = List.of(
                    new FS_ClientboundUpdateAttributesPacket.AttributeSnapshot("minecraft:scale", data.getScale())
            );
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
        if (data.getType() != EntityType.PLAYER) {
            String displayName = data.getDisplayName();
            if (displayName != null && !displayName.equalsIgnoreCase("<empty>")) {
                Component nameComponent = ModernChatColorHandler.translate(displayName);
                fsEntity.setCustomName(Optional.of(nameComponent));
                fsEntity.setCustomNameVisible(true);
            } else {
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
        if (data.getType() != EntityType.PLAYER || fsEntity == null) {
            return;
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

        FancySitula.PACKET_FACTORY.createPlayerInfoUpdatePacket(actions, List.of(entry)).send(fsPlayer);
    }

    private void sendTeamPacket(FS_RealPlayer fsPlayer, Player viewer) {
        if (fsEntity == null) {
            return;
        }

        String teamName = "npc-" + localName.substring(0, Math.min(localName.length(), 12));
        String entityName = data.getType() == EntityType.PLAYER ? localName : fsEntity.getUuid().toString();

        FS_Color glowColor = convertToFSColor(data.getGlowingColor());
        Component displayNameComponent = data.getDisplayName().equalsIgnoreCase("<empty>")
                ? Component.empty()
                : ModernChatColorHandler.translate(data.getDisplayName(), viewer);

        // For non-player entities, use empty prefix since custom name is set via entity data
        // For player entities, use the display name as prefix (shown above the player's head)
        Component teamPrefix = data.getType() == EntityType.PLAYER ? displayNameComponent : Component.empty();

        FS_NameTagVisibility visibility = data.getDisplayName().equalsIgnoreCase("<empty>")
                ? FS_NameTagVisibility.NEVER
                : FS_NameTagVisibility.ALWAYS;

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
}
