package com.fancyinnovations.fancynpcs;

import com.fancyinnovations.fancynpcs.api.AttributeManager;
import com.fancyinnovations.fancynpcs.api.NpcAttribute;
import com.fancyinnovations.fancynpcs.npc.Attributes;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class AttributeManagerImpl implements AttributeManager {

    private List<NpcAttribute> attributes;

    public AttributeManagerImpl() {
        this.attributes = new ArrayList<>();
        init();
    }

    private void init() {
        attributes = Attributes.getAllAttributes();
    }

    @Override
    public NpcAttribute getAttributeByName(EntityType type, String name) {
        for (NpcAttribute attribute : attributes) {
            if (attribute.getTypes().contains(type) && attribute.getName().equalsIgnoreCase(name)) {
                return attribute;
            }
        }

        return null;
    }

    @Override
    public void registerAttribute(NpcAttribute attribute) {
        attributes.add(attribute);
    }

    @Override
    public List<NpcAttribute> getAllAttributes() {
        return attributes;
    }

    @Override
    public List<NpcAttribute> getAllAttributesForEntityType(EntityType type) {
        return attributes.stream()
                .filter(attribute -> attribute.getTypes().contains(type))
                .toList();
    }
}
