package com.fancyinnovations.fancyholograms.trait.builtin;

import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.trait.HologramTrait;
import com.fancyinnovations.fancyholograms.api.trait.HologramTraitClass;
import com.fancyinnovations.fancyholograms.util.PluginUtils;
import de.oliver.fancyanalytics.logger.properties.ThrowableProperty;
import com.fancyinnovations.fancynpcs.api.FancyNpcsPlugin;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.NpcAttribute;
import com.fancyinnovations.fancynpcs.api.NpcData;
import com.fancyinnovations.fancynpcs.api.actions.ActionTrigger;
import com.fancyinnovations.fancynpcs.api.actions.NpcAction;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApiStatus.Experimental
@HologramTraitClass(traitName = "interaction_trait")
public class InteractionTrait extends HologramTrait {

    private static final FancyNpcsPlugin FN = FancyNpcsPlugin.get();
    private Configuration config;
    private Npc hitbox;

    public InteractionTrait() {
        if (!PluginUtils.isFancyNpcsEnabled()) {
            throw new IllegalStateException("FancyNpcs is not enabled. Please enable it to use InteractionTrait.");
        }
    }

    @Override
    public void onAttach() {
        this.load();
        this.updateHitbox();
    }

    @Override
    public void onSpawn(Player player) {
        this.updateHitbox();
    }

    @Override
    public void onDespawn(Player player) {
        this.updateHitbox();
    }

    @Override
    public void onModify() {
        this.updateHitbox();
    }

    @Override
    public void onUnregister() {
        if (this.hitbox != null) {
            this.hitbox.removeForAll();
            FN.getNpcManager().removeNpc(this.hitbox);
            this.hitbox = null;
        }
    }

    @Override
    public void save() {
        try {
            storage.set(hologram.getData().getName(), config);
        } catch (Exception e) {
            logger.error("Failed to save configuration for InteractionTrait", ThrowableProperty.of(e));
        }
    }

    @Override
    public void load() {
        try {
            config = storage.get(hologram.getData().getName(), Configuration.class);
        } catch (Exception e) {
            logger.error("Failed to load configuration for InteractionTrait", ThrowableProperty.of(e));
        }
        if (config == null) {
            config = new Configuration(List.of(
                    new ActionConfig("message", "Hi, you just clicked a hologram!")
            ));
            save();
        }
    }

    private void updateHitbox() {
        if (FN.getNpcManager().getNpc("hologram_hitbox_for_" + hologram.getData().getName()) == null) {
            this.hitbox = FN.getNpcAdapter().apply(new NpcData(
                    "hologram_hitbox_for_" + hologram.getData().getName(),
                    UUID.randomUUID(),
                    hologram.getData().getLocation()
            ));
            this.hitbox.setSaveToFile(false);
            this.hitbox.getData().setType(EntityType.INTERACTION);
            this.hitbox.getData().setDisplayName("<empty>");

            List<NpcAction.NpcActionData> actions = new ArrayList<>();
            for (ActionConfig acfg : this.config.actions()) {
                NpcAction action = FN.getActionManager().getActionByName(acfg.action());
                if (action == null) {
                    logger.warn("Action " + acfg.action() + " is not registered");
                    continue;
                }

                actions.add(new NpcAction.NpcActionData(actions.size(), action, acfg.value()));
            }

            this.hitbox.getData().getActions().put(ActionTrigger.ANY_CLICK, actions);

            this.hitbox.create();
            this.hitbox.spawnForAll();
            FN.getNpcManager().registerNpc(this.hitbox);
        }

        this.hitbox.getData().setLocation(hologram.getData().getLocation());

        NpcAttribute widthAttr = FN.getAttributeManager().getAttributeByName(EntityType.INTERACTION, "width");
        this.hitbox.getData().addAttribute(widthAttr, String.valueOf(calcWidth()));

        NpcAttribute heightAttr = FN.getAttributeManager().getAttributeByName(EntityType.INTERACTION, "height");
        this.hitbox.getData().addAttribute(heightAttr, String.valueOf(calcHeight()));

        this.hitbox.updateForAll();
    }

    private double calcHeight() {
        int lines = ((TextHologramData) hologram.getData()).getText().size();
        double heightPerLine = 0.25;
        double scale = ((TextHologramData) hologram.getData()).getScale().y;
        return lines * heightPerLine * scale;
    }

    private double calcWidth() {
        List<String> lines = ((TextHologramData) hologram.getData()).getText();
        double maxCharacters = 0;
        for (String line : lines) {
            if (line.length() > maxCharacters) {
                maxCharacters = line.length();
            }
        }

        double widthPerCharacter = 0.12;
        double scale = ((TextHologramData) hologram.getData()).getScale().x;
        return maxCharacters * widthPerCharacter * scale;
    }

    public record ActionConfig(
            String action,
            String value
    ) {

    }

    public record Configuration(
            List<ActionConfig> actions
    ) {

    }
}
