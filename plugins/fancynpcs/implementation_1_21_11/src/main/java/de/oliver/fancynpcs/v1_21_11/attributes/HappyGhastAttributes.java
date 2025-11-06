package de.oliver.fancynpcs.v1_21_11.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v1_21_11.ReflectionHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class HappyGhastAttributes {

    public static List<NpcAttribute> getAllAttributes() {
        List<NpcAttribute> attributes = new ArrayList<>();

        attributes.add(new NpcAttribute(
                "harness",
                List.of("white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"),
                List.of(EntityType.HAPPY_GHAST),
                HappyGhastAttributes::setHarness
        ));

        return attributes;
    }

    private static void setHarness(Npc npc, String value) {
        HappyGhast ghast = ReflectionHelper.getEntity(npc);

        ItemStack harnessItem = switch (value.toLowerCase()) {
            case "white" -> Items.WHITE_HARNESS.getDefaultInstance();
            case "orange" -> Items.ORANGE_HARNESS.getDefaultInstance();
            case "magenta" -> Items.MAGENTA_HARNESS.getDefaultInstance();
            case "light_blue" -> Items.LIGHT_BLUE_HARNESS.getDefaultInstance();
            case "yellow" -> Items.YELLOW_HARNESS.getDefaultInstance();
            case "lime" -> Items.LIME_HARNESS.getDefaultInstance();
            case "pink" -> Items.PINK_HARNESS.getDefaultInstance();
            case "gray" -> Items.GRAY_HARNESS.getDefaultInstance();
            case "light_gray" -> Items.LIGHT_GRAY_HARNESS.getDefaultInstance();
            case "cyan" -> Items.CYAN_HARNESS.getDefaultInstance();
            case "purple" -> Items.PURPLE_HARNESS.getDefaultInstance();
            case "blue" -> Items.BLUE_HARNESS.getDefaultInstance();
            case "brown" -> Items.BROWN_HARNESS.getDefaultInstance();
            case "green" -> Items.GREEN_HARNESS.getDefaultInstance();
            case "red" -> Items.RED_HARNESS.getDefaultInstance();
            case "black" -> Items.BLACK_HARNESS.getDefaultInstance();
            default -> Items.AIR.getDefaultInstance();
        };

        if (!harnessItem.isEmpty()) {
            ghast.setItemSlot(EquipmentSlot.BODY, harnessItem);
        }
    }
}
