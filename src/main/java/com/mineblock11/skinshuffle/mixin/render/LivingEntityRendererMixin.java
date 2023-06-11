package com.mineblock11.skinshuffle.mixin.render;

import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {
    @Inject(method = "hasLabel(Lnet/minecraft/entity/LivingEntity;)Z", cancellable = true, at = @At("HEAD"))
    private <T extends LivingEntity> void fakeHasLabel(T livingEntity, CallbackInfoReturnable<Boolean> cir) {
        if(livingEntity instanceof DummyClientPlayerEntity) {
            cir.setReturnValue(false);
        }
    }
}
