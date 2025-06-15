package de.oliver.fancysitula;

import de.oliver.fancyanalytics.logger.LogLevel;
import de.oliver.fancysitula.api.IFancySitula;
import de.oliver.fancysitula.api.packets.FS_ServerboundCustomClickActionPacket;
import de.oliver.fancysitula.api.packets.FS_ServerboundPacket;
import de.oliver.fancysitula.api.utils.FS_PacketListener;
import de.oliver.fancysitula.commands.FancySitulaCMD;
import de.oliver.fancysitula.factories.FancySitula;
import de.oliver.fancysitula.listener.PlayerJoinListener;
import org.bukkit.plugin.java.JavaPlugin;

public class FancySitulaPlugin extends JavaPlugin {

    private static FancySitulaPlugin instance;
    private FS_PacketListener packetListener;

    public FancySitulaPlugin() {
        instance = this;
    }

    public static FancySitulaPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        packetListener = FancySitula.PACKET_LISTENER_FACTORY.createPacketListener(FS_ServerboundPacket.Type.CUSTOM_CLICK_ACTION);
        packetListener.addListener((event) -> {
            FS_ServerboundCustomClickActionPacket packet = (FS_ServerboundCustomClickActionPacket) event.packet();
            System.out.println("Received custom click action packet: " + packet.getId() + " with payload: " + packet.getPayload().orElse("No payload"));
        });
    }

    @Override
    public void onEnable() {
        IFancySitula.LOGGER.setCurrentLevel(LogLevel.DEBUG);

        getServer().getCommandMap().register("fancysitula", new FancySitulaCMD());

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    }

    public FS_PacketListener getPacketListener() {
        return packetListener;
    }
}
