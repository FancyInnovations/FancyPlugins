package de.oliver.fancysitula.api.utils;

import de.oliver.fancysitula.api.packets.FS_ServerboundPacket;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class FS_PacketListener {

    protected final FS_ServerboundPacket.Type packet;
    protected final List<Consumer<PacketReceivedEvent>> listeners;

    public FS_PacketListener(FS_ServerboundPacket.Type packet) {
        this.packet = packet;
        this.listeners = new ArrayList<>();
    }

    public abstract void inject(Player player);

    public void addListener(Consumer<PacketReceivedEvent> listener) {
        listeners.add(listener);
    }

    public FS_ServerboundPacket.Type getPacket() {
        return packet;
    }

    public record PacketReceivedEvent(
            FS_ServerboundPacket packet,
            Player player
    ) {
    }
}
