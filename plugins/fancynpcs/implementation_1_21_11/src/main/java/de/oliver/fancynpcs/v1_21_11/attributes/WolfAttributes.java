package de.oliver.fancynpcs.v1_21_11.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_11.ReflectionHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.animal.wolf.WolfVariant;
import net.minecraft.world.item.DyeColor;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class WolfAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sitting"),
                List.of(EntityType.WOLF),
                WolfAttributes::setPose
        ));

        attributes.add(new NpcAttribute(
                "angry",
                List.of("true", "false"),
                List.of(EntityType.WOLF),
                WolfAttributes::setAngry
        ));

        attributes.add(new NpcAttribute(
                "variant",
                getWolfVariantRegistry()
                        .listElementIds()
                        .map(id -> id.identifier().getPath())
                        .toList(),
                List.of(EntityType.WOLF),
                WolfAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "collar_color",
                List.of("RED", "BLUE", "YELLOW", "GREEN", "PURPLE", "ORANGE", "LIME", "MAGENTA", "BROWN", "WHITE", "GRAY", "LIGHT_GRAY", "LIGHT_BLUE", "BLACK", "CYAN", "PINK", "NONE"),
                List.of(EntityType.WOLF),
                WolfAttributes::setCollarColor
        ));

        return attributes;
    }

    private static void setPose(Npc npc, String value) {
        Wolf wolf = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "standing" -> wolf.setInSittingPose(false, false);
            case "sitting" -> wolf.setInSittingPose(true, false);
        }
    }

    private static void setAngry(Npc npc, String value) {
        Wolf wolf = ReflectionHelper.getEntity(npc);

        boolean angry = Boolean.parseBoolean(value.toLowerCase());

        wolf.setTimeToRemainAngry(angry ? 100 : 0);
    }

    private static void setVariant(Npc npc, String value) {
        Wolf wolf = ReflectionHelper.getEntity(npc);

        Registry<WolfVariant> registry = wolf.level().registryAccess().lookupOrThrow(Registries.WOLF_VARIANT);

        Identifier variantLocation = Identifier.tryParse("minecraft:" + value.toLowerCase());
        if (variantLocation == null) {
            System.out.println("Invalid variant name: " + value);
            return;
        }

        WolfVariant variant = registry.getOptional(variantLocation).orElse(null);
        if (variant == null) {
            System.out.println("Wolf variant not found: " + variantLocation);
            return;
        }

        // Get the ResourceKey from the registry
        registry.getResourceKey(variant).ifPresentOrElse(
                key -> {
                    // Get the holder from the registry — this is properly bound
                    Holder<WolfVariant> holder = registry.wrapAsHolder(variant);
                    wolf.setVariant(holder);
                },
                () -> System.out.println("Wolf variant not registered: " + variantLocation)
        );
    }

    private static void setCollarColor(Npc npc, String value) {
        Wolf wolf = ReflectionHelper.getEntity(npc);

        if (value.equalsIgnoreCase("none") || value.isEmpty()) {
            // Reset to no collar
            wolf.setTame(false, false);
            return;
        }

        try {
            DyeColor color = DyeColor.valueOf(value.toUpperCase());
            if (!wolf.isTame()) {
                wolf.setTame(true, false);
            }
            wolf.setCollarColor(color);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid wolf collar color: " + value);
        }
    }

    private static HolderLookup.RegistryLookup<WolfVariant> getWolfVariantRegistry() {
        return MinecraftServer.getServer().registryAccess().lookupOrThrow(Registries.WOLF_VARIANT);
    }
}
