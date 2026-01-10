package de.oliver.fancysitula.api.utils.entityData;

import de.oliver.fancysitula.api.packets.FS_ClientboundSetEntityDataPacket.EntityDataAccessor;

/**
 * Entity data accessors for SpellcasterIllager entities.
 * Applies to: Evoker, Illusioner.
 */
public class FS_SpellcasterData {

    /**
     * The spell being cast (SpellcasterIllager.IllagerSpell).
     * Value should be a Byte (spell ID):
     * 0 = NONE, 1 = SUMMON_VEX, 2 = FANGS, 3 = WOLOLO, 4 = DISAPPEAR, 5 = BLINDNESS
     */
    public static final EntityDataAccessor SPELL_CASTING = new EntityDataAccessor(
            "net.minecraft.world.entity.monster.SpellcasterIllager",
            "DATA_SPELL_CASTING_ID"
    );

    // Spell IDs
    public static final byte SPELL_NONE = 0;
    public static final byte SPELL_SUMMON_VEX = 1;
    public static final byte SPELL_FANGS = 2;
    public static final byte SPELL_WOLOLO = 3;
    public static final byte SPELL_DISAPPEAR = 4;
    public static final byte SPELL_BLINDNESS = 5;
}
