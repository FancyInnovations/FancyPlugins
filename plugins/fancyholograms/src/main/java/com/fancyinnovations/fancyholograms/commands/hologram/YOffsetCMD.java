package com.fancyinnovations.fancyholograms.commands.hologram;

import com.google.common.primitives.Floats;
import com.fancyinnovations.fancyholograms.api.events.HologramUpdateEvent;
import com.fancyinnovations.fancyholograms.api.hologram.Hologram;
import com.fancyinnovations.fancyholograms.commands.HologramCMD;
import com.fancyinnovations.fancyholograms.commands.Subcommand;
import com.fancyinnovations.fancyholograms.main.FancyHologramsPlugin;
import de.oliver.fancylib.MessageHelper;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class YOffsetCMD implements Subcommand {

    @Override
    public List<String> tabcompletion(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        return null;
    }

    @Override
    public boolean run(@NotNull CommandSender player, @Nullable Hologram hologram, @NotNull String[] args) {
        if (!(player.hasPermission("fancyholograms.hologram.edit.y_offset"))) {
            MessageHelper.error(player, "You don't have the required permission to edit a hologram");
            return false;
        }

        final var offset = Floats.tryParse(args[3]);

        if (offset == null) {
            MessageHelper.error(player, "Could not parse Y offset");
            return false;
        }

        if (Float.compare(offset, hologram.getData().getYOffset()) == 0) {
            MessageHelper.warning(player, "This hologram already has this Y offset");
            return false;
        }

        final var copied = hologram.getData().copy(hologram.getData().getName());
        copied.setYOffset(offset);

        if (!HologramCMD.callModificationEvent(hologram, player, copied, HologramUpdateEvent.HologramModification.Y_OFFSET)) {
            return false;
        }

        if (Float.compare(copied.getYOffset(), hologram.getData().getYOffset()) == 0) {
            MessageHelper.warning(player, "This hologram already has this Y offset");
            return false;
        }

        hologram.getData().setYOffset(copied.getYOffset());

        if (hologram.getData().getLinkedNpcName() != null) {
            FancyHologramsPlugin.get().getControllerImpl().syncHologramWithNpc(hologram);
        }

        if (FancyHologramsPlugin.get().getHologramConfiguration().isSaveOnChangedEnabled()) {
            FancyHologramsPlugin.get().getStorage().save(hologram.getData());
        }

        MessageHelper.success(player, "Changed Y offset");
        return true;
    }
}

