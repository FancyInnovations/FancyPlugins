package de.oliver.fancysitula.api.packets;

import net.kyori.adventure.key.Key;

import java.util.Optional;

public class FS_ServerboundCustomClickActionPacket extends FS_ServerboundPacket {

    private final Key id;
    private final Optional<String> payload;

    public FS_ServerboundCustomClickActionPacket(Type type, Key id, Optional<String> payload) {
        super(type);
        this.id = id;
        this.payload = payload;
    }

    public Key getId() {
        return id;
    }

    public Optional<String> getPayload() {
        return payload;
    }

    @Override
    public Type getType() {
        return super.getType();
    }
}
