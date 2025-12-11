package de.oliver.fancynpcs.v1_21_11;

import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Predicate;

public class FakeSynchronizer implements ServerEntity.Synchronizer {

    public static final FakeSynchronizer INSTANCE = new FakeSynchronizer();

    private FakeSynchronizer() {
    }

    @Override
    public void sendToTrackingPlayers(Packet<? super ClientGamePacketListener> packet) {

    }

    @Override
    public void sendToTrackingPlayersAndSelf(Packet<? super ClientGamePacketListener> packet) {

    }

    @Override
    public void sendToTrackingPlayersFiltered(Packet<? super ClientGamePacketListener> packet, Predicate<ServerPlayer> predicate) {

    }
}
