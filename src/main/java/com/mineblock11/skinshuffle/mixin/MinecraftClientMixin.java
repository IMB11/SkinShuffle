package com.mineblock11.skinshuffle.mixin;

import com.mineblock11.skinshuffle.client.SkinShuffleClient;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void render(boolean tick, CallbackInfo ci) {
        //? if >=1.21 {
        SkinShuffleClient.TOTAL_TICK_DELTA += MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);
        //?} else {
        /*SkinShuffleClient.TOTAL_TICK_DELTA += MinecraftClient.getInstance().getTickDelta();
        *///?}
    }
}
