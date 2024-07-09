package com.mineblock11.skinshuffle.mixin.compat;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.tslat.tes.api.util.TESUtil;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(value = TESUtil.class, remap = false)
public interface TESCompat {
    @Inject(method = "shouldTESHandleEntity", at = @At("HEAD"), cancellable = true, remap = false)
    private static void $skinshuffle$skipDummies(LivingEntity entity, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if(entity instanceof DummyClientPlayerEntity) {
            cir.setReturnValue(false);
        }
    }
}
