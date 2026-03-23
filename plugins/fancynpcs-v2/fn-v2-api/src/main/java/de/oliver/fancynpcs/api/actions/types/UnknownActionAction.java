package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.ActionTrigger;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UnknownActionAction extends NpcAction {

    private final ActionTrigger trigger;
    private final String unknownActionName;
    private final String unknownActionValue;
    private final int order;

    public UnknownActionAction(ActionTrigger trigger, String unknownActionName, String unknownActionValue, int order) {
        super("unknown_action", true);
        this.trigger = trigger;
        this.unknownActionName = unknownActionName;
        this.unknownActionValue = unknownActionValue;
        this.order = order;
    }

    @Override
    public void execute(@NotNull ActionExecutionContext context, @Nullable String value) {
        NpcAction action = FancyNpcsPlugin.get().getActionManager().getActionByName(unknownActionName);
        if (action == null) {
            FancyNpcsPlugin.get().getFancyLogger().warn("Could not find action: " + unknownActionName);
            return;
        }

        action.execute(context, value);

        // TODO replace THIS action with the real action in the npc's actions list, so that next time it will be executed directly without needing to go through this UnknownActionAction

    }

    @Override
    public String getName() {
        return unknownActionName + " (unknown)";
    }

    public ActionTrigger getTrigger() {
        return trigger;
    }

    public String getUnknownActionName() {
        return unknownActionName;
    }

    public String getUnknownActionValue() {
        return unknownActionValue;
    }

    public int getOrder() {
        return order;
    }
}
