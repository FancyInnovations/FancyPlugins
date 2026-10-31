package de.oliver.fancyholograms.listeners;

import de.oliver.fancyholograms.FancyHolograms;
import de.oliver.fancyholograms.api.data.HologramData;
import de.oliver.fancyholograms.api.hologram.Hologram;
import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.MessageHelper;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.api.events.NpcRemoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public final class NpcListener implements Listener {

    private final @NotNull FancyHolograms plugin;

    public NpcListener(@NotNull final FancyHolograms plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onRemove(@NotNull final NpcRemoveEvent event) {
        this.plugin.getHologramsManager()
                .getHolograms()
                .stream()
                .filter(hologram -> event.getNpc().getData().getName().equals(hologram.getData().getLinkedNpcName()))
                .forEach(hologram -> hologram.getData().setLinkedNpcName(null));
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onModify(@NotNull final NpcModifyEvent event) {
        final var holograms = this.plugin.getHologramsManager().getHolograms();

        switch (event.getModification()) {
            case TYPE, LOCATION, SCALE, ATTRIBUTE -> {
                final var needsToBeUpdated = holograms.stream()
                        .filter(hologram -> event.getNpc().getData().getName().equals(hologram.getData().getLinkedNpcName()))
                        .toList();

                FancyLib.getInstance().getScheduler().runTaskLater(null, 1L, () -> needsToBeUpdated.forEach(this.plugin.getHologramsManager()::syncHologramWithNpc));
            }
            case NAME -> {
                if (!(event.getNewValue() instanceof final String newName)) {
                    break;
                }

                final var npc = event.getNpc();
                final var oldName = npc.getData().getName();
                if (oldName.equals(newName)) {
                    break;
                }

                final var linked = holograms.stream()
                        .filter(hologram -> oldName.equals(hologram.getData().getLinkedNpcName()))
                        .toList();
                if (linked.isEmpty()) {
                    break;
                }

                // Update links immediately: the event carries old and new name, so chained
                // renames stay consistent. Sync is deferred until the rename completed.
                linked.forEach(hologram -> hologram.getData().setLinkedNpcName(newName));

                FancyLib.getInstance().getScheduler().runTaskLater(null, 1L, () -> linked.forEach(hologram -> {
                    if (!newName.equals(hologram.getData().getLinkedNpcName())) {
                        return; // Moved on by a later rename, leave it alone.
                    }

                    if (!npc.getData().getName().equals(newName)) {
                        // Rename didn't happen (event cancelled afterwards) -> revert ours.
                        hologram.getData().setLinkedNpcName(oldName);
                        if (this.plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                            this.plugin.getHologramStorage().save(hologram);
                        }
                        return;
                    }

                    this.plugin.getHologramsManager().syncHologramWithNpc(hologram);
                    if (this.plugin.getHologramConfiguration().isSaveOnChangedEnabled()) {
                        this.plugin.getHologramStorage().save(hologram);
                    }
                }));
            }
            case DISPLAY_NAME, SHOW_IN_TAB -> {
                final var isLinked = holograms.stream()
                        .map(Hologram::getData)
                        .map(HologramData::getLinkedNpcName)
                        .anyMatch(event.getNpc().getData().getName()::equals);

                if (isLinked) {
                    event.setCancelled(true);
                    MessageHelper.error(event.getModifier(), "This modification is not allowed on a hologram linked npc");
                }
            }
        }
    }

}
