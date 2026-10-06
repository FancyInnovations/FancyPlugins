package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram.edit;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.FancyContext;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.BlockStatePropertySuggestion;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.BlockStateValueSuggestion;
import com.fancyinnovations.fancyholograms.commands.oldCommands.HologramCMD;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Map;
import java.util.UUID;

public final class BlockStateCMD extends FancyContext {

    public static final BlockStateCMD INSTANCE = new BlockStateCMD();

    private BlockStateCMD() {
    }

    @IsHologramType(types = {HologramType.BLOCK})
    @Command("hologram edit <hologram> blockstate <property> <value>")
    @Description("Changes a block state property for the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.blockstate")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(BlockStatePropertySuggestion.class) String property,
            final @NotNull @SuggestWith(BlockStateValueSuggestion.class) String value
    ) {
        BlockHologramData data = (BlockHologramData) hologram.getData();
        Map<String, String> properties = data.getBlockStateProperties();

        if (properties.isEmpty() || !properties.containsKey(property.toLowerCase())) {
            translator.translate("commands.hologram.edit.blockstate.invalid_property")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("material", data.getBlock().name())
                    .replace("property", property)
                    .replace("valid_properties", properties.isEmpty() ? "none" : String.join(", ", properties.keySet()))
                    .send(actor.sender());
            return;
        }

        String matchedProp = property.toLowerCase();
        String currentVal = properties.get(matchedProp);
        if (currentVal != null && currentVal.equalsIgnoreCase(value)) {
            translator.translate("commands.hologram.edit.blockstate.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("property", matchedProp)
                    .replace("value", value)
                    .send(actor.sender());
            return;
        }

        BlockHologramData copied = data.copy(data.getName());
        try {
            copied.setBlockStateProperty(matchedProp, value);
        } catch (Exception e) {
            translator.translate("commands.hologram.edit.blockstate.invalid_value")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("property", matchedProp)
                    .replace("value", value)
                    .send(actor.sender());
            return;
        }

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return;
        }

        data.setBlockStateProperty(matchedProp, value);

        for (UUID viewerUUID : hologram.getViewers()) {
            Player viewer = Bukkit.getPlayer(viewerUUID);
            if (viewer == null || !viewer.isOnline()) {
                continue;
            }

            hologram.despawnFrom(viewer);
            hologram.spawnTo(viewer);
        }

        if (config.isSaveOnChangedEnabled()) {
            plugin.getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.blockstate.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("property", matchedProp)
                .replace("value", value)
                .send(actor.sender());
    }
}
