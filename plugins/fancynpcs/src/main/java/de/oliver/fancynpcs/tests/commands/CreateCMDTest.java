package de.oliver.fancynpcs.tests.commands;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.NpcManager;
import de.oliver.plugintests.annotations.FPAfterEach;
import de.oliver.plugintests.annotations.FPBeforeEach;
import de.oliver.plugintests.annotations.FPTest;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

import static de.oliver.plugintests.Expectable.expect;
import static de.oliver.plugintests.utils.Delay.delay;

public class CreateCMDTest {

    private static final NpcManager NPC_MANAGER = FancyNpcsPlugin.get().getNpcManager();

    private String npcName;
    private Npc createdNpc;

    @FPBeforeEach
    public void setUp(Player player) {
        npcName = "test-" + UUID.randomUUID().toString().substring(0, 8);
        createdNpc = null;
    }

    @FPAfterEach
    public void tearDown(Player player) {
        if (createdNpc != null) {
            NPC_MANAGER.removeNpc(createdNpc);
        }

        expect(NPC_MANAGER.getNpc(npcName)).toBeNull();

        createdNpc = null;
        npcName = null;
    }

    @FPTest(name = "Create npc")
    public void createNpc(Player player) {
        expect(player.performCommand("npc create " + npcName)).toBe(true);

        delay(() -> {
            createdNpc = NPC_MANAGER.getNpc(npcName);
            expect(createdNpc).toBeDefined();

            expect(createdNpc.getEntityId()).toBeGreaterThan(-1);

            expect(createdNpc.getData().getName()).toEqual(npcName);
            expect(createdNpc.getData().getType()).toEqual(EntityType.PLAYER);
            expect(createdNpc.getData().getLocation()).toBeDefined();
            expect(createdNpc.getData().getLocation().getWorld().getName()).toEqual(player.getWorld().getName());
            expect(createdNpc.getData().getCreator()).toEqual(player.getUniqueId());
        });
    }

    @FPTest(name = "Create npc with type")
    public void createNpcWithType(Player player) {
        expect(player.performCommand("npc create " + npcName + " --type PIG")).toBe(true);

        delay(() -> {
            createdNpc = NPC_MANAGER.getNpc(npcName);
            expect(createdNpc).toBeDefined();

            expect(createdNpc.getEntityId()).toBeGreaterThan(-1);

            expect(createdNpc.getData().getType()).toEqual(EntityType.PIG);
        });
    }

    @FPTest(name = "Create npc with location")
    public void createNpcWithLocation(Player player) {
        expect(player.performCommand("npc create " + npcName + " --location 12 154 842")).toBe(true);

        delay(() -> {
            createdNpc = NPC_MANAGER.getNpc(npcName);
            expect(createdNpc).toBeDefined();

            expect(createdNpc.getEntityId()).toBeGreaterThan(-1);

            expect(createdNpc.getData().getLocation().x()).toEqual(12d);
            expect(createdNpc.getData().getLocation().y()).toEqual(154d);
            expect(createdNpc.getData().getLocation().z()).toEqual(842d);
        });
    }

    @FPTest(name = "Create npc with world")
    public void createNpcWithWorld(Player player) {
        String worldName;
        if (Bukkit.getWorld("world_the_nether") == null) {
            worldName = "world";
        } else {
            worldName = "world_the_nether";
        }

        expect(player.performCommand("npc create " + npcName + " --world " + worldName)).toBe(true);

        delay(() -> {
            createdNpc = NPC_MANAGER.getNpc(npcName);
            expect(createdNpc).toBeDefined();

            expect(createdNpc.getEntityId()).toBeGreaterThan(-1);

            expect(createdNpc.getData().getLocation().getWorld().getName()).toEqual(worldName);
        });
    }

    @FPTest(name = "Create npc with invalid name")
    public void createNpcWithInvalidName(Player player) {
        expect(player.performCommand("npc create " + "invalid.name")).toBe(true);

        delay(() -> {
            createdNpc = NPC_MANAGER.getNpc("invalid.name");
            expect(createdNpc).toBeNull();
        });
    }

    @FPTest(name = "Create npc with existing name")
    public void createNpcWithExistingName(Player player) {
        expect(player.performCommand("npc create " + npcName)).toBe(true);

        delay(() -> {
            createdNpc = NPC_MANAGER.getNpc(npcName);
            expect(createdNpc).toBeDefined();

            expect(player.performCommand("npc create " + npcName)).toBe(true);

            Npc existingNpc = NPC_MANAGER.getNpc(npcName);
            expect(existingNpc).toBeDefined();

            expect(existingNpc.getEntityId()).toBeGreaterThan(-1);
            expect(existingNpc).toEqual(createdNpc);
        });
    }

    @FPTest(name = "Create npc with all flags")
    public void createNpcWithAllFlags(Player player) {
        String worldName;
        if (Bukkit.getWorld("world_the_nether") == null) {
            worldName = "world";
        } else {
            worldName = "world_the_nether";
        }

        expect(player.performCommand("npc create " + npcName + " --type COW --location 137 131 -571 --world " + worldName)).toBe(true);

        delay(() -> {
            createdNpc = NPC_MANAGER.getNpc(npcName);
            expect(createdNpc).toBeDefined();

            expect(createdNpc.getEntityId()).toBeGreaterThan(-1);

            expect(createdNpc.getData().getType()).toEqual(EntityType.COW);
            expect(createdNpc.getData().getLocation().x()).toEqual(137d);
            expect(createdNpc.getData().getLocation().y()).toEqual(131d);
            expect(createdNpc.getData().getLocation().z()).toEqual(-571d);
            expect(createdNpc.getData().getLocation().getWorld().getName()).toEqual(worldName);
        });
    }
}
