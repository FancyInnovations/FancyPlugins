package de.oliver.fancysitula.api.entities;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.entityData.FS_ItemDisplayData;
import org.bukkit.entity.EntityType;

import java.util.List;

public class FS_ItemDisplay extends FS_Display {

    protected final FS_ClientboundSetEntityDataPacket.EntityData itemData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_ItemDisplayData.ITEM, null);

    public FS_ItemDisplay() {
        super(EntityType.ITEM_DISPLAY);
    }

    public org.bukkit.inventory.ItemStack getItem() {
        return (org.bukkit.inventory.ItemStack) this.itemData.getValue();
    }

    public void setItem(org.bukkit.inventory.ItemStack item) {
        this.itemData.setValue(item);
    }

    @Override
    public List<FS_ClientboundSetEntityDataPacket.EntityData> getEntityData() {
        List<FS_ClientboundSetEntityDataPacket.EntityData> entityData = super.getEntityData();
        entityData.add(this.itemData);
        return entityData;
    }
}
