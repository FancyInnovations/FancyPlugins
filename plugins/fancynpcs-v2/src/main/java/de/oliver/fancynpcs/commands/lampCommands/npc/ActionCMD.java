package de.oliver.fancynpcs.commands.lampCommands.npc;

import de.oliver.fancynpcs.api.Npc;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.commands.lampCommands.FancyContext;
import de.oliver.fancynpcs.commands.lampCommands.types.GreedyStringCommandType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Named;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.ParseWith;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.ArrayList;
import java.util.List;

public final class ActionCMD extends FancyContext {
    public static final ActionCMD INSTANCE = new ActionCMD();

    private ActionCMD() {
    }

    @Command("npc action <npc> <trigger> add <actionType>")
    @CommandPermission("fancynpcs.command.npc.action.add")
    public void onActionAdd(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @NotNull @Named("actionType") NpcAction actionType,
            final @Optional @Nullable @ParseWith(GreedyStringCommandType.class) String value
    ) {
        final CommandSender sender = actor.sender();
        if (!checkAddPermissions(sender, actionType)) {
            return;
        }

        if (value != null && DisplayNameCMD.INSTANCE.hasBlockedCommands(value)) {
            translator.translate("command_input_contains_blocked_command")
                    .withPrefix()
                    .send(sender);
            return;
        }

        if (actionType.requiresValue() && (value == null || value.isEmpty())) {
            translator.translate("npc_action_requires_value")
                    .withPrefix()
                    .send(sender);
            return;
        }

        if (actionType.getName().equalsIgnoreCase("player_command_as_op")) {
            translator.translate("npc_action_add_op_warning")
                    .withPrefix()
                    .send(sender);
            return;
        }

        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions().getOrDefault(trigger, new ArrayList<>());

        npc.getData().addAction(trigger, currentActions.size() + 1, actionType, value);
        translator.translate("npc_action_add_success")
                .withPrefix()
                .replaceStripped("total", String.valueOf(npc.getData().getActions(trigger).size()))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> add_before <index> <actionType>")
    @CommandPermission("fancynpcs.command.npc.action.addBefore")
    public void onActionAddBefore(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final int index,
            final @NotNull @Named("actionType") NpcAction actionType,
            final @Optional @Nullable @ParseWith(GreedyStringCommandType.class) String value
    ) {
        final CommandSender sender = actor.sender();
        if (!checkAddPermissions(sender, actionType)) {
            return;
        }

        if (value != null && DisplayNameCMD.INSTANCE.hasBlockedCommands(value)) {
            translator.translate("command_input_contains_blocked_command")
                    .withPrefix()
                    .send(sender);
            return;
        }

        if (actionType.requiresValue() && (value == null || value.isEmpty())) {
            translator.translate("npc_action_requires_value")
                    .withPrefix()
                    .send(sender);
            return;
        }

        if (actionType.getName().equalsIgnoreCase("player_command_as_op")) {
            translator.translate("npc_action_add_op_warning")
                    .withPrefix()
                    .send(sender);
            return;
        }

        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions(trigger);
        currentActions.add(Math.clamp(index - 1, 0, currentActions.size()), new NpcAction.NpcActionData(index, actionType, value));

        npc.getData().setActions(trigger, reorderActions(currentActions));
        translator.translate("npc_action_add_before_success")
                .withPrefix()
                .replaceStripped("number", String.valueOf(index))
                .replaceStripped("total", String.valueOf(npc.getData().getActions(trigger).size()))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> add_after <index> <actionType>")
    @CommandPermission("fancynpcs.command.npc.action.addAfter")
    public void onActionAddAfter(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final int index,
            final @NotNull @Named("actionType") NpcAction actionType,
            final @Optional @Nullable @ParseWith(GreedyStringCommandType.class) String value
    ) {
        final CommandSender sender = actor.sender();
        if (!checkAddPermissions(sender, actionType)) {
            return;
        }

        if (value != null && DisplayNameCMD.INSTANCE.hasBlockedCommands(value)) {
            translator.translate("command_input_contains_blocked_command")
                    .withPrefix()
                    .send(sender);
            return;
        }

        if (actionType.requiresValue() && (value == null || value.isEmpty())) {
            translator
                    .translate("npc_action_requires_value")
                    .withPrefix()
                    .send(sender);
            return;
        }

        if (actionType.getName().equalsIgnoreCase("player_command_as_op")) {
            translator.translate("npc_action_add_op_warning")
                    .withPrefix()
                    .send(sender);
            return;
        }

        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions(trigger);
        currentActions.add(Math.clamp(index, 0, currentActions.size() + 1), new NpcAction.NpcActionData(index, actionType, value));

        npc.getData().setActions(trigger, reorderActions(currentActions));
        translator.translate("npc_action_add_after_success")
                .withPrefix()
                .replaceStripped("number", String.valueOf(index))
                .replaceStripped("total", String.valueOf(npc.getData().getActions(trigger).size()))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> set <number> <actionType>")
    @CommandPermission("fancynpcs.command.npc.action.set")
    public void onActionSet(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final @NotNull Integer number,
            final @NotNull @Named("actionType") NpcAction actionType,
            final @Optional @Nullable @ParseWith(GreedyStringCommandType.class) String value
    ) {
        final CommandSender sender = actor.sender();
        if (!checkAddPermissions(sender, actionType)) {
            return;
        }

        if (value != null && DisplayNameCMD.INSTANCE.hasBlockedCommands(value)) {
            translator.translate("command_input_contains_blocked_command")
                    .withPrefix()
                    .send(sender);
            return;
        }

        if (actionType.requiresValue() && (value == null || value.isEmpty())) {
            translator.translate("npc_action_requires_value")
                    .withPrefix()
                    .send(sender);
            return;
        }

        if (actionType.getName().equalsIgnoreCase("player_command_as_op")) {
            translator.translate("npc_action_add_op_warning")
                    .withPrefix()
                    .send(sender);
            return;
        }

        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions(trigger);
        if (number < 1 || number > currentActions.size()) {
            translator.translate("npc_action_set_failure")
                    .withPrefix()
                    .replaceStripped("number", String.valueOf(number))
                    .send(sender);
            return;
        }

        currentActions.set(number - 1, new NpcAction.NpcActionData(number, actionType, value));
        npc.getData().setActions(trigger, currentActions);
        translator.translate("npc_action_set_success")
                .withPrefix()
                .replaceStripped("number", String.valueOf(number))
                .replaceStripped("total", String.valueOf(npc.getData().getActions(trigger).size()))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> remove <number>")
    @CommandPermission("fancynpcs.command.npc.action.remove")
    public void onActionRemove(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final int number
    ) {
        final CommandSender sender = actor.sender();
        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions(trigger);
        if (number < 1 || number > currentActions.size()) {
            translator.translate("npc_action_remove_failure")
                    .withPrefix()
                    .replaceStripped("number", String.valueOf(number))
                    .send(sender);
            return;
        }

        currentActions.remove(number - 1);

        npc.getData().setActions(trigger, reorderActions(currentActions));
        translator.translate("npc_action_remove_success")
                .withPrefix()
                .replaceStripped("number", String.valueOf(number))
                .replaceStripped("total", String.valueOf(npc.getData().getActions(trigger).size()))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> move_up <number>")
    @CommandPermission("fancynpcs.command.npc.action.moveUp")
    public void onActionMoveUp(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final int number
    ) {
        final CommandSender sender = actor.sender();
        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions(trigger);
        if (number <= 1) {
            translator.translate("npc_action_move_up_failure")
                    .withPrefix()
                    .replaceStripped("number", String.valueOf(number))
                    .send(sender);
            return;
        }

        NpcAction.NpcActionData action = currentActions.get(number - 1);
        currentActions.remove(number - 1);
        currentActions.add(number - 2, action);

        npc.getData().setActions(trigger, reorderActions(currentActions));
        translator.translate("npc_action_move_up_success")
                .withPrefix()
                .replaceStripped("number", String.valueOf(number))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> move_down <number>")
    @CommandPermission("fancynpcs.command.npc.action.moveDown")
    public void onActionMoveDown(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger,
            final int number
    ) {
        final CommandSender sender = actor.sender();
        List<NpcAction.NpcActionData> currentActions = npc.getData().getActions(trigger);
        if (number >= currentActions.size()) {
            translator.translate("npc_action_move_down_failure")
                    .withPrefix()
                    .replaceStripped("number", String.valueOf(number))
                    .send(sender);
            return;
        }

        NpcAction.NpcActionData action = currentActions.get(number - 1);
        currentActions.remove(number - 1);
        currentActions.add(number, action);

        npc.getData().setActions(trigger, reorderActions(currentActions));
        translator.translate("npc_action_move_down_success")
                .withPrefix()
                .replaceStripped("number", String.valueOf(number))
                .send(sender);
    }

    @Command("npc action <npc> <trigger> clear")
    @CommandPermission("fancynpcs.command.npc.action.clear")
    public void onActionClear(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger
    ) {
        final CommandSender sender = actor.sender();
        npc.getData().setActions(trigger, new ArrayList<>());
        translator.translate("npc_action_clear_success")
                .withPrefix()
                .send(sender);
    }

    @Command("npc action <npc> <trigger> list")
    @CommandPermission("fancynpcs.command.npc.action.list")
    public void onActionList(
            final BukkitCommandActor actor,
            final @NotNull Npc npc,
            final @NotNull ActionTrigger trigger
    ) {
        final CommandSender sender = actor.sender();
        List<NpcAction.NpcActionData> actions = npc.getData().getActions(trigger);
        if (actions.isEmpty()) {
            translator.translate("npc_action_list_failure_empty")
                    .withPrefix()
                    .send(sender);
            return;
        }

        translator.translate("npc_action_list_header")
                .replaceStripped("trigger", trigger.name())
                .send(sender);

        for (int i = 0; i < actions.size(); i++) {
            NpcAction.NpcActionData action = actions.get(i);
            translator.translate("npc_action_list_entry")
                    .replaceStripped("number", String.valueOf(action.order()))
                    .replaceStripped("action", action.action().getName())
                    .replaceStripped("value", action.value() != null ? action.value() : "")
                    .send(sender);
        }

        translator.translate("npc_action_list_footer")
                .replaceStripped("total", String.valueOf(actions.size()))
                .send(sender);
    }

    private List<NpcAction.NpcActionData> reorderActions(List<NpcAction.NpcActionData> actions) {
        List<NpcAction.NpcActionData> newActions = new ArrayList<>();
        for (int i = 0; i < actions.size(); i++) {
            NpcAction.NpcActionData a = actions.get(i);
            newActions.add(new NpcAction.NpcActionData(i + 1, a.action(), a.value()));
        }
        return newActions;
    }

    private boolean checkAddPermissions(final CommandSender sender, final NpcAction action) {
        boolean hasGeneralPerms = sender.hasPermission("fancynpcs.command.npc.action.add.*");
        boolean hasSpecificPerms = sender.hasPermission("fancynpcs.command.npc.action.add." + action.getName());

        if (hasGeneralPerms || hasSpecificPerms) {
            return true;
        }

        translator.translate("action_missing_permissions")
                .withPrefix()
                .send(sender);

        return false;
    }


}
