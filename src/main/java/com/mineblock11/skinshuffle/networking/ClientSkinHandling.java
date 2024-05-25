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
import com.mineblock11.skinshuffle.api.SkinQueryResult;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.util.SkinShuffleClientPlayer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;

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
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeProperty(result.toProperty());
        ClientPlayNetworking.send(SkinShuffle.id("refresh"), buf);
    }

    public static void init() {

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            SkinPresetManager.setApiPreset(null);
        });

        ClientPlayConnectionEvents.INIT.register((handler, client) -> {
            if(client.world == null) return;
            handshakeTakenPlace = false;
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            handshakeTakenPlace = false;
            setReconnectRequired(false);
            SkinPresetManager.setApiPreset(null);
        });

        ClientPlayNetworking.registerGlobalReceiver(SkinShuffle.id("handshake"), (client1, handler1, buf, responseSender) -> {
            handshakeTakenPlace = true;
        });

        ClientPlayNetworking.registerGlobalReceiver(SkinShuffle.id("refresh_player_list_entry"), (client, handler, buf, responseSender) -> {
            int id = buf.readVarInt();
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
