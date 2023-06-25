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
import com.mineblock11.skinshuffle.api.MojangSkinAPI;
import com.mineblock11.skinshuffle.util.SkinShufflePlayer;
import com.mojang.authlib.properties.Property;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ServerSkinHandling {
    /**
     * Player UUIDs that currently have a cooldown.
     */
    private static final ArrayList<String> LOCKED_PLAYERS = new ArrayList<>();

    /**
     * Player UUIDs that are currently in the refresh process.
     */
    private static final ArrayList<String> CURRENTLY_REFRESHING = new ArrayList<>();

    /**
     * Player UUIDs that are currently waiting for the cooldown to expire before starting the refresh process.
     */
    private static final ArrayList<String> PLAYERS_WITH_SCHEDULERS = new ArrayList<>();


    private static void handlePresetChange(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        String uuidAsString = player.getUuidAsString();
        if(CURRENTLY_REFRESHING.contains(uuidAsString)) return;
        if(LOCKED_PLAYERS.contains(uuidAsString)) {
            if(!PLAYERS_WITH_SCHEDULERS.contains(uuidAsString)) {
                CompletableFuture.delayedExecutor(30, TimeUnit.SECONDS, server).execute(() -> {
                    LOCKED_PLAYERS.remove(uuidAsString);
                    PLAYERS_WITH_SCHEDULERS.remove(uuidAsString);
                    if(player.isDisconnected()) return;
                    handlePresetChange(server, player, handler, buf, responseSender);
                });
            }

            if(!PLAYERS_WITH_SCHEDULERS.contains(uuidAsString)) PLAYERS_WITH_SCHEDULERS.add(uuidAsString);

            return;
        }

        LOCKED_PLAYERS.add(uuidAsString);
        CURRENTLY_REFRESHING.add(uuidAsString);

        responseSender.sendPacket(SkinShuffle.id("reset_cooldown"), PacketByteBufs.empty());

        var result = MojangSkinAPI.getPlayerSkinTexture(uuidAsString);
        SkinShuffle.LOGGER.info("recieved packet");
        Property skinData = result.toProperty();

        server.execute(() -> {
            var properties = player.getGameProfile().getProperties();
            try {
                properties.removeAll("textures");
            } catch (Exception ignored) {
            }

            try {
                properties.put("textures", skinData);
            } catch (Error e) {
                SkinShuffle.LOGGER.error("Failed to refresh GameProfile for " + player.getName(), e.getMessage());
            }

            SkinShufflePlayer skinShufflePlayer = (SkinShufflePlayer) player;
            skinShufflePlayer.skinShuffle$refreshSkin();

            CURRENTLY_REFRESHING.remove(uuidAsString);
        });
    }

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            ServerSkinHandling.PLAYERS_WITH_SCHEDULERS.clear();
            ServerSkinHandling.LOCKED_PLAYERS.clear();
            ServerSkinHandling.CURRENTLY_REFRESHING.clear();
        });

        // Send handshake packet to client.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayNetworking.send(handler.getPlayer(), SkinShuffle.id("handshake"), PacketByteBufs.empty());
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->  {
            ServerSkinHandling.PLAYERS_WITH_SCHEDULERS.removeIf(uuid -> uuid.equals(handler.getPlayer().getUuidAsString()));
            ServerSkinHandling.LOCKED_PLAYERS.removeIf(uuid -> uuid.equals(handler.getPlayer().getUuidAsString()));
            ServerSkinHandling.CURRENTLY_REFRESHING.removeIf(uuid -> uuid.equals(handler.getPlayer().getUuidAsString()));
        });

        ServerPlayNetworking.registerGlobalReceiver(SkinShuffle.id("preset_changed"), ServerSkinHandling::handlePresetChange);
    }
}
