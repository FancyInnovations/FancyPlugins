package de.oliver.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import de.oliver.fancynpcs.FancyNpcs;
import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.events.NpcModifyEvent;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

public enum RotateCMD {
    INSTANCE;

    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command("npc rotate <npc> <yaw> <pitch>")
    @Permission("fancynpcs.command.npc.rotate")
    public void onRotate(
            final @NotNull CommandSender sender,
            final @NotNull Npc npc,
            final float yaw,
            final float pitch
    ) {
        final Location currentLocation = npc.getData().getLocation();
        final Location newLocation = currentLocation.clone();
        newLocation.setYaw(yaw);
        newLocation.setPitch(pitch);

        if (new NpcModifyEvent(npc, NpcModifyEvent.NpcModification.ROTATION, new float[]{yaw, pitch}, sender).callEvent()) {
            npc.getData().setLocation(newLocation);
            npc.updateForAll();
            translator.translate("npc_rotate_set_success")
                    .replace("npc", npc.getData().getName())
                    .replace("yaw", String.valueOf(yaw))
                    .replace("pitch", String.valueOf(pitch))
                    .send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
}