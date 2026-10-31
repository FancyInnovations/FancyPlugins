package de.oliver.fancynpcs.v26_4.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v26_4.ReflectionHelper;
import net.minecraft.world.entity.animal.cow.MushroomCow;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class MushroomCowAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "type",
                Arrays.stream(MushroomCow.Variant.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.MOOSHROOM),
                MushroomCowAttributes::setType
        ));

        return attributes;
    }

    private static void setType(Npc npc, String value) {
        MushroomCow mushroomCow = ReflectionHelper.getEntity(npc);

        MushroomCow.Variant type = MushroomCow.Variant.valueOf(value.toUpperCase(Locale.ROOT));
        mushroomCow.setVariant(type);
    }

}
