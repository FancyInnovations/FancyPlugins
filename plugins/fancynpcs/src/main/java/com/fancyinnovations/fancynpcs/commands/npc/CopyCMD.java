package com.fancyinnovations.fancynpcs.commands.npc;

import de.oliver.fancylib.translations.Translator;
import com.fancyinnovations.fancynpcs.FancyNpcs;
import com.fancyinnovations.fancynpcs.api.Npc;
import com.fancyinnovations.fancynpcs.api.NpcData;
import com.fancyinnovations.fancynpcs.api.events.NpcCreateEvent;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

// TO-DO: Console support with --position and --world parameter flags.
public enum CopyCMD {
    INSTANCE; // SINGLETON

    private static final Pattern NPC_NAME_PATTERN = Pattern.compile("^[A-Za-z0-9/_-]*$");
    private final Translator translator = FancyNpcs.getInstance().getTranslator();

    @Command(value = "npc copy <npc> <name>", requiredSender = Player.class)
    @Permission("fancynpcs.command.npc.copy")
    public void onCopy(
            final @NotNull Player sender,
            final @NotNull Npc npc,
            final @NotNull String name
    ) {
        // Sending error message if name does not match configured pattern.
        if (!NPC_NAME_PATTERN.matcher(name).find()) {
            translator.translate("npc_create_failure_invalid_name").replaceStripped("name", name).send(sender);
            return;
        }
        // Creating a copy of an NPC and all it's data. The only different thing is it's UUID.
        final Npc copied = FancyNpcs.getInstance().getNpcAdapter().apply(
                new NpcData(
                        UUID.randomUUID().toString(),
                        name,
                        sender.getUniqueId(),
                        npc.getData().getDisplayName(),
                        npc.getData().getSkinData(),
                        sender.getLocation().clone(),
                        npc.getData().isShowInTab(),
                        npc.getData().isSpawnEntity(),
                        npc.getData().isCollidable(),
                        npc.getData().isGlowing(),
                        npc.getData().getGlowingColor(),
                        npc.getData().getType(),
                        new ConcurrentHashMap<>(npc.getData().getEquipment()),
                        npc.getData().isTurnToPlayer(),
                        npc.getData().getTurnToPlayerDistance(),
                        npc.getData().getOnClick(),
                        npc.getData().getActions()
                                .entrySet()
                                .stream()
                                .collect(Collectors.toConcurrentMap(
                                        Map.Entry::getKey,
                                        e -> new ArrayList<>(e.getValue())
                                )),
                        npc.getData().getInteractionCooldown(),
                        npc.getData().getScale(),
                        npc.getData().getVisibilityDistance(),
                        new ConcurrentHashMap<>(npc.getData().getAttributes()),
                        npc.getData().isMirrorSkin()
                ));
        // Calling the event and creating + registering copied NPC if not cancelled.
        if (new NpcCreateEvent(copied, sender).callEvent()) {
            copied.create();
            FancyNpcs.getInstance().getNpcManagerImpl().registerNpc(copied);
            copied.spawnForAll();
            // Save immediately if save-on-changed is enabled
            if (FancyNpcs.getInstance().getFancyNpcConfig().isSaveOnChangedEnabled()) {
                FancyNpcs.getInstance().getNpcStorage().save(copied);
            }
            translator.translate("npc_copy_success").replace("npc", npc.getData().getName()).replace("new_npc", copied.getData().getName()).send(sender);
        } else {
            translator.translate("command_npc_modification_cancelled").send(sender);
        }
    }
}
