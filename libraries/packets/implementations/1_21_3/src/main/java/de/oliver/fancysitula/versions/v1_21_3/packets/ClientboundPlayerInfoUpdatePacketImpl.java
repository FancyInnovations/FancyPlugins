package de.oliver.fancysitula.versions.v1_21_3.packets;

import de.oliver.fancysitula.api.entities.FS_RealPlayer;
import de.oliver.fancysitula.api.packets.FS_ClientboundPlayerInfoUpdatePacket;
import de.oliver.fancysitula.versions.v1_21_3.utils.GameProfileImpl;
import de.oliver.fancysitula.versions.v1_21_3.utils.VanillaPlayerAdapter;
import io.papermc.paper.adventure.PaperAdventure;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ClientboundPlayerInfoUpdatePacketImpl extends FS_ClientboundPlayerInfoUpdatePacket {

    public ClientboundPlayerInfoUpdatePacketImpl(EnumSet<Action> actions, List<Entry> entries) {
        super(actions, entries);
    }

    @Override
    public Object createPacket() {
        EnumSet<ClientboundPlayerInfoUpdatePacket.Action> vanillaActions = EnumSet.noneOf(ClientboundPlayerInfoUpdatePacket.Action.class);
        for (FS_ClientboundPlayerInfoUpdatePacket.Action action : actions) {
            vanillaActions.add(ClientboundPlayerInfoUpdatePacket.Action.valueOf(action.name()));
        }

        List<ClientboundPlayerInfoUpdatePacket.Entry> entries = new ArrayList<>();
        for (Entry entry : this.entries) {
            entries.add(new ClientboundPlayerInfoUpdatePacket.Entry(
                    entry.uuid(),
                    GameProfileImpl.asVanilla(entry.profile()),
                    entry.listed(),
                    entry.latency(),
                    GameType.byId(entry.gameMode().getId()),
                    PaperAdventure.asVanilla(entry.displayName()),
                    -1,
                    null // ChatSession not needed for NPCs
            ));
        }

        return new ClientboundPlayerInfoUpdatePacket(vanillaActions, entries);
    }

    @Override
    public void sendPacketTo(FS_RealPlayer player) {
        ClientboundPlayerInfoUpdatePacket packet = (ClientboundPlayerInfoUpdatePacket) createPacket();

        ServerPlayer vanillaPlayer = VanillaPlayerAdapter.asVanilla(player.getBukkitPlayer());
        vanillaPlayer.connection.send(packet);
    }
}
