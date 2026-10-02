package de.oliver.fancynpcs.commands.lampCommands.types;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.autocomplete.SuggestionProvider;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.node.ExecutionContext;
import revxrsal.commands.parameter.ParameterType;
import revxrsal.commands.stream.MutableStringStream;

import java.util.ArrayList;
import java.util.stream.StreamSupport;

public final class ItemStackCommandType implements ParameterType<BukkitCommandActor, ItemStack> {

    public static final ItemStackCommandType INSTANCE = new ItemStackCommandType();

    private ItemStackCommandType() {
    }

    @Override
    public ItemStack parse(@NotNull MutableStringStream input, @NotNull ExecutionContext<BukkitCommandActor> context) {
        String value = input.readString().toLowerCase();
        if (value.equals("@none")) return new ItemStack(Material.AIR);
        if (value.equals("@hand") && context.actor().isPlayer()) {
            return context.actor().requirePlayer().getInventory().getItemInMainHand().clone();
        }

        NamespacedKey key = NamespacedKey.fromString(value);

        Material material = key == null ? null : Registry.MATERIAL.get(key);
        if (material == null || !material.isItem()) {
            throw new FancyNpcsParameterException("command_invalid_material", value);
        }

        return new ItemStack(material);
    }

    @Override
    public SuggestionProvider<BukkitCommandActor> defaultSuggestions() {
        return context -> {
            ArrayList<String> suggestions = new java.util.ArrayList<>();

            StreamSupport.stream(Registry.MATERIAL.spliterator(), false)
                    .filter(Material::isItem)
                    .map(material -> material.key().asString())
                    .forEach(suggestions::add);

            suggestions.add("@none");

            if (context.actor().isPlayer()) {
                suggestions.add("@hand");
            }

            return suggestions;
        };
    }
}
