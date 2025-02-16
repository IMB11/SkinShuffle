package dev.imb11.skinshuffle.mixin;

import dev.imb11.skinshuffle.client.SkinShuffleClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void render(boolean tick, CallbackInfo ci) {
        SkinShuffleClient.TOTAL_TICK_DELTA += MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
    }
}
