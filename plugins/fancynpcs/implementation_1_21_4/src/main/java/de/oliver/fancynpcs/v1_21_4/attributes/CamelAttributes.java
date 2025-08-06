package de.oliver.fancynpcs.v1_21_4.attributes;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_4.ReflectionHelper;
import net.minecraft.world.entity.animal.camel.Camel;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class CamelAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "pose",
                List.of("standing", "sitting", "dashing"),
                List.of(EntityType.CAMEL),
                CamelAttributes::setPose
        ));

        attributes.add(new NpcAttribute(
                "has_saddle",
                List.of("true", "false"),
                List.of(EntityType.CAMEL),
                CamelAttributes::setHasSaddle
        ));

        return attributes;
    }

    private static void setPose(Npc npc, String value) {
        Camel camel = ReflectionHelper.getEntity(npc);

        Bukkit.getScheduler().runTask(FancyNpcsPlugin.get().getPlugin(), () -> {
            switch (value.toLowerCase()) {
                case "standing" -> {
                    camel.setDashing(false);
                    camel.standUp();
                }
                case "sitting" -> {
                    camel.setDashing(false);
                    camel.sitDown();
                }
                case "dashing" -> {
                    camel.standUpInstantly();
                    camel.setDashing(true);
                }
            }
        });
    }

    private static void setHasSaddle(Npc npc, String value) {
        Camel camel = ReflectionHelper.getEntity(npc);

        boolean hasSaddle = Boolean.parseBoolean(value.toLowerCase());

        camel.steering.setSaddle(hasSaddle);
    }

}
