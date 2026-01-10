package de.oliver.fancysitula.versions.v1_21_11.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.FS_GameProfile;
import de.oliver.fancysitula.versions.v1_21_11.utils.GameProfileImpl;
import de.oliver.fancysitula.api.utils.reflections.ReflectionUtils;
import de.oliver.fancysitula.versions.v1_21_11.utils.VanillaPlayerAdapter;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Interaction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.fox.Fox;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogVariant;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.animal.panda.Panda;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.rabbit.Rabbit;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import net.minecraft.world.entity.animal.turtle.Turtle;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.entity.animal.fish.TropicalFish;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Llama;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.decoration.Mannequin;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.illager.SpellcasterIllager;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.core.ClientAsset;
import com.mojang.datafixers.util.Either;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClientboundSetEntityDataPacketImpl extends FS_ClientboundSetEntityDataPacket {

    // Pre-populated class registry to bypass Paper's reflection remapper
    private static final ConcurrentMap<String, Class<?>> ENTITY_CLASS_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentMap<String, net.minecraft.network.syncher.EntityDataAccessor<Object>> ENTITY_ACCESSOR_CACHE = new ConcurrentHashMap<>();

    static {
        // Pre-register all known entity classes to avoid reflection remapper issues
        // In 1.21.11, many animal classes were moved to subpackages
        // Keys are the OLD class names (used by FancySitula API), values are the NEW class references

        // Core entity classes (unchanged)
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.Entity", Entity.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.LivingEntity", LivingEntity.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.Mob", Mob.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.AgeableMob", AgeableMob.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.Interaction", Interaction.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.player.Player", Player.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.decoration.ArmorStand", ArmorStand.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.Display", Display.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.Display$TextDisplay", Display.TextDisplay.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.Display$BlockDisplay", Display.BlockDisplay.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.Display$ItemDisplay", Display.ItemDisplay.class);

        // Animals - OLD paths (for backwards compatibility with existing code)
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Cow", Cow.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Chicken", Chicken.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Pig", Pig.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.MushroomCow", MushroomCow.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Cat", Cat.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Bee", Bee.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Fox", Fox.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Panda", Panda.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Parrot", Parrot.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.PolarBear", PolarBear.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Rabbit", Rabbit.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Sheep", Sheep.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.Turtle", Turtle.class);

        // Animals - NEW paths (1.21.11 actual class locations)
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.cow.Cow", Cow.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.cow.MushroomCow", MushroomCow.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.chicken.Chicken", Chicken.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.pig.Pig", Pig.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.feline.Cat", Cat.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.bee.Bee", Bee.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.fox.Fox", Fox.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.panda.Panda", Panda.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.parrot.Parrot", Parrot.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.rabbit.Rabbit", Rabbit.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.sheep.Sheep", Sheep.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.fish.TropicalFish", TropicalFish.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.equine.Llama", Llama.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.equine.Horse", Horse.class);

        // Animals - already in subpackages (unchanged)
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.frog.Frog", Frog.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.wolf.Wolf", Wolf.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.armadillo.Armadillo", Armadillo.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.sniffer.Sniffer", Sniffer.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.axolotl.Axolotl", Axolotl.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.camel.Camel", Camel.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.animal.goat.Goat", Goat.class);

        // Monsters
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.monster.SpellcasterIllager", SpellcasterIllager.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.monster.Creeper", Creeper.class);
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.raid.Raider", Raider.class);

        // NPCs
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.npc.Villager", Villager.class);

        // Avatar (new in 1.21.11 - Player extends Avatar)
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.Avatar", net.minecraft.world.entity.Avatar.class);

        // Mannequin (new in 1.21.11 - player-like entity without tab list)
        ENTITY_CLASS_CACHE.put("net.minecraft.world.entity.decoration.Mannequin", net.minecraft.world.entity.decoration.Mannequin.class);
    }

    public ClientboundSetEntityDataPacketImpl(int entityId, List<EntityData> entityData) {
        super(entityId, entityData);
    }

    /**
     * Translates field names that were renamed between versions.
     */
    private static String translateFieldName(String entityClassName, String fieldName) {
        // DATA_REMAINING_ANGER_TIME was renamed to DATA_ANGER_END_TIME in 1.21.11
        if (fieldName.equals("DATA_REMAINING_ANGER_TIME")) {
            return "DATA_ANGER_END_TIME";
        }
        return fieldName;
    }

    /**
     * Translates class names for fields that moved between versions.
     * In 1.21.11, Player extends Avatar and some fields moved to Avatar.
     */
    private static String translateClassName(String entityClassName, String fieldName) {
        // DATA_PLAYER_MODE_CUSTOMISATION and DATA_PLAYER_MAIN_HAND moved from Player to Avatar in 1.21.11
        if (entityClassName.equals("net.minecraft.world.entity.player.Player")
                && (fieldName.equals("DATA_PLAYER_MODE_CUSTOMISATION") || fieldName.equals("DATA_PLAYER_MAIN_HAND"))) {
            return "net.minecraft.world.entity.Avatar";
        }
        return entityClassName;
    }

    private static Class<?> getEntityClassCached(String className) throws ClassNotFoundException {
        Class<?> cached = ENTITY_CLASS_CACHE.get(className);
        if (cached != null) {
            return cached;
        }
        // Fallback to class loader for classes not in our registry
        ClassLoader classLoader = MinecraftServer.class.getClassLoader();
        Class<?> resolved = classLoader.loadClass(className);
        ENTITY_CLASS_CACHE.put(className, resolved);
        return resolved;
    }

    private static net.minecraft.network.syncher.EntityDataAccessor<Object> getAccessorCached(String entityClassName, String fieldName) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        String key = entityClassName + "#" + fieldName;
        net.minecraft.network.syncher.EntityDataAccessor<Object> cached = ENTITY_ACCESSOR_CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        Class<?> entityClass = getEntityClassCached(entityClassName);
        net.minecraft.network.syncher.EntityDataAccessor<Object> accessor = ReflectionUtils.getStaticField(entityClass, fieldName);
        ENTITY_ACCESSOR_CACHE.put(key, accessor);
        return accessor;
    }

    @Override
    public Object createPacket() {
        List<SynchedEntityData.DataValue<?>> dataValues = new ArrayList<>();
        for (EntityData data : entityData) {
            try {
                String originalFieldName = data.getAccessor().accessorFieldName();
                String accessorFieldName = translateFieldName(data.getAccessor().entityClassName(), originalFieldName);
                String entityClassName = translateClassName(data.getAccessor().entityClassName(), originalFieldName);
                net.minecraft.network.syncher.EntityDataAccessor<Object> accessor = getAccessorCached(entityClassName, accessorFieldName);

                Object vanillaValue = data.getValue();

                if (data.getValue() == null) {
                    continue;
                }

                if (data.getValue() instanceof Component c) {
                    vanillaValue = PaperAdventure.asVanilla(c);
                }

                // Handle Optional<Component> for custom names
                if (data.getValue() instanceof Optional<?> opt) {
                    if (opt.isPresent() && opt.get() instanceof Component c) {
                        vanillaValue = Optional.of(PaperAdventure.asVanilla(c));
                    } else {
                        vanillaValue = Optional.empty();
                    }
                }

                if (data.getValue() instanceof ItemStack i) {
                    vanillaValue = net.minecraft.world.item.ItemStack.fromBukkitCopy(i);
                }

                if (data.getValue() instanceof BlockState b) {
                    vanillaValue = ((CraftBlockState) b).getHandle();
                }

                // Handle Armadillo state enum conversion (value passed as String)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_STATE")
                        && entityClassName.contains("Armadillo")) {
                    try {
                        vanillaValue = Armadillo.ArmadilloState.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        vanillaValue = Armadillo.ArmadilloState.IDLE;
                    }
                }

                // Handle Wolf variant (data-driven registry)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Wolf")) {
                    String variantKey = s.startsWith("wolf:") ? s.substring("wolf:".length()) : s;
                    Identifier loc = Identifier.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    Registry<WolfVariant> registry = server.registryAccess().lookupOrThrow(Registries.WOLF_VARIANT);
                    Holder.Reference<WolfVariant> holder = registry.get(loc).orElse(null);
                    if (holder != null) {
                        vanillaValue = holder;
                    }
                }

                // Handle Cat variant (data-driven registry)
                // Note: In 1.21.11, CatVariant class location changed - using raw types for runtime resolution
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Cat")) {
                    String variantKey = s.startsWith("cat:") ? s.substring("cat:".length()) : s;
                    Identifier loc = Identifier.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Registry registry = server.registryAccess().lookupOrThrow(Registries.CAT_VARIANT);
                    @SuppressWarnings("rawtypes")
                    java.util.Optional optHolder = registry.get(loc);
                    if (optHolder.isPresent()) {
                        vanillaValue = optHolder.get();
                    }
                }

                // Handle Frog variant (data-driven registry)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Frog")) {
                    String variantKey = s.startsWith("frog:") ? s.substring("frog:".length()) : s;
                    Identifier loc = Identifier.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    Registry<FrogVariant> registry = server.registryAccess().lookupOrThrow(Registries.FROG_VARIANT);
                    Holder.Reference<FrogVariant> holder = registry.get(loc).orElse(null);
                    if (holder != null) {
                        vanillaValue = holder;
                    }
                }

                // Handle Cow variant (data-driven registry - 1.21.5+)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Cow") && !entityClassName.contains("Mushroom")) {
                    String variantKey = s.startsWith("minecraft:") ? s : "minecraft:" + s.toLowerCase();
                    Identifier loc = Identifier.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Registry registry = server.registryAccess().lookupOrThrow(Registries.COW_VARIANT);
                    @SuppressWarnings("rawtypes")
                    java.util.Optional optHolder = registry.get(loc);
                    if (optHolder.isPresent()) {
                        vanillaValue = optHolder.get();
                    }
                }

                // Handle Chicken variant (data-driven registry - 1.21.5+)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Chicken")) {
                    String variantKey = s.startsWith("minecraft:") ? s : "minecraft:" + s.toLowerCase();
                    Identifier loc = Identifier.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Registry registry = server.registryAccess().lookupOrThrow(Registries.CHICKEN_VARIANT);
                    @SuppressWarnings("rawtypes")
                    java.util.Optional optHolder = registry.get(loc);
                    if (optHolder.isPresent()) {
                        vanillaValue = optHolder.get();
                    }
                }

                // Handle Pig variant (data-driven registry - 1.21.5+)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VARIANT_ID")
                        && entityClassName.contains("Pig") && !entityClassName.contains("Piglin")) {
                    String variantKey = s.startsWith("minecraft:") ? s : "minecraft:" + s.toLowerCase();
                    Identifier loc = Identifier.parse(variantKey);
                    MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
                    @SuppressWarnings({"unchecked", "rawtypes"})
                    Registry registry = server.registryAccess().lookupOrThrow(Registries.PIG_VARIANT);
                    @SuppressWarnings("rawtypes")
                    java.util.Optional optHolder = registry.get(loc);
                    if (optHolder.isPresent()) {
                        vanillaValue = optHolder.get();
                    }
                }

                // Handle Sniffer state enum
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_STATE")
                        && entityClassName.contains("Sniffer")) {
                    try {
                        vanillaValue = Sniffer.State.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        vanillaValue = Sniffer.State.IDLING;
                    }
                }

                // Handle Mooshroom type enum (RED/BROWN)
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_TYPE")
                        && entityClassName.contains("MushroomCow")) {
                    try {
                        vanillaValue = MushroomCow.Variant.valueOf(s.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        vanillaValue = MushroomCow.Variant.RED;
                    }
                }

                // Handle VillagerData - supports format: "type:X,profession:Y,level:Z" (all optional)
                // Also supports legacy format: "profession:X" or "type:X" alone
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_VILLAGER_DATA")
                        && entityClassName.contains("Villager")) {
                    String typeKey = "minecraft:plains";
                    String profKey = "minecraft:none";
                    int level = 1;

                    // Parse comma-separated values
                    for (String part : s.split(",")) {
                        part = part.trim();
                        if (part.startsWith("type:")) {
                            typeKey = part.substring("type:".length());
                        } else if (part.startsWith("profession:")) {
                            profKey = part.substring("profession:".length());
                        } else if (part.startsWith("level:")) {
                            try {
                                level = Integer.parseInt(part.substring("level:".length()));
                            } catch (NumberFormatException ignored) {}
                        }
                    }

                    Identifier typeLoc = Identifier.parse(typeKey);
                    Identifier profLoc = Identifier.parse(profKey);
                    Holder.Reference<VillagerType> typeHolder = BuiltInRegistries.VILLAGER_TYPE.get(typeLoc).orElse(null);
                    Holder.Reference<VillagerProfession> profHolder = BuiltInRegistries.VILLAGER_PROFESSION.get(profLoc).orElse(null);

                    if (typeHolder != null && profHolder != null) {
                        vanillaValue = new VillagerData(typeHolder, profHolder, level);
                    }
                }

                // Handle FS_GameProfile -> ResolvableProfile for Mannequin
                if (data.getValue() instanceof FS_GameProfile profile && accessorFieldName.equals("DATA_PROFILE")) {
                    com.mojang.authlib.GameProfile authProfile = GameProfileImpl.asVanilla(profile);

                    // Check if profile has resource pack texture assets
                    if (profile.hasTextureAssets()) {
                        // Build PlayerSkin.Patch with ResourceTexture for resource pack skins
                        Optional<ClientAsset.ResourceTexture> bodyTexture = Optional.empty();
                        Optional<ClientAsset.ResourceTexture> capeTexture = Optional.empty();
                        Optional<ClientAsset.ResourceTexture> elytraTexture = Optional.empty();
                        Optional<PlayerModelType> modelType = Optional.empty();

                        if (profile.getSkinTextureAsset() != null) {
                            bodyTexture = Optional.of(new ClientAsset.ResourceTexture(
                                    Identifier.parse(profile.getSkinTextureAsset())
                            ));
                        }
                        if (profile.getCapeTextureAsset() != null) {
                            capeTexture = Optional.of(new ClientAsset.ResourceTexture(
                                    Identifier.parse(profile.getCapeTextureAsset())
                            ));
                        }
                        if (profile.getElytraTextureAsset() != null) {
                            elytraTexture = Optional.of(new ClientAsset.ResourceTexture(
                                    Identifier.parse(profile.getElytraTextureAsset())
                            ));
                        }
                        if (profile.getModelType() != null) {
                            modelType = Optional.of(
                                    "SLIM".equalsIgnoreCase(profile.getModelType())
                                            ? PlayerModelType.SLIM
                                            : PlayerModelType.WIDE
                            );
                        }

                        PlayerSkin.Patch skinPatch = PlayerSkin.Patch.create(
                                bodyTexture, capeTexture, elytraTexture, modelType
                        );

                        // Create ResolvableProfile.Static with the skin patch
                        vanillaValue = new ResolvableProfile.Static(
                                Either.left(authProfile), skinPatch
                        );
                    } else {
                        // No texture assets, use standard profile
                        vanillaValue = ResolvableProfile.createResolved(authProfile);
                    }
                }

                // Handle HumanoidArm for Avatar/Mannequin main hand
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_PLAYER_MAIN_HAND")) {
                    vanillaValue = s.equalsIgnoreCase("LEFT") ? HumanoidArm.LEFT : HumanoidArm.RIGHT;
                }

                // Handle Pose enum for Mannequin and other entities
                if (data.getValue() instanceof String s && accessorFieldName.equals("DATA_POSE")) {
                    vanillaValue = switch (s.toUpperCase()) {
                        case "STANDING" -> net.minecraft.world.entity.Pose.STANDING;
                        case "FALL_FLYING" -> net.minecraft.world.entity.Pose.FALL_FLYING;
                        case "SLEEPING" -> net.minecraft.world.entity.Pose.SLEEPING;
                        case "SWIMMING" -> net.minecraft.world.entity.Pose.SWIMMING;
                        case "SPIN_ATTACK" -> net.minecraft.world.entity.Pose.SPIN_ATTACK;
                        case "CROUCHING", "SNEAKING" -> net.minecraft.world.entity.Pose.CROUCHING;
                        case "LONG_JUMPING" -> net.minecraft.world.entity.Pose.LONG_JUMPING;
                        case "DYING" -> net.minecraft.world.entity.Pose.DYING;
                        case "CROAKING" -> net.minecraft.world.entity.Pose.CROAKING;
                        case "USING_TONGUE" -> net.minecraft.world.entity.Pose.USING_TONGUE;
                        case "SITTING" -> net.minecraft.world.entity.Pose.SITTING;
                        case "ROARING" -> net.minecraft.world.entity.Pose.ROARING;
                        case "SNIFFING" -> net.minecraft.world.entity.Pose.SNIFFING;
                        case "EMERGING" -> net.minecraft.world.entity.Pose.EMERGING;
                        case "DIGGING" -> net.minecraft.world.entity.Pose.DIGGING;
                        case "SLIDING" -> net.minecraft.world.entity.Pose.SLIDING;
                        case "SHOOTING" -> net.minecraft.world.entity.Pose.SHOOTING;
                        case "INHALING" -> net.minecraft.world.entity.Pose.INHALING;
                        default -> net.minecraft.world.entity.Pose.STANDING;
                    };
                }

                dataValues.add(SynchedEntityData.DataValue.create(accessor, vanillaValue));
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return new ClientboundSetEntityDataPacket(entityId, dataValues);
    }

    @Override
    public void sendPacketTo(FS_RealPlayer player) {
        ClientboundSetEntityDataPacket packet = (ClientboundSetEntityDataPacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
