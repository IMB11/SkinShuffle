package dev.imb11.skinshuffle.networking;

import dev.imb11.skinshuffle.api.data.SkinQueryResult;
import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.util.SkinShuffleClientPlayer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class ClientSkinHandling {
    private static boolean handshakeTakenPlace = false;

    private static boolean reconnectRequired = false;

    public static boolean isReconnectRequired() {
        return reconnectRequired;
    }

    public static void setReconnectRequired(boolean reconnectRequired) {
        ClientSkinHandling.reconnectRequired = reconnectRequired;
    }

    public static boolean isInstalledOnServer() {
        return handshakeTakenPlace;
    }

    public static void sendRefresh(SkinQueryResult result) {
        ClientPlayNetworking.send(new SkinRefreshPayload(result.toProperty()));
    }

    public static void init() {
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            SkinPresetManager.setApiPreset(null);
        });

        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            if (client.world == null) return;
            handshakeTakenPlace = false;
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            handshakeTakenPlace = false;
            setReconnectRequired(false);
            SkinPresetManager.setApiPreset(null);
        });

        ClientPlayNetworking.registerGlobalReceiver(HandshakePayload.PACKET_ID, (payload, context) -> {
            handshakeTakenPlace = true;
        });

        ClientPlayNetworking.registerGlobalReceiver(RefreshPlayerListEntryPayload.PACKET_ID, (payload, context) -> {
            int id = payload.entityID();
            MinecraftClient client = context.client();
            client.execute(() -> {
                ClientWorld world = client.world;
                if (world != null) {
                    Entity entity = world.getEntityById(id);
                    if (entity instanceof AbstractClientPlayerEntity player) {
                        ((SkinShuffleClientPlayer) player).skinShuffle$refreshPlayerListEntry();
                    }
                }
            });
        });
    }
}
