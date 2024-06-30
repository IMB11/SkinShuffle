/*? if >=1.20.5 {*/
package com.mineblock11.skinshuffle.networking;

import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record HandshakePayload() implements CustomPayload {
    public static final HandshakePayload INSTANCE = new HandshakePayload();
    public static final CustomPayload.Id<HandshakePayload> PACKET_ID = new CustomPayload.Id<>(Identifier.of("skinshuffle", "handshake"));

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
/*?}*/
