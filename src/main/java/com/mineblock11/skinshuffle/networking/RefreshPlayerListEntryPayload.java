package com.mineblock11.skinshuffle.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RefreshPlayerListEntryPayload(int entityID) implements CustomPayload {
    public static final CustomPayload.Id<RefreshPlayerListEntryPayload> PACKET_ID = new CustomPayload.Id<>(Identifier.of("skinshuffle", "refresh_player_list_entry"));
    public static final PacketCodec<RegistryByteBuf, RefreshPlayerListEntryPayload> PACKET_CODEC = PacketCodec.tuple(
        PacketCodecs.VAR_INT,
        RefreshPlayerListEntryPayload::entityID,
        RefreshPlayerListEntryPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
