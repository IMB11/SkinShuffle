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

package com.mineblock11.skinshuffle.util;

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAccessor;
import com.mineblock11.skinshuffle.networking.ClientSkinHandling;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.QuickPlay;
import net.minecraft.client.gui.screen.MessageScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.realms.gui.screen.RealmsMainScreen;
import net.minecraft.text.Text;
import net.minecraft.util.WorldSavePath;

import java.util.Objects;

public class NetworkingUtil {
    public static boolean isLoggedIn() {
        MinecraftClient client = MinecraftClient.getInstance();
        return ((MinecraftClientAccessor) client).getUserApiService() instanceof YggdrasilUserApiService;
    }

    public static void handleReconnect(MinecraftClient client) {
        ClientSkinHandling.setReconnectRequired(false);
        SkinPresetManager.setApiPreset(null);

        boolean isSingleplayer = client.isInSingleplayer();
        String folderName, serverAddress;

        if (isSingleplayer) {
            serverAddress = null;
            folderName = client.server.getSavePath(WorldSavePath.ROOT).toFile().getName();
            client.world.disconnect();
            client.disconnect(new MessageScreen(Text.translatable("skinshuffle.reconnect.rejoining")));
        } else {
            folderName = null;
            if(!client.getNetworkHandler().getConnection().isLocal()) {
                serverAddress = client.getNetworkHandler().getServerInfo().address;
            } else {
                serverAddress = null;
            }
            client.world.disconnect();
            client.disconnect(new MessageScreen(Text.translatable("skinshuffle.reconnect.reconnecting")));
        }


        if(client.isInSingleplayer()) {
            client.executeTask(() -> {
                try {
                    Thread.sleep(250);
                    client.execute(() -> QuickPlay.startSingleplayer(client, folderName));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        } else {
            client.executeTask(() -> {
                try {
                    Thread.sleep(250);
                    client.execute(() -> QuickPlay.startMultiplayer(client, serverAddress));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
