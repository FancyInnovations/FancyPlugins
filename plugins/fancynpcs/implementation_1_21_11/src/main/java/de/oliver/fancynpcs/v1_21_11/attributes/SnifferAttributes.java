package de.oliver.fancynpcs.v1_21_11.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_11.ReflectionHelper;
import net.minecraft.world.entity.animal.sniffer.Sniffer;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SnifferAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "state",
                Arrays.stream(Sniffer.State.values())
                        .map(Enum::name)
                        .toList(),
                List.of(EntityType.SNIFFER),
                SnifferAttributes::setState
        ));

        return attributes;
    }

    private static void setState(Npc npc, String value) {
        final Sniffer sniffer = ReflectionHelper.getEntity(npc);

        Sniffer.State state = Sniffer.State.valueOf(value.toUpperCase());
        sniffer.transitionTo(state);
    }
}
