/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

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
            if(client.world == null) return;
            handshakeTakenPlace = false;
            CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS).execute(() -> {
                client.execute(() -> {
                    if(!handshakeTakenPlace) {
                        ToastHelper.showHandshakeInitial(client);
                    }
                });
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(SkinShuffle.id("handshake"), (client1, handler1, buf, responseSender) -> {
            handshakeTakenPlace = true;
        });

        ClientPlayNetworking.registerGlobalReceiver(SkinShuffle.id("reset_cooldown"), ClientSkinHandling::resetCooldown);
    }
}
