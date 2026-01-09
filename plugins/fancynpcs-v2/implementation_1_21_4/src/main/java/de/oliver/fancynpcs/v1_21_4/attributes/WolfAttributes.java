package de.oliver.fancynpcs.v1_21_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_4.ReflectionHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WolfVariant;
import org.bukkit.entity.EntityType;
import net.minecraft.world.item.DyeColor;

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
                List.of("PALE", "SPOTTED", "SNOWY", "BLACK", "ASHEN", "RUSTY", "WOODS", "CHESTNUT", "STRIPED"),
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
        wolf.setRemainingPersistentAngerTime(angry ? 100 : 0);
    }

    private static void setVariant(Npc npc, String value) {
        Wolf wolf = ReflectionHelper.getEntity(npc);

        Registry<WolfVariant> registry = wolf.level().registryAccess().lookupOrThrow(Registries.WOLF_VARIANT);

        ResourceLocation variantLocation = ResourceLocation.tryParse("minecraft:" + value.toLowerCase());
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

    public static void setCollarColor(Npc npc, String value) {
        Wolf wolf = ReflectionHelper.getEntity(npc); // NMS Wolf

        if (value.equalsIgnoreCase("none") || value.isEmpty()) {
            // Untame to remove collar
            wolf.setTame(false, false);
            return;
        }

        try {
            DyeColor color = DyeColor.valueOf(value.toUpperCase()); // NMS enum
            wolf.setTame(true, false); // Wolves need to be tamed for collar color to show
            wolf.setCollarColor(color);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid color for wolf collar: " + value);
        }
    }
}
