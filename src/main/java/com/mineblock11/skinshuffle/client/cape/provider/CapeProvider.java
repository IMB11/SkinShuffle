package com.mineblock11.skinshuffle.client.cape.provider;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import org.jetbrains.annotations.Nullable;

public interface CapeProvider {
    @Nullable byte[] getCapeTexture(String username);
    default @Nullable byte[] getClientCapeTexture() {
        return getCapeTexture(MinecraftClient.getInstance().getSession().getUsername());
    }

    enum CapeProviderType {
        OPTIFINE,
        MC_CAPES
    }
}
