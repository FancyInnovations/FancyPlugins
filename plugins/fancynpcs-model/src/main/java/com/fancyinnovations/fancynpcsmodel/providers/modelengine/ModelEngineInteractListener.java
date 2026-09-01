package com.fancyinnovations.fancynpcsmodel.providers.modelengine;

import com.ticxo.modelengine.api.events.BaseEntityInteractEvent;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;

/**
 * Routes clicks on ModelEngine hitboxes to the FancyNpcs interaction system.
 */
public class ModelEngineInteractListener implements Listener {

    private final ModelEngineProvider provider;

    public ModelEngineInteractListener(ModelEngineProvider provider) {
        this.provider = provider;
    }

    @EventHandler
    public void onBaseEntityInteract(BaseEntityInteractEvent event) {
        Npc npc = provider.getNpcForBase(event.getBaseEntity());
        if (npc == null) {
            return;
        }

        switch (event.getAction()) {
            case ATTACK -> npc.interact(event.getPlayer(), ActionTrigger.LEFT_CLICK);
            case INTERACT -> {
                if (event.getSlot() == EquipmentSlot.HAND) {
                    npc.interact(event.getPlayer(), ActionTrigger.RIGHT_CLICK);
                }
            }
            case INTERACT_ON -> {
                // fired together with INTERACT - ignored to prevent double triggering
            }
        }
    }
}
