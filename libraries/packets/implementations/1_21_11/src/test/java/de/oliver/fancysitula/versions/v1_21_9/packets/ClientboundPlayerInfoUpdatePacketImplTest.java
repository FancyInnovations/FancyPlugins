package de.oliver.fancysitula.versions.v1_21_9.packets;

import de.oliver.fancysitula.api.packets.FS_ClientboundPlayerInfoUpdatePacket;
import de.oliver.fancysitula.api.utils.FS_GameProfile;
import de.oliver.fancysitula.api.utils.FS_GameType;
import de.oliver.fancysitula.versions.v1_21_11.packets.ClientboundPlayerInfoUpdatePacketImpl;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

class ClientboundPlayerInfoUpdatePacketImplTest {

    @Test
    void createPacket() {
        // Setup packet
        EnumSet<FS_ClientboundPlayerInfoUpdatePacket.Action> actions = EnumSet.noneOf(FS_ClientboundPlayerInfoUpdatePacket.Action.class);
        actions.add(FS_ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        actions.add(FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);
        actions.add(FS_ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED);

        FS_GameProfile gameProfile = new FS_GameProfile(UUID.randomUUID(), "Test name");
        boolean listed = true;
        int latency = 42;
        FS_GameType gameMode = FS_GameType.SURVIVAL;
        Component displayName = Component.text("Test displayname");

        List<FS_ClientboundPlayerInfoUpdatePacket.Entry> entries = new ArrayList<>();
        entries.add(new FS_ClientboundPlayerInfoUpdatePacket.Entry(
                gameProfile.getUUID(),
                gameProfile,
                listed,
                latency,
                gameMode,
                displayName
        ));

        ClientboundPlayerInfoUpdatePacketImpl packet = new ClientboundPlayerInfoUpdatePacketImpl(actions, entries);

        ClientboundPlayerInfoUpdatePacket createdPacket = (ClientboundPlayerInfoUpdatePacket) packet.createPacket();

        assert createdPacket.entries().size() == 1;
        assert createdPacket.actions().size() == 3;

        // check entry
        ClientboundPlayerInfoUpdatePacket.Entry entry = createdPacket.entries().getFirst();
        assert entry.profile().id().equals(gameProfile.getUUID());
        assert entry.profile().name().equals(gameProfile.getName());
        assert entry.listed() == listed;
        assert entry.latency() == latency;
        assert entry.gameMode().getId() == gameMode.getId();

        // check actions
        assert createdPacket.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER);
        assert createdPacket.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_DISPLAY_NAME);
        assert createdPacket.actions().contains(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED);
    }
}