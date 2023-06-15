package com.mineblock11.skinshuffle.networking;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.util.ToastHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;

import java.util.concurrent.*;

public class ClientSkinHandling {
    private static Future<?> cooldownExecutor;
    private static final ExecutorService executorService =
            new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>());

    /**
     * Handles the "reset_cooldown" packet which resets the skin cooldown.
     */
    private static void resetCooldown(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        SkinPresetManager.setCooldown(true);

        if(cooldownExecutor != null) {
            cooldownExecutor.cancel(true);
        }

        cooldownExecutor = executorService.submit(() -> {
            try {
                Thread.sleep(30 * 1000);
                SkinPresetManager.setCooldown(false);
            } catch (InterruptedException ignored) {}
        });
    }

    private static boolean handshakeTakenPlace = false;

    public static boolean isInstalledOnServer() {
        return handshakeTakenPlace;
    }

    public static void init() {
        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            handshakeTakenPlace = false;
            CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> {
                client.execute(() -> {
                    if(!handshakeTakenPlace) {
                        ToastHelper.showHandshakeInitial(client);
                    }
                });
            });
            ClientPlayNetworking.registerReceiver(SkinShuffle.id("handshake"), (client1, handler1, buf, responseSender) -> {
                ClientPlayNetworking.unregisterReceiver(SkinShuffle.id("handshake"));
                handshakeTakenPlace = true;
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SkinShuffle.id("reset_cooldown"), ClientSkinHandling::resetCooldown);
    }
}
