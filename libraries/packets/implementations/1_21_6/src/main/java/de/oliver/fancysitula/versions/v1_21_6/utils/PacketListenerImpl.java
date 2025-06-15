package de.oliver.fancysitula.versions.v1_21_6.utils;

import de.oliver.fancysitula.api.packets.FS_ServerboundCustomClickActionPacket;
import de.oliver.fancysitula.api.packets.FS_ServerboundPacket;
import de.oliver.fancysitula.api.utils.FS_PacketListener;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

public class PacketListenerImpl extends FS_PacketListener {

    private static final String PIPELINE_NAME = "fancysitula-packet-injector";

    public PacketListenerImpl(FS_ServerboundPacket.Type packet) {
        super(packet);
    }

    @Override
    public void inject(Player player) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        Channel channel = serverPlayer.connection.connection.channel;

        if (channel.pipeline().get(PIPELINE_NAME) != null) {
            return;
        }

        channel.pipeline().addAfter("decoder", PIPELINE_NAME, new MessageToMessageDecoder<Packet<?>>() {
            @Override
            protected void decode(ChannelHandlerContext ctx, Packet<?> msg, List<Object> out) {
                out.add(msg);

                FS_ServerboundPacket.Type packetType = getPacketType(msg);
                if (packetType == null) {
                    return; // Unsupported packet type
                }

                if (packet == FS_ServerboundPacket.Type.ALL) {
                    FS_ServerboundPacket fsPacket = convert(packetType, msg);
                    PacketReceivedEvent packetReceivedEvent = new PacketReceivedEvent(fsPacket, player);
                    listeners.forEach(listener -> listener.accept(packetReceivedEvent));
                    return;
                }

                if (packet == packetType) {
                    FS_ServerboundPacket fsPacket = convert(packetType, msg);
                    PacketReceivedEvent packetReceivedEvent = new PacketReceivedEvent(fsPacket, player);
                    listeners.forEach(listener -> listener.accept(packetReceivedEvent));
                }
            }
        });
    }

    private FS_ServerboundPacket.Type getPacketType(Packet<?> packet) {
        String className = packet.getClass().getSimpleName();
        for (FS_ServerboundPacket.Type type : FS_ServerboundPacket.Type.values()) {
            if (type.getPacketClassName().equalsIgnoreCase(className)) {
                return type;
            }
        }

        return null;
    }

    private FS_ServerboundPacket convert(FS_ServerboundPacket.Type type, Packet<?> packet) {
        switch (type) {
            case CUSTOM_CLICK_ACTION -> {
                ServerboundCustomClickActionPacket customClickActionPacket = (ServerboundCustomClickActionPacket) packet;
                return new FS_ServerboundCustomClickActionPacket(
                        type,
                        PaperAdventure.asAdventure(customClickActionPacket.id()),
                        customClickActionPacket.payload().isPresent() ?
                                customClickActionPacket.payload().get().asCompound().get().getString("data") :
                                Optional.empty()
                );
            }
            // Add more cases for other packet types as needed
            default -> throw new IllegalArgumentException("Unsupported packet type: " + type);
        }
    }
}
