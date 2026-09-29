package de.oliver.fancynpcs;

import de.oliver.fancyanalytics.logger.ExtendedFancyLogger;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.api.NpcData;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.types.UnknownActionAction;
import de.oliver.fancynpcs.api.data.property.NpcVisibility;
import de.oliver.fancynpcs.api.events.NpcsLoadedEvent;
import de.oliver.fancynpcs.api.skins.SkinData;
import de.oliver.fancynpcs.api.skins.SkinLoadException;
import de.oliver.fancynpcs.api.utils.NpcEquipmentSlot;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class NpcManagerImpl implements NpcManager {

    private final FancyNpcs plugin;
    private final ExtendedFancyLogger logger;
    private final Function<NpcData, Npc> npcAdapter;
    private final File npcConfigFile;
    private final Map<String, Npc> npcs; // npc id -> npc
    private boolean isLoaded;

    public NpcManagerImpl(FancyNpcs plugin, Function<NpcData, Npc> npcAdapter) {
        this.plugin = plugin;
        this.logger = plugin.getFancyLogger();
        this.npcAdapter = npcAdapter;
        npcs = new ConcurrentHashMap<>();
        npcConfigFile = new File("plugins" + File.separator + "FancyNpcs" + File.separator + "npcs.yml");
        isLoaded = false;
    }

    @Override
    public void registerNpc(Npc npc) {
        if (!FancyNpcs.PLAYER_NPCS_FEATURE_FLAG.isEnabled() && getAllNpcs().stream().anyMatch(npc1 -> npc1.getData().getName().equals(npc.getData().getName()))) {
            throw new IllegalStateException("An NPC with this name already exists");
        } else {
            npcs.put(npc.getData().getId(), npc);
        }
    }

    @Override
    public void removeNpc(Npc npc) {
        npcs.remove(npc.getData().getId());

        YamlConfiguration npcConfig = YamlConfiguration.loadConfiguration(npcConfigFile);
        npcConfig.set("npcs." + npc.getData().getId(), null);
        try {
            npcConfig.save(npcConfigFile);
        } catch (IOException e) {
            logger.error("Could not save npc config file", ThrowableProperty.of(e));
        }
    }

    @ApiStatus.Internal
    @Override
    public Npc getNpc(int entityId) {
        for (Npc npc : getAllNpcs()) {
            if (npc.getEntityId() == entityId) {
                return npc;
            }
        }

        return null;
    }

    @Override
    public Npc getNpc(String name) {
        for (Npc npc : getAllNpcs()) {
            if (npc.getData().getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }

        return null;
    }

    @Override
    public Npc getNpcById(String id) {
        return npcs.get(id);
    }

    @Override
    public Npc getNpc(String name, UUID creator) {
        for (Npc npc : getAllNpcs()) {
            if (npc.getData().getCreator().equals(creator) && npc.getData().getName().equalsIgnoreCase(name)) {
                return npc;
            }
        }

        return null;
    }

    @Override
    public Collection<Npc> getAllNpcs() {
        return new ArrayList<>(npcs.values());
    }

    @Override
    public void saveNpcs(boolean force) {
        if (!isLoaded) {
            return;
        }

        if (!npcConfigFile.exists()) {
            try {
                npcConfigFile.createNewFile();
            } catch (IOException e) {
                logger.error("Could not create npc config file", ThrowableProperty.of(e));
                return;
            }
        }

        YamlConfiguration npcConfigRoot = YamlConfiguration.loadConfiguration(npcConfigFile);

        for (Npc npc : getAllNpcs()) {
            if (!npc.isSaveToFile()) {
                continue;
            }

            boolean shouldSave = force || npc.isDirty();
            if (!shouldSave) {
                continue;
            }

            NpcData data = npc.getData();
            ConfigurationSection npcConfig = npcConfigRoot.getConfigurationSection("npcs." + data.getId());
            if (npcConfig == null) {
                npcConfig = npcConfigRoot.createSection("npcs." + data.getId());
            }

            npcConfig.set("name", data.getName());
            npcConfig.set("creator", data.getCreator().toString());
            npcConfig.set("displayName", data.getDisplayName());
            npcConfig.set("type", data.getType().name());
            npcConfig.set("location.world", data.getLocation().getWorld().getName());
            npcConfig.set("location.x", data.getLocation().getX());
            npcConfig.set("location.y", data.getLocation().getY());
            npcConfig.set("location.z", data.getLocation().getZ());
            npcConfig.set("location.yaw", data.getLocation().getYaw());
            npcConfig.set("location.pitch", data.getLocation().getPitch());
            npcConfig.set("showInTab", data.isShowInTab());
            npcConfig.set("spawnEntity", data.isSpawnEntity());
            npcConfig.set("collidable", data.isCollidable());
            npcConfig.set("glowing", data.isGlowing());
            npcConfig.set("glowingColor", data.getGlowingColor().toString());
            npcConfig.set("turnToPlayer", data.isTurnToPlayer());
            npcConfig.set("turnToPlayerDistance", data.getTurnToPlayerDistance());
            npcConfig.set("messages", null);
            npcConfig.set("playerCommands", null);
            npcConfig.set("serverCommands", null);
            npcConfig.set("sendMessagesRandomly", null);
            npcConfig.set("interactionCooldown", data.getInteractionCooldown());
            npcConfig.set("scale", data.getScale());
            npcConfig.set("visibility_distance", data.getVisibilityDistance());
            npcConfig.set("visibility", data.getVisibility().name());

            if (data.getSkinData() != null) {
                npcConfig.set("skin.identifier", data.getSkinData().getIdentifier());
                npcConfig.set("skin.variant", data.getSkinData().getVariant().name());
            } else {
                npcConfig.set("skin.identifier", null);
            }
            npcConfig.set("skin.mirrorSkin", data.isMirrorSkin());

            if (data.getEquipment() != null) {
                for (Map.Entry<NpcEquipmentSlot, ItemStack> entry : data.getEquipment().entrySet()) {
                    npcConfig.set("equipment." + entry.getKey().name(), entry.getValue());
                }
            }

            for (NpcAttribute attribute : plugin.getAttributeManager().getAllAttributesForEntityType(data.getType())) {
                String value = data.getAttributes().getOrDefault(attribute, null);
                npcConfig.set("attributes." + attribute.getName(), value);
            }

            npcConfig.set("actions", null);
            for (Map.Entry<ActionTrigger, List<NpcAction.NpcActionData>> entry : npc.getData().getActions().entrySet()) {
                for (NpcAction.NpcActionData actionData : entry.getValue()) {
                    if (actionData == null) {
                        continue;
                    }

                    if (actionData.action() instanceof UnknownActionAction unknownActionAction) {
                        npcConfig.set("actions." + entry.getKey().name() + "." + actionData.order() + ".action", unknownActionAction.getUnknownActionName());
                        npcConfig.set("actions." + entry.getKey().name() + "." + actionData.order() + ".value", unknownActionAction.getUnknownActionValue());
                    } else {
                        npcConfig.set("actions." + entry.getKey().name() + "." + actionData.order() + ".action", actionData.action().getName());
                        npcConfig.set("actions." + entry.getKey().name() + "." + actionData.order() + ".value", actionData.value());
                    }
                }
            }

            npc.setDirty(false);
        }

        try {
            npcConfigRoot.save(npcConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadNpcs() {
        npcs.clear();
        YamlConfiguration npcConfigRoot = YamlConfiguration.loadConfiguration(npcConfigFile);

        if (!npcConfigRoot.isConfigurationSection("npcs")) {
            this.setLoaded();
            return;
        }

        for (String id : npcConfigRoot.getConfigurationSection("npcs").getKeys(false)) {
            ConfigurationSection npcConfig = npcConfigRoot.getConfigurationSection("npcs." + id);
            if (npcConfig == null) {
                continue;
            }
            String name = npcConfig.getString("name");
            if (name == null) name = id;

            String creatorStr = npcConfig.getString("creator");
            UUID creator = creatorStr == null ? null : UUID.fromString(creatorStr);

            String displayName = npcConfig.getString("displayName", "<empty>");
            EntityType type = EntityType.valueOf(npcConfig.getString("type", "PLAYER").toUpperCase());

            Location location = null;

            try {
                location = npcConfig.getLocation("location");
            } catch (Exception ignored) {
                logger.warn("Could not load location for npc '" + id + "'");
            }

            if (location == null) {
                String worldName = npcConfig.getString("location.world");
                World world = Bukkit.getWorld(worldName);

                if (world == null) {
                    File worldFolder = new File(worldName);
                    if (worldFolder.exists() && worldFolder.isDirectory()) {
                        world = (!ServerSoftware.isFolia()) ? new WorldCreator(worldName).createWorld() : null;
                    }
                }

                if (world == null) {
                    logger.info("Could not load npc '" + id + "', because the world '" + worldName + "' is not loaded");
                    continue;
                }

                double x = npcConfig.getDouble("location.x");
                double y = npcConfig.getDouble("location.y");
                double z = npcConfig.getDouble("location.z");
                float yaw = (float) npcConfig.getDouble("location.yaw");
                float pitch = (float) npcConfig.getDouble("location.pitch");

                location = new Location(world, x, y, z, yaw, pitch);
            }

            SkinData skin = null;
            String skinIdentifier = npcConfig.getString("skin.identifier", npcConfig.getString("skin.uuid", ""));
            String skinVariantStr = npcConfig.getString("skin.variant", SkinData.SkinVariant.AUTO.name());
            SkinData.SkinVariant skinVariant = SkinData.SkinVariant.valueOf(skinVariantStr);
            if (!skinIdentifier.isEmpty()) {
                try {
                    skin = plugin.getSkinManagerImpl().getByIdentifier(skinIdentifier, skinVariant);
                    skin.setIdentifier(skinIdentifier);
                } catch (final SkinLoadException e) {
                    logger.error("NPC named '" + name + "' identified by '" + id + "' could not have their skin loaded.");
                    logger.error("  " + e.getReason() + " " + e.getMessage());
                }
            }


            if (npcConfig.isSet("skin.value") && npcConfig.isSet("skin.signature")) {
                // using old skin system --> take backup
                takeBackup(npcConfigRoot);

                String value = npcConfig.getString("skin.value");
                String signature = npcConfig.getString("skin.signature");

                if (value != null && !value.isEmpty() && signature != null && !signature.isEmpty()) {
                    SkinData oldSkin = new SkinData(skinIdentifier, SkinData.SkinVariant.AUTO, value, signature);
                    plugin.getSkinManagerImpl().getFileCache().addSkin(oldSkin);
                    plugin.getSkinManagerImpl().getMemCache().addSkin(oldSkin);
                }
            }

            boolean mirrorSkin = npcConfig.getBoolean("skin.mirrorSkin");

            boolean showInTab = npcConfig.getBoolean("showInTab");
            boolean spawnEntity = npcConfig.getBoolean("spawnEntity");
            boolean collidable = npcConfig.getBoolean("collidable", true);
            boolean glowing = npcConfig.getBoolean("glowing");
            NamedTextColor glowingColor = NamedTextColor.NAMES.value(npcConfig.getString("glowingColor", "white"));
            boolean turnToPlayer = npcConfig.getBoolean("turnToPlayer");
            int turnToPlayerDistance = npcConfig.getInt("turnToPlayerDistance", -1);

            Map<ActionTrigger, List<NpcAction.NpcActionData>> actions = new ConcurrentHashMap<>();

            ConfigurationSection actiontriggerSection = npcConfig.getConfigurationSection("actions");
            if (actiontriggerSection != null) {
                actiontriggerSection.getKeys(false).forEach(trigger -> {
                    ActionTrigger actionTrigger = ActionTrigger.getByName(trigger);
                    if (actionTrigger == null) {
                        logger.warn("Could not find action trigger: " + trigger);
                        return;
                    }

                    List<NpcAction.NpcActionData> actionList = new ArrayList<>();
                    ConfigurationSection actionsSection = npcConfig.getConfigurationSection("actions." + trigger);
                    if (actionsSection != null) {
                        actionsSection.getKeys(false).forEach(order -> {
                            String actionName = npcConfig.getString("actions." + trigger + "." + order + ".action");
                            String value = npcConfig.getString("actions." + trigger + "." + order + ".value");
                            NpcAction action = plugin.getActionManager().getActionByName(actionName);
                            if (action == null) {
                                logger.warn("Could not find action: " + actionName);
                                action = new UnknownActionAction(actionTrigger, actionName, value, Integer.parseInt(order));
                            }

                            try {
                                actionList.add(new NpcAction.NpcActionData(Integer.parseInt(order), action, value));
                            } catch (NumberFormatException e) {
                                logger.warn("Could not parse order: " + order);
                            }
                        });

                        actions.put(actionTrigger, actionList);
                    }
                });
            }

            float interactionCooldown = (float) npcConfig.getDouble("interactionCooldown", 0);
            float scale = (float) npcConfig.getDouble("scale", 1);
            int visibilityDistance = npcConfig.getInt("visibility_distance", -1);
            String visibilityStr = npcConfig.getString("visibility", "ALL");
            NpcVisibility visibility = NpcVisibility.byString(visibilityStr).orElse(NpcVisibility.ALL);

            Map<NpcAttribute, String> attributes = new HashMap<>();
            if (npcConfig.isConfigurationSection("attributes")) {
                for (String attrName : npcConfig.getConfigurationSection("attributes").getKeys(false)) {
                    NpcAttribute attribute = plugin.getAttributeManager().getAttributeByName(type, attrName);
                    if (attribute == null) {
                        logger.warn("Could not find attribute: " + attrName);
                        continue;
                    }

                    String value = npcConfig.getString("attributes." + attrName);
                    if (!attribute.isValidValue(value)) {
                        logger.warn("Invalid value for attribute: " + attrName);
                        continue;
                    }

                    attributes.put(attribute, value);
                }
            }

            NpcData data = new NpcData(
                    id,
                    name,
                    creator,
                    displayName,
                    skin,
                    location,
                    showInTab,
                    spawnEntity,
                    collidable,
                    glowing,
                    glowingColor,
                    type,
                    new HashMap<>(),
                    turnToPlayer,
                    turnToPlayerDistance,
                    null,
                    actions,
                    interactionCooldown,
                    scale,
                    visibilityDistance,
                    attributes,
                    mirrorSkin
            );
            Npc npc = npcAdapter.apply(data);

            if (npcConfig.isConfigurationSection("equipment")) {
                for (String equipmentSlotStr : npcConfig.getConfigurationSection("equipment").getKeys(false)) {
                    NpcEquipmentSlot equipmentSlot = NpcEquipmentSlot.parse(equipmentSlotStr);
                    ItemStack item = npcConfig.getItemStack("equipment." + equipmentSlotStr);
                    npc.getData().addEquipment(equipmentSlot, item);
                }
            }

            npc.getData().setVisibility(visibility);
            npc.create();
            registerNpc(npc);
        }
        this.setLoaded();
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    private void setLoaded() {
        isLoaded = true;
        new NpcsLoadedEvent().callEvent();
    }

    @Override
    public void reloadNpcs() {
        Collection<Npc> npcCopy = new ArrayList<>(getAllNpcs());
        npcs.clear();
        for (Npc npc : npcCopy) {
            npc.removeForAll();
        }

        loadNpcs();
    }

    private void takeBackup(YamlConfiguration npcConfig) {
        String folderPath = "plugins" + File.separator + "FancyNpcs" + File.separator + "/backups";
        File backupDir = new File(folderPath);
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String backupFileName = "npcs-" + formatter.format(now) + ".yml";
        File backupFile = new File(folderPath + File.separator + backupFileName);
        if (backupFile.exists()) {
            backupFile.delete();
        }

        try {
            backupFile.createNewFile();
        } catch (IOException e) {
            logger.error("Could not create backup file for NPCs");
        }

        try {
            npcConfig.save(backupFile);
        } catch (IOException e) {
            logger.error("Could not save backup file for NPCs");
        }
    }
}
