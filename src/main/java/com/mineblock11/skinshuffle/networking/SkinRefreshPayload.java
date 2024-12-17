package com.mineblock11.skinshuffle.networking;

import com.mojang.authlib.properties.Property;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SkinRefreshPayload(Property textureProperty) implements CustomPayload {
    public static final CustomPayload.Id<SkinRefreshPayload> PACKET_ID = new CustomPayload.Id<>(Identifier.of("skinshuffle", "skin_refresh"));
    public static final PacketCodec<RegistryByteBuf, SkinRefreshPayload> PACKET_CODEC = PacketCodec.tuple(
        PacketCodec.of(
                (value, buf) -> {
                    buf.writeBoolean(value.hasSignature());
                    buf.writeString(value.name());
                    buf.writeString(value.value());
                    if(value.hasSignature()) {
                        buf.writeString(value.signature());
                    }
                },
                (buf) -> {
                    if(buf.readBoolean()) {
                        return new Property(buf.readString(), buf.readString(), buf.readString());
                    }
                    return new Property(buf.readString(), buf.readString(), null);
                }
        ),
        SkinRefreshPayload::textureProperty,
        SkinRefreshPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}