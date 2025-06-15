package de.oliver.fancysitula.api.packets;

public abstract class FS_ServerboundPacket {

    protected final Type type;

    public FS_ServerboundPacket(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        ALL("*"),
        CUSTOM_CLICK_ACTION("ServerboundCustomClickActionPacket"),
        ;

        private String packetClassName;

        Type(String packetClassName) {
            this.packetClassName = packetClassName;
        }

        public String getPacketClassName() {
            return packetClassName;
        }
    }
}
