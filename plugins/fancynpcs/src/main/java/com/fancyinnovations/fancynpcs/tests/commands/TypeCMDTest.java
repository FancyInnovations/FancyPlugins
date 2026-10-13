package com.fancyinnovations.fancynpcs.tests.commands;

import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.tests.api.NpcTestEnv;
import de.oliver.plugintests.annotations.FPAfterEach;
import de.oliver.plugintests.annotations.FPBeforeEach;
import de.oliver.plugintests.annotations.FPTest;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import static de.oliver.plugintests.Expectable.expect;
import static de.oliver.plugintests.utils.Delay.delay;

public class TypeCMDTest {

    private Npc npc;
    private String npcName;

    @FPBeforeEach
    public void setUp(Player player) {
        npc = NpcTestEnv.givenDefaultNpcIsCreated();
        npcName = npc.getData().getName();

        NpcTestEnv.givenNpcIsRegistered(npc);
    }

    @FPAfterEach
    public void tearDown(Player player) {
        NpcTestEnv.givenNpcIsUnregistered(npc);

        npc = null;
        npcName = null;
    }

    @FPTest(name = "Set type to COW")
    public void setTypeToCow(Player player) {
        expect(player.performCommand("npc type " + npcName + " COW")).toBe(true);

        delay(() -> {
            expect(npc.getData().getType()).toBe(EntityType.COW);
        });
    }
}
