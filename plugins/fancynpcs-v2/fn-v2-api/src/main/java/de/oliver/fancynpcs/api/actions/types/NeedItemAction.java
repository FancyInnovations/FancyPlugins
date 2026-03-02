package de.oliver.fancynpcs.api.actions.types;

import de.oliver.fancynpcs.api.FancyNpcsPlugin;
import de.oliver.fancynpcs.api.actions.NpcAction;
import de.oliver.fancynpcs.api.actions.executor.ActionExecutionContext;
import org.bukkit.Material;

public class NeedItemAction extends NpcAction {

    public NeedItemAction() {
        super("need_item", true);
    }

    @Override
    public void execute(ActionExecutionContext context, String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        if (context.getPlayer() == null) {
            return;
        }

        String[] args = value.split(" ");
        if (args.length == 0) {
            return;
        }

        boolean invertCheck = args[0].startsWith("!");
        String item = invertCheck ? args[0].substring(1) : args[0];
        Material material = Material.getMaterial(item.toUpperCase());
        if (material == null) {
            FancyNpcsPlugin.get().getFancyLogger().warn("Invalid material specified in need_item action: " + item);
            return;
        }

        boolean passesCheck = false;
        if (args.length == 1) {
            boolean hasItem = context.getPlayer().getInventory().contains(material);
            passesCheck = invertCheck ? !hasItem : hasItem;
        } else if (args.length == 2) {
            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                FancyNpcsPlugin.get().getFancyLogger().warn("Invalid amount specified in need_item action: " + args[1]);
                return;
            }

            boolean hasItem = context.getPlayer().getInventory().contains(material, amount);
            passesCheck = invertCheck ? !hasItem : hasItem;
        }


        if (!passesCheck) {
            FancyNpcsPlugin.get().getTranslator()
                    .translate("action_missing_item")
                    .replace("item", material.name())
                    .replace("amount", args.length == 2 ? args[1] : "1")
                    .send(context.getPlayer());

            context.terminate();
        }
    }
}
