package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * The ExecuteRandomActionAction class represents an action that can be executed randomly by an NPC.
 * <p>
 * The ExecuteRandomActionAction class provides an implementation for the execute method,
 * which executes a random action triggered by the given action trigger on the specified NPC and player.
 * The execution of the action is based on the actions associated with the NPC's data for the given trigger.
 */
public class ExecuteRandomActionAction extends NpcAction {

    public ExecuteRandomActionAction() {
        super("execute_random_action", false);
    }

    /**
     * Executes a random action triggered by the given action trigger on the specified NPC and player.
     */
    @Override
    public void execute(@NotNull ActionExecutionContext context, String value) {
        int currentIndex = context.getActionIndex();
        int actionCount = context.getActions().size();

        if (currentIndex >= actionCount) {
            NpcActionData fallback = context.getActions().stream()
                    .filter(npcActionData -> !(npcActionData.action() instanceof ExecuteRandomActionAction))
                    .findFirst()
                    .orElse(null);

            String npcId = context.getNpc().getData().getId();
            String trigger = context.getTrigger().name();

            if (fallback == null) {
                FancyNpcsPlugin.get().getFancyLogger().warn("Misconfigured execute_random_action for npc '" + npcId + "' (trigger " + trigger + "): no other actions to choose from. Add at least one non-random action to this trigger. Skipping execution.");
                context.terminate();
                return;
            }

            FancyNpcsPlugin.get().getFancyLogger().warn("Misplaced execute_random_action for npc '" + npcId + "' (trigger " + trigger + "): no following actions to choose from. Falling back to first available action '" + fallback.action().getName() + "'. Move execute_random_action before the actions it should choose from.");
            fallback.action().execute(context, fallback.value());
            context.terminate();
            return;
        }

        int randomIndex = getRandomIndex(currentIndex, actionCount);

        NpcActionData action = context.getActions().get(randomIndex);
        action.action().execute(context, action.value());

        context.terminate();
    }

    private int getRandomIndex(int from, int to) {
        return new Random().nextInt(to - from) + from;
    }
}
