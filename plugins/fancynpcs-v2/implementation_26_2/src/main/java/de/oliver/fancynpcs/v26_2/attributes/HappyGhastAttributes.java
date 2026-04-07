package de.oliver.fancynpcs.v26_2.attributes;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcAttribute;
import de.oliver.fancynpcs.v26_2.ReflectionHelper;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.happyghast.HappyGhast;
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
            case "white" -> Items.HARNESS.white().getDefaultInstance();
            case "orange" -> Items.HARNESS.orange().getDefaultInstance();
            case "magenta" -> Items.HARNESS.magenta().getDefaultInstance();
            case "light_blue" -> Items.HARNESS.lightBlue().getDefaultInstance();
            case "yellow" -> Items.HARNESS.yellow().getDefaultInstance();
            case "lime" -> Items.HARNESS.lime().getDefaultInstance();
            case "pink" -> Items.HARNESS.pink().getDefaultInstance();
            case "gray" -> Items.HARNESS.gray().getDefaultInstance();
            case "light_gray" -> Items.HARNESS.lightGray().getDefaultInstance();
            case "cyan" -> Items.HARNESS.cyan().getDefaultInstance();
            case "purple" -> Items.HARNESS.purple().getDefaultInstance();
            case "blue" -> Items.HARNESS.blue().getDefaultInstance();
            case "brown" -> Items.HARNESS.brown().getDefaultInstance();
            case "green" -> Items.HARNESS.green().getDefaultInstance();
            case "red" -> Items.HARNESS.red().getDefaultInstance();
            case "black" -> Items.HARNESS.black().getDefaultInstance();
            default -> Items.AIR.getDefaultInstance();
        };

        if (!harnessItem.isEmpty()) {
            ghast.setItemSlot(EquipmentSlot.BODY, harnessItem);
        }
    }
}
