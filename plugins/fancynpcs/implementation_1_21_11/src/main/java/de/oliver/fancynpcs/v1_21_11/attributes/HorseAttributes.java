package de.oliver.fancynpcs.v1_21_11.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_11.ReflectionHelper;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.animal.equine.Markings;
import net.minecraft.world.entity.animal.equine.Variant;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HorseAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "variant",
                Arrays.stream(Variant.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.HORSE),
                HorseAttributes::setVariant
        ));

        attributes.add(new NpcAttribute(
                "markings",
                Arrays.stream(Markings.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.HORSE),
                HorseAttributes::setMarkings
        ));

        attributes.add(new NpcAttribute(
                "pose",
                List.of("eating", "rearing", "standing"),
                Arrays.stream(EntityType.values())
                        .filter(type -> type.getEntityClass() != null && (type == EntityType.HORSE || type == EntityType.DONKEY ||
                                type == EntityType.MULE || type == EntityType.SKELETON_HORSE || type == EntityType.ZOMBIE_HORSE))
                        .toList(),
                HorseAttributes::setPose
        ));

        return attributes;
    }

    private static void setVariant(Npc npc, String value) {
        Horse horse = ReflectionHelper.getEntity(npc);

        Variant variant = Variant.valueOf(value.toUpperCase());
        horse.setVariantAndMarkings(variant, horse.getMarkings());
    }

    private static void setMarkings(Npc npc, String value) {
        Horse horse = ReflectionHelper.getEntity(npc);

        Markings markings = Markings.valueOf(value.toUpperCase());
        horse.setVariantAndMarkings(horse.getVariant(), markings);
    }

    private static void setPose(Npc npc, String value) {
        AbstractHorse horse = ReflectionHelper.getEntity(npc);

        switch (value.toLowerCase()) {
            case "standing" -> {
                horse.setEating(false);
                horse.setStanding(0);
            }
            case "rearing" -> {
                horse.setStanding(20);
                horse.setEating(false);
            }
            case "eating" -> {
                horse.setStanding(0);
                horse.setEating(true);
            }
        }
    }

}
