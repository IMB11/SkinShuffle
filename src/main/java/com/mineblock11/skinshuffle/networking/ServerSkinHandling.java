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
import com.mineblock11.skinshuffle.util.SkinShufflePlayer;
import com.mojang.authlib.properties.Property;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerSkinHandling {
    private static void handleSkinRefresh(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        Property skinData = buf.readProperty();
        SkinShuffle.LOGGER.info("Recieved skin refresh packet from: " + player.getName().getString());

        server.execute(() -> {
            var properties = player.getGameProfile().getProperties();
            try {
                properties.removeAll("textures");
            } catch (Exception ignored) {
            }

            try {
                properties.put("textures", skinData);
            } catch (Error e) {
                SkinShuffle.LOGGER.error("Failed to refresh GameProfile for " + player.getName() + "\n" + e.getMessage());
            }

            SkinShufflePlayer skinShufflePlayer = (SkinShufflePlayer) player;
            skinShufflePlayer.skinShuffle$refreshSkin();
        });
    }

    public static void init() {
        // Send handshake packet to client.
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayNetworking.send(handler.getPlayer(), SkinShuffle.id("handshake"), PacketByteBufs.empty());
        });

        ServerPlayNetworking.registerGlobalReceiver(SkinShuffle.id("refresh"), ServerSkinHandling::handleSkinRefresh);
    }
}
