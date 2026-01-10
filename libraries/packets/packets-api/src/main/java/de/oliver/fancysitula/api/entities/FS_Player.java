package de.oliver.fancysitula.api.entities;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.FS_EquipmentSlot;
import de.oliver.fancysitula.api.utils.entityData.FS_PlayerData;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FS_Player extends FS_Entity {

    protected Map<FS_EquipmentSlot, ItemStack> equipment;

    protected FS_ClientboundSetEntityDataPacket.EntityData skinCustomizationData =
            new FS_ClientboundSetEntityDataPacket.EntityData(FS_PlayerData.SKIN_CUSTOMIZATION, FS_PlayerData.SKIN_ALL);

    public FS_Player() {
        super(EntityType.PLAYER);
        this.equipment = new ConcurrentHashMap<>();
    }

    public Map<FS_EquipmentSlot, ItemStack> getEquipment() {
        return Collections.unmodifiableMap(equipment);
    }

    public ItemStack getEquipment(FS_EquipmentSlot slot) {
        return equipment.get(slot);
    }

    public void setEquipment(Map<FS_EquipmentSlot, ItemStack> equipment) {
        if (equipment == null) {
            this.equipment.clear();
            return;
        }
        this.equipment.clear();
        this.equipment.putAll(equipment);
    }

    public void setEquipment(FS_EquipmentSlot slot, ItemStack item) {
        if (slot == null) {
            return;
        }
        if (item == null) {
            this.equipment.remove(slot);
        } else {
            this.equipment.put(slot, item);
        }
    }

    public byte getSkinCustomization() {
        return (byte) skinCustomizationData.getValue();
    }

    public void setSkinCustomization(byte skinCustomization) {
        this.skinCustomizationData.setValue(skinCustomization);
    }

    @Override
    public List<FS_ClientboundSetEntityDataPacket.EntityData> getEntityData() {
        List<FS_ClientboundSetEntityDataPacket.EntityData> entityData = super.getEntityData();
        entityData.add(this.skinCustomizationData);
        return entityData;
    }
}
