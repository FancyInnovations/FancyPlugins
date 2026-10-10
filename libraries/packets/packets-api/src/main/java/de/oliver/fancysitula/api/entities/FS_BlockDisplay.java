package de.oliver.fancysitula.api.entities;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;
import de.oliver.fancysitula.api.utils.entityData.FS_BlockDisplayData;
import org.bukkit.block.BlockState;
import org.bukkit.entity.EntityType;

import java.util.List;

public class FS_BlockDisplay extends FS_Display {

    protected final FS_ClientboundSetEntityDataPacket.EntityData blockData = new FS_ClientboundSetEntityDataPacket.EntityData(FS_BlockDisplayData.BLOCK, null);

    public FS_BlockDisplay() {
        super(EntityType.BLOCK_DISPLAY);
    }

    public BlockState getBlock() {
        return (BlockState) this.blockData.getValue();
    }

    public void setBlock(BlockState block) {
        this.blockData.setValue(block);
    }

    @Override
    public List<FS_ClientboundSetEntityDataPacket.EntityData> getEntityData() {
        List<FS_ClientboundSetEntityDataPacket.EntityData> entityData = super.getEntityData();
        entityData.add(this.blockData);
        return entityData;
    }
}
