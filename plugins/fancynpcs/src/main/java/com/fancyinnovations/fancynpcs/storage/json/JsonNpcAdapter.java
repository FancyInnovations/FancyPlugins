package com.fancyinnovations.fancynpcs.storage.json;

import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.NpcAttribute;
import com.fancyinnovations.fancynpcs.api.NpcData;
import com.fancyinnovations.fancynpcs.api.actions.ActionTrigger;
import com.fancyinnovations.fancynpcs.api.actions.NpcAction;
import com.fancyinnovations.fancynpcs.api.data.property.NpcVisibility;
import com.fancyinnovations.fancynpcs.api.skins.SkinData;
import com.fancyinnovations.fancynpcs.api.skins.SkinLoadException;
import com.fancyinnovations.fancynpcs.api.utils.NpcEquipmentSlot;
import com.fancyinnovations.fancynpcs.storage.json.model.JsonNpcModel;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JsonNpcAdapter {

    public static JsonNpcModel toJson(NpcData data) {
        // Convert location
        JsonNpcModel.JsonLocation jsonLocation = new JsonNpcModel.JsonLocation(
                data.getLocation().getWorld().getName(),
                data.getLocation().getX(),
                data.getLocation().getY(),
                data.getLocation().getZ(),
                data.getLocation().getYaw(),
                data.getLocation().getPitch()
        );

        // Convert skin
        JsonNpcModel.JsonSkin jsonSkin = null;
        if (data.getSkinData() != null) {
            jsonSkin = new JsonNpcModel.JsonSkin(
                    data.getSkinData().getIdentifier(),
                    data.getSkinData().getVariant().name(),
                    data.getSkinData().isTexturePackSkin() ? true : null,
                    data.getSkinData().getCapeTextureAsset(),
                    data.getSkinData().getElytraTextureAsset()
            );
        }

        // Convert equipment
        Map<String, String> jsonEquipment = new HashMap<>();
        if (data.getEquipment() != null) {
            for (Map.Entry<NpcEquipmentSlot, ItemStack> entry : data.getEquipment().entrySet()) {
                if (entry.getValue() != null) {
                    jsonEquipment.put(
                            entry.getKey().name(),
                            Base64.getEncoder().encodeToString(entry.getValue().serializeAsBytes())
                    );
                }
            }
        }

        // Convert attributes
        Map<String, String> jsonAttributes = new HashMap<>();
        if (data.getAttributes() != null) {
            for (Map.Entry<NpcAttribute, String> entry : data.getAttributes().entrySet()) {
                jsonAttributes.put(entry.getKey().getName(), entry.getValue());
            }
        }

        // Convert actions
        Map<String, List<JsonNpcModel.JsonAction>> jsonActions = new HashMap<>();
        if (data.getActions() != null) {
            for (Map.Entry<ActionTrigger, List<NpcAction.NpcActionData>> entry : data.getActions().entrySet()) {
                List<JsonNpcModel.JsonAction> actionList = entry.getValue().stream()
                        .map(actionData -> new JsonNpcModel.JsonAction(
                                actionData.order(),
                                actionData.action().getName(),
                                actionData.value()
                        ))
                        .collect(Collectors.toList());
                jsonActions.put(entry.getKey().name(), actionList);
            }
        }

        // Convert trigger cooldowns
        Map<String, Float> jsonTriggerCooldowns = new HashMap<>();
        if (data.getTriggerCooldowns() != null) {
            for (Map.Entry<ActionTrigger, Float> entry : data.getTriggerCooldowns().entrySet()) {
                if (entry.getValue() != null && entry.getValue() > 0) {
                    jsonTriggerCooldowns.put(entry.getKey().name(), entry.getValue());
                }
            }
        }

        return new JsonNpcModel(
                data.getId(),
                data.getName(),
                data.getCreator().toString(),
                data.getDisplayName(),
                data.getDisplayNameScale() != 1.0f ? data.getDisplayNameScale() : null,
                data.getType(),
                jsonLocation,
                jsonSkin,
                data.isMirrorSkin(),
                data.isShowInTab(),
                data.isSpawnEntity(),
                data.isCollidable(),
                data.isGlowing(),
                data.getGlowingColor().toString(),
                data.isTurnToPlayer(),
                data.getTurnToPlayerDistance(),
                data.getInteractionCooldown(),
                jsonTriggerCooldowns.isEmpty() ? null : jsonTriggerCooldowns,
                data.getScale(),
                data.getVisibilityDistance(),
                data.getVisibility(),
                jsonEquipment,
                jsonAttributes,
                jsonActions
        );
    }

    public static NpcData fromJson(JsonNpcModel model) {
        // Parse location
        World world = Bukkit.getWorld(model.location().world());
        if (world == null) {
            FancyNpcs.getInstance().getFancyLogger().warn("Could not load NPC '" + model.id() + "', because the world '" + model.location().world() + "' is not loaded");
            return null;
        }

        Location location = new Location(
                world,
                model.location().x(),
                model.location().y(),
                model.location().z(),
                model.location().yaw(),
                model.location().pitch()
        );

        // Parse creator
        UUID creator = null;
        try {
            creator = UUID.fromString(model.creator());
        } catch (Exception e) {
            FancyNpcs.getInstance().getFancyLogger().warn("Invalid creator UUID for NPC '" + model.id() + "': " + model.creator());
        }

        // Parse skin
        SkinData skin = null;
        if (model.skin() != null && model.skin().identifier() != null && !model.skin().identifier().isEmpty()) {
            try {
                SkinData.SkinVariant skinVariant = SkinData.SkinVariant.valueOf(model.skin().variant());

                // Check if this is a texture pack skin
                if (model.skin().texturePackSkin() != null && model.skin().texturePackSkin()) {
                    // Create texture pack skin directly without fetching from SkinManager
                    skin = SkinData.texturePackSkin(
                            model.skin().identifier(),
                            model.skin().capeTextureAsset(),
                            model.skin().elytraTextureAsset(),
                            skinVariant
                    );
                } else {
                    // Traditional skin - fetch from SkinManager
                    skin = FancyNpcs.getInstance().getSkinManagerImpl().getByIdentifier(model.skin().identifier(), skinVariant);
                    skin.setIdentifier(model.skin().identifier());
                }
            } catch (final SkinLoadException e) {
                FancyNpcs.getInstance().getFancyLogger().error("NPC named '" + model.name() + "' identified by '" + model.id() + "' could not have their skin loaded.");
                FancyNpcs.getInstance().getFancyLogger().error("  " + e.getReason() + " " + e.getMessage());
            } catch (IllegalArgumentException e) {
                FancyNpcs.getInstance().getFancyLogger().warn("Invalid skin variant for NPC '" + model.id() + "': " + model.skin().variant());
            }
        }

        // Parse glowing color
        NamedTextColor glowingColor = NamedTextColor.NAMES.value(model.glowingColor() != null ? model.glowingColor() : "white");
        if (glowingColor == null) {
            glowingColor = NamedTextColor.WHITE;
        }

        // Parse equipment
        Map<NpcEquipmentSlot, ItemStack> equipment = new HashMap<>();
        if (model.equipment() != null) {
            for (Map.Entry<String, String> entry : model.equipment().entrySet()) {
                try {
                    NpcEquipmentSlot slot = NpcEquipmentSlot.parse(entry.getKey());
                    if (slot == null) {
                        FancyNpcs.getInstance().getFancyLogger().warn("Could not parse equipment slot '" + entry.getKey() + "' for NPC '" + model.id() + "'");
                        continue;
                    }
                    ItemStack item = ItemStack.deserializeBytes(Base64.getDecoder().decode(entry.getValue()));
                    equipment.put(slot, item);
                } catch (Exception e) {
                    FancyNpcs.getInstance().getFancyLogger().warn("Could not load equipment for slot '" + entry.getKey() + "' for NPC '" + model.id() + "': " + e.getMessage());
                }
            }
        }

        // Parse attributes
        Map<NpcAttribute, String> attributes = new HashMap<>();
        if (model.attributes() != null) {
            for (Map.Entry<String, String> entry : model.attributes().entrySet()) {
                NpcAttribute attribute = FancyNpcs.getInstance().getAttributeManager().getAttributeByName(model.type(), entry.getKey());
                if (attribute == null) {
                    FancyNpcs.getInstance().getFancyLogger().warn("Could not find attribute: " + entry.getKey());
                    continue;
                }

                if (!attribute.isValidValue(entry.getValue())) {
                    FancyNpcs.getInstance().getFancyLogger().warn("Invalid value for attribute: " + entry.getKey());
                    continue;
                }

                attributes.put(attribute, entry.getValue());
            }
        }

        // Parse actions
        Map<ActionTrigger, List<NpcAction.NpcActionData>> actions = new ConcurrentHashMap<>();
        if (model.actions() != null) {
            for (Map.Entry<String, List<JsonNpcModel.JsonAction>> entry : model.actions().entrySet()) {
                ActionTrigger actionTrigger = ActionTrigger.getByName(entry.getKey());
                if (actionTrigger == null) {
                    FancyNpcs.getInstance().getFancyLogger().warn("Could not find action trigger: " + entry.getKey());
                    continue;
                }

                List<NpcAction.NpcActionData> actionList = new ArrayList<>();
                for (JsonNpcModel.JsonAction jsonAction : entry.getValue()) {
                    NpcAction action = FancyNpcs.getInstance().getActionManager().getActionByName(jsonAction.action());
                    if (action == null) {
                        FancyNpcs.getInstance().getFancyLogger().warn("Could not find action: " + jsonAction.action());
                        continue;
                    }

                    actionList.add(new NpcAction.NpcActionData(jsonAction.order(), action, jsonAction.value()));
                }

                actions.put(actionTrigger, actionList);
            }
        }

        // Create NpcData
        NpcData data = new NpcData(
                model.id(),
                model.name(),
                creator,
                model.displayName() != null ? model.displayName() : model.name(),
                skin,
                location,
                model.showInTab() != null ? model.showInTab() : false,
                model.spawnEntity() != null ? model.spawnEntity() : true,
                model.collidable() != null ? model.collidable() : true,
                model.glowing() != null ? model.glowing() : false,
                glowingColor,
                model.type() != null ? model.type() : EntityType.PLAYER,
                equipment,
                model.turnToPlayer() != null ? model.turnToPlayer() : false,
                model.turnToPlayerDistance() != null ? model.turnToPlayerDistance() : -1,
                null,
                actions,
                model.interactionCooldown() != null ? model.interactionCooldown() : 0f,
                model.scale() != null ? model.scale() : 1f,
                model.visibilityDistance() != null ? model.visibilityDistance() : -1,
                attributes,
                model.mirrorSkin() != null ? model.mirrorSkin() : false
        );

        data.setVisibility(model.visibility() != null ? model.visibility() : NpcVisibility.ALL);

        // Load display name scale
        if (model.displayNameScale() != null) {
            data.setDisplayNameScale(model.displayNameScale());
        }

        // Load trigger cooldowns
        if (model.triggerCooldowns() != null) {
            for (Map.Entry<String, Float> entry : model.triggerCooldowns().entrySet()) {
                ActionTrigger trigger = ActionTrigger.getByName(entry.getKey());
                if (trigger != null && entry.getValue() != null && entry.getValue() > 0) {
                    data.setTriggerCooldown(trigger, entry.getValue());
                }
            }
        }

        return data;
    }

    public static List<JsonNpcModel> toJsonList(Collection<Npc> npcs) {
        return npcs.stream()
                .filter(Npc::isSaveToFile)
                .map(npc -> toJson(npc.getData()))
                .collect(Collectors.toList());
    }
}
