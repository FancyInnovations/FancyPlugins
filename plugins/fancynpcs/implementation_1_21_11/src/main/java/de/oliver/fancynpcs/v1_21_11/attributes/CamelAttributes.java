package de.oliver.fancynpcs.v1_21_11.attributes;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_11.ReflectionHelper;
import net.minecraft.world.entity.Pose;
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

        return attributes;
    }

    private static void setPose(Npc npc, String value) {
        Camel camel = ReflectionHelper.getEntity(npc);

        Bukkit.getScheduler().runTask(FancyNpcsPlugin.get().getPlugin(), () -> {
            switch (value.toLowerCase()) {
                case "standing" -> {
                    camel.setPose(Pose.STANDING);
                    camel.setDashing(false);
                    camel.resetLastPoseChangeTick(camel.level().getGameTime());
                }
                case "sitting" -> {
                    camel.setPose(Pose.SITTING);
                    camel.setDashing(false);
                    camel.resetLastPoseChangeTick(-camel.level().getGameTime());
                }
                case "dashing" -> {
                    camel.setPose(Pose.STANDING);
                    camel.setDashing(true);
                    camel.resetLastPoseChangeTick(camel.level().getGameTime());
                }
            }
        });
    }

}
