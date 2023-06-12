package com.mineblock11.skinshuffle.networking;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.networking.packet.ChangedPresetPacket;
import com.mineblock11.skinshuffle.util.SkinCacheRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

import java.nio.file.Path;
import java.util.HashMap;

public class ClientSkinNetworking {
    public static final Path CACHED_TEXTURES_PATH = SkinShuffle.DATA_DIR.resolve("cache/");
    private final MinecraftClient client;

    public ClientSkinNetworking(MinecraftClient client) {
        this.client = client;

        ClientPlayNetworking.registerGlobalReceiver(ChangedPresetPacket.PACKET_TYPE, this::handlePlayerPresetChange);
    }

    private void handlePlayerPresetChange(ChangedPresetPacket changedPresetPacket, ClientPlayerEntity clientPlayerEntity, PacketSender packetSender) {
        byte[] texture = changedPresetPacket.getSkinTexture();
    }
}
