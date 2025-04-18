package dev.imb11.skinshuffle.mixin.accessor;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftClient.class)
public interface MinecraftClientAuthAccessor {
    @Accessor("accessToken")
    String getAccessToken();
}
