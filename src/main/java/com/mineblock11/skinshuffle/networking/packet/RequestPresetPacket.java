package com.mineblock11.skinshuffle.networking.packet;

import com.mineblock11.skinshuffle.SkinShuffle;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public class RequestPresetPacket implements FabricPacket {
    public static final PacketType<RequestPresetPacket> PACKET_TYPE = PacketType.create(SkinShuffle.id("request_preset"), )
    @Override
    public void write(PacketByteBuf buf) {

    }

    @Override
    public PacketType<?> getType() {
        return null;
    }
}
