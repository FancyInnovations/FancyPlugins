package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.jetbrains.annotations.NotNull;

public class PlayAnimationAction extends NpcAction {

    public PlayAnimationAction() {
        super("play_animation", true);
    }

    @Override
    public void execute(@NotNull ActionExecutionContext context, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (context.getNpc() == null) {
            return;
        }

        String modelName = context.getNpc().getData().getModelName();
        if (modelName == null || modelName.isEmpty()) {
            return;
        }

        try {
            Class<?> hookClass = Class.forName("de.oliver.fancynpcs.bettermodel.BetterModelHook");
            java.lang.reflect.Method isAvailable = hookClass.getMethod("isAvailable");
            boolean available = (boolean) isAvailable.invoke(null);

            if (!available) {
                return;
            }

            Class<?> fancyNpcsClass = Class.forName("de.oliver.fancynpcs.FancyNpcs");
            java.lang.reflect.Method getInstance = fancyNpcsClass.getMethod("getInstance");
            Object instance = getInstance.invoke(null);

            java.lang.reflect.Method getModelController = fancyNpcsClass.getMethod("getModelController");
            Object controller = getModelController.invoke(instance);

            if (controller != null) {
                java.lang.reflect.Method playAnimation = controller.getClass().getMethod("playAnimation",
                    de.oliver.fancynpcs.api.Npc.class, String.class);
                playAnimation.invoke(controller, context.getNpc(), value);
            }
        } catch (Exception ignored) {
        }
    }
}
