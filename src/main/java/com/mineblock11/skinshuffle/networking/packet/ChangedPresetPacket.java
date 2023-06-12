package com.mineblock11.skinshuffle.networking.packet;

import com.mineblock11.skinshuffle.SkinShuffle;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

/**
 * This packet is used to update the client's skin for all connected players.
 */
public class ChangedPresetPacket implements FabricPacket {
    public static final PacketType<ChangedPresetPacket> PACKET_TYPE = PacketType.create(SkinShuffle.id("changed_preset"), ChangedPresetPacket::new);
    private byte[] skinTexture;
    private final String textureHash;
    private final String model;

    public ChangedPresetPacket(byte[] skinTexture, String textureHash, String model) {
        this.skinTexture = skinTexture;
        this.textureHash = textureHash;
        this.model = model;
    }

    public ChangedPresetPacket(PacketByteBuf buf) {
        this(getBytesOrNull(buf), buf.readString(), buf.readString());
    }

    private static @Nullable byte[] getBytesOrNull(PacketByteBuf buf) {
        boolean hasTextureFile = buf.readBoolean();
        return hasTextureFile ? buf.readByteArray() : null;
    }

    @Override
    public void write(PacketByteBuf buf) {
        buf.writeBoolean(this.skinTexture != null);
        if(this.skinTexture != null) buf.writeByteArray(this.skinTexture);
        buf.writeString(this.textureHash);
        buf.writeString(this.model);
    }

    @Override
    public PacketType<?> getType() {
        return PACKET_TYPE;
    }

    /**
     * Get the bytes of the skin texture.
     */
    public byte[] getSkinTexture() {
        return skinTexture;
    }

    /**
     * Get the skin model type.
     */
    public String getModel() {
        return model;
    }
}
