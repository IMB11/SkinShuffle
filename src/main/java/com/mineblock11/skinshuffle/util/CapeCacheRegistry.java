package com.mineblock11.skinshuffle.util;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.cape.provider.CapeProviders;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

public class CapeCacheRegistry {
    public static final HashMap<String, @Nullable Identifier> CAPE_CACHE = new HashMap<>();
    public static boolean doesPlayerHaveCape(String uuid) {
        if(CAPE_CACHE.containsKey(uuid)) {
            Identifier cape = CAPE_CACHE.get(uuid);
            return cape != null;
        } else {
            CAPE_CACHE.put(uuid, null);
            if(!uuid.equals(MinecraftClient.getInstance().getSession().getUuid()))
                getPlayerCape(uuid);
            return false;
        }
    }

    public static @Nullable Identifier getCapeTexture(String uuid) {
        return CAPE_CACHE.getOrDefault(uuid, null);
    }

    public static void clearCache() {
        CAPE_CACHE.clear();
    }

    private static void getPlayerCape(String uuid) {
        new Thread(() -> {
            byte[] data = CapeProviders.MC_CAPES.getCapeTexture(uuid);
            if (data != null) {
                try {
                    NativeImageBackedTexture imageBackedTexture = new NativeImageBackedTexture(NativeImage.read(data));
                    Identifier capeID = SkinShuffle.id(uuid);
                    MinecraftClient.getInstance().execute(() -> {
                        MinecraftClient.getInstance().getTextureManager().registerTexture(capeID, imageBackedTexture);
                        CAPE_CACHE.put(uuid, capeID);
                    });
                } catch (IOException e) {
                    SkinShuffle.LOGGER.info("Failed to load cape texture for uuid: " + uuid);
                    SkinShuffle.LOGGER.error(e.toString());
                }
            }
        }).start();
    }

    public static void applyFromPreset(SkinPreset chosenPreset, String uuid) {
        new Thread(() -> {
            byte[] data = chosenPreset.getCapeProvider().getCapeTexture(uuid);
            if (data != null) {
                try {
                    NativeImageBackedTexture imageBackedTexture = new NativeImageBackedTexture(NativeImage.read(data));
                    Identifier capeID = SkinShuffle.id(uuid);
                    MinecraftClient.getInstance().execute(() -> {
                        MinecraftClient.getInstance().getTextureManager().registerTexture(capeID, imageBackedTexture);
                        CAPE_CACHE.put(uuid, capeID);
                    });
                } catch (IOException e) {
                    SkinShuffle.LOGGER.info("Failed to load cape texture for uuid: " + uuid);
                    SkinShuffle.LOGGER.error(e.toString());
                }
            }
        }).start();
    }
}
