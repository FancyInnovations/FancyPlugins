package de.oliver.fancynpcs.api;

import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class NpcAttribute {

    private final String name;
    private final List<String> possibleValues;
    private final Supplier<List<String>> possibleValuesSupplier;
    private final List<EntityType> types;
    private final BiConsumer<Npc, String> applyFunc; // npc, value

    public NpcAttribute(String name, List<String> possibleValues, List<EntityType> types, BiConsumer<Npc, String> applyFunc) {
        this.name = name;
        this.possibleValues = possibleValues;
        this.possibleValuesSupplier = List::of;
        this.types = types;
        this.applyFunc = applyFunc;
    }

    public NpcAttribute(String name, Supplier<List<String>> possibleValuesSupplier, List<EntityType> types, BiConsumer<Npc, String> applyFunc) {
        this.name = name;
        this.possibleValues = List.of();
        this.possibleValuesSupplier = possibleValuesSupplier;
        this.types = types;
        this.applyFunc = applyFunc;
    }

    public boolean isValidValue(String value) {
        List<String> fromSupplier = possibleValuesSupplier.get();

        if (possibleValues.isEmpty() && fromSupplier.isEmpty()) {
            return true;
        }

        for (String pv : possibleValues) {
            if (pv.equalsIgnoreCase(value)) {
                return true;
            }
        }

        for (String pv : fromSupplier) {
            if (pv.equalsIgnoreCase(value)) {
                return true;
            }
        }

        return false;
    }

    public void apply(Npc npc, String value) {
        applyFunc.accept(npc, value);
    }

    public String getName() {
        return name;
    }

    /**
     * @return A list of possible values for this attribute. This list is a combination of the static possible values and the values provided by the supplier.
     */
    public List<String> getPossibleValues() {
        List<String> values = new ArrayList<>();
        values.addAll(possibleValuesSupplier.get());
        values.addAll(possibleValues);
        return values;
    }

    public List<EntityType> getTypes() {
        return types;
    }

}
