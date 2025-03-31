

package dev.imb11.skinshuffle.mixin.accessor;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(YggdrasilUserApiService.class)
public interface YggdrasilUserApiServiceAccessor {
    @Accessor("minecraftClient")
    MinecraftClient getMinecraftClient();
}
