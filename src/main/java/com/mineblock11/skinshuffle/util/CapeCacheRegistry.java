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

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.cape.provider.CapeProvider;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CapeCacheRegistry {
    public static final BiHashMap<String, CapeProvider, @Nullable Identifier> CAPE_CACHE = new BiHashMap<>();
    public static boolean doesPlayerHaveCape(String username, CapeProvider capeProvider, @Nullable String usernameToStoreAs) {
        if(usernameToStoreAs != null) {
            if(CAPE_CACHE.containsKeys(usernameToStoreAs, capeProvider)) {
                Identifier cape = CAPE_CACHE.get(usernameToStoreAs, capeProvider);
                return cape != null;
            }
            CAPE_CACHE.put(usernameToStoreAs, capeProvider, null);
            getPlayerCape(username, capeProvider, usernameToStoreAs);
        } else {
            if(CAPE_CACHE.containsKeys(username, capeProvider)) {
                Identifier cape = CAPE_CACHE.get(username, capeProvider);
                return cape != null;
            }
            CAPE_CACHE.put(username, capeProvider, null);
            getPlayerCape(username, capeProvider, usernameToStoreAs);
        }

        return false;
    }

    public static @Nullable Identifier getCapeTexture(String username, CapeProvider capeProvider, @Nullable String usernameToStoreAs) {
        if(usernameToStoreAs != null) {
            return CAPE_CACHE.get(usernameToStoreAs, capeProvider);
        }
        return CAPE_CACHE.get(username, capeProvider);
    }

    public static void clearCache() {
        CAPE_CACHE.clear();
    }

    private static void getPlayerCape(String username, CapeProvider capeProvider, @Nullable String usernameToStoreAs) {
        new Thread(() -> {
            Identifier capeID = SkinShuffle.id(toIdValidUsername(username) + "/" + capeProvider.getProviderID());

            try {
                if(MinecraftClient.getInstance().getTextureManager().getOrDefault(capeID, null) != null) {
                    if(usernameToStoreAs != null) CAPE_CACHE.put(usernameToStoreAs, capeProvider, capeID);
                    else CAPE_CACHE.put(username, capeProvider, capeID);
                    return;
                }
            } catch (Exception ignored) {}

            byte[] data = capeProvider.getCapeTexture(username);
            if (data != null) {

                    MinecraftClient.getInstance().execute(() -> {
                        try {
                        NativeImageBackedTexture imageBackedTexture = new NativeImageBackedTexture(NativeImage.read(data));
                        MinecraftClient.getInstance().getTextureManager().registerTexture(capeID, imageBackedTexture);
                        if(usernameToStoreAs != null) CAPE_CACHE.put(usernameToStoreAs, capeProvider, capeID);
                        else CAPE_CACHE.put(username, capeProvider, capeID);
                    } catch (IOException e) {
                        SkinShuffle.LOGGER.info("Failed to load cape texture for uuid: " + username);
                        SkinShuffle.LOGGER.error(e.toString());
                    }
                    });

            }
        }).start();
    }

    public static void applyFromPreset(SkinPreset chosenPreset, String username) {
        CapeProvider provider = chosenPreset.getCapeProvider();
        getPlayerCape(username, provider, null);
    }

    private static String toIdValidUsername(String username) {
        return String.valueOf(Math.abs(username.hashCode()));
    }
}
