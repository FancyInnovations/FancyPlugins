package com.fancyinnovations.fancyholograms.commands.lampCommands.hologram;

import com.fancyinnovations.fancyholograms.api.data.BlockHologramData;
import com.fancyinnovations.fancyholograms.api.data.TextHologramData;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.api.hologram.HologramType;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.lampCommands.conditions.IsHologramType;
import com.fancyinnovations.fancyholograms.commands.lampCommands.suggestions.BlockMaterialSuggestion;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.api.actions.types.BlockUntilDoneAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Description;
import revxrsal.commands.annotation.SuggestWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.UUID;

public final class BlockCMD {

    public static final BlockCMD INSTANCE = new BlockCMD();

    private final FancyHologramsPlugin plugin = FancyHologramsPlugin.get();
    private final Translator translator = FancyHologramsPlugin.get().getTranslator();

    private BlockCMD() {
    }

    @IsHologramType(types = {HologramType.BLOCK})
    @Command("hologram-new edit <hologram> block <material>")
    @Description("Changes the block material for the hologram")
    @CommandPermission("fancyholograms.commands.hologram.edit.block")
    public void set(
            final @NotNull BukkitCommandActor actor,
            final @NotNull Hologram hologram,
            final @NotNull @SuggestWith(BlockMaterialSuggestion.class) Material material
    ) {
        if (!material.isBlock()) {
            translator.translate("commands.hologram.edit.block.not_a_block")
                    .withPrefix()
                    .replace("material", material.name())
                    .send(actor.sender());
            return;
        }

        BlockHologramData data = (BlockHologramData) hologram.getData();

        BlockHologramData copied = data.copy(data.getName());
        copied.setBlock(material);

        if (copied.getBlock().equals(data.getBlock())) {
            translator.translate("commands.hologram.edit.block.already_set")
                    .withPrefix()
                    .replace("hologram", hologram.getData().getName())
                    .replace("material", material.name())
                    .send(actor.sender());
            return;
        }

        if (!HologramCMD.callModificationEvent(hologram, actor.sender(), copied, HologramUpdateEvent.HologramModification.BILLBOARD)) {
            return;
        }

        data.setBlock(material);

        for (UUID viewerUUID : hologram.getViewers()) {
            Player viewer = Bukkit.getPlayer(viewerUUID);
            if (viewer == null || !viewer.isOnline()) {
                continue;
            }

            hologram.despawnFrom(viewer);
            hologram.spawnTo(viewer);
        }

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        translator.translate("commands.hologram.edit.block.updated")
                .withPrefix()
                .replace("hologram", hologram.getData().getName())
                .replace("material", material.name())
                .send(actor.sender());
    }
}
