package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket;

/**
 * Entity data accessors for Mannequin entity (1.21.11+)
 * Mannequin is a player-like entity that doesn't require tab list entries
 */
public class FS_MannequinData {

    /**
     * Use {@link de.oliver.fancysitula.api.utils.FS_GameProfile} as value
     * The skin profile for this mannequin
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor DATA_PROFILE =
            new FS_ClientboundSetEntityDataPacket.EntityDataAccessor(
                    "net.minecraft.world.entity.decoration.Mannequin",
                    "DATA_PROFILE"
            );

    /**
     * Use {@link Boolean} as value
     * Whether the mannequin is immovable (cannot be pushed)
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor DATA_IMMOVABLE =
            new FS_ClientboundSetEntityDataPacket.EntityDataAccessor(
                    "net.minecraft.world.entity.decoration.Mannequin",
                    "DATA_IMMOVABLE"
            );

    /**
     * Use {@link java.util.Optional<net.kyori.adventure.text.Component>} as value
     * Description text shown below the mannequin's name
     */
    public static final FS_ClientboundSetEntityDataPacket.EntityDataAccessor DATA_DESCRIPTION =
            new FS_ClientboundSetEntityDataPacket.EntityDataAccessor(
                    "net.minecraft.world.entity.decoration.Mannequin",
                    "DATA_DESCRIPTION"
            );

}
