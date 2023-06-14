package com.mineblock11.skinshuffle.networking;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
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

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(SkinShuffle.id("reset_cooldown"), (client, handler, buf, responseSender) -> {
            SkinShuffleConfig.setCooldown(true);

            if(cooldownExecutor != null) {
                cooldownExecutor.cancel(true);
            }

            cooldownExecutor = executorService.submit(() -> {
                try {
                    Thread.sleep(30 * 1000);
                    SkinShuffleConfig.setCooldown(false);
                } catch (InterruptedException ignored) {}
            });
        });
    }
}
