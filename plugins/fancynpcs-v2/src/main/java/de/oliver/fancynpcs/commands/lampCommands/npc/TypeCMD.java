package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

public final class TypeCMD extends FancyContext {
    public static final TypeCMD INSTANCE = new TypeCMD();

    private TypeCMD() {
    }

    @Command("npc type <npc> <type>")
    @CommandPermission("fancynpcs.command.npc.type")
    public void onType(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull EntityType type
    ) {
        final CommandSender sender = actor.sender();
        // Calling the event and updating the type if not cancelled.
        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.TYPE, type, sender).callEvent()) {
            npc.getData().clearAttributes();

            npc.getData().setType(type);

            if (type != EntityType.PLAYER) {
                npc.getData().setShowInTab(false);
                npc.getData().setSkinData(null);
                npc.getData().setMirrorSkin(false);
            }

            if (!type.isAlive() && npc.getData().getEquipment() != null) {
                npc.getData().getEquipment().clear();
            }

            if (type == EntityType.ENDER_DRAGON) {
                npc.removeForAll();
                npc.create();
                Bukkit.getOnlinePlayers().forEach(npc::checkAndUpdateVisibility);
            } else {
                FancyNpcsPlugin.get().getNpcThread().submit(() -> {
                    npc.removeForAll();
                    npc.create();
                    Bukkit.getOnlinePlayers().forEach(npc::checkAndUpdateVisibility);
                });
            }
            translator.translate("npc_type_success").withPrefix().replace("npc", npc.getData().getName()).replace("type", type.name().toLowerCase()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").withPrefix().send(sender);
        }
    }
}
