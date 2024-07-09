package com.mineblock11.skinshuffle.mixin.compat;

import com.provismet.provihealth.util.Visibility;
import net.minecraft.entity.LivingEntity;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Visibility.class, remap = false)
@Pseudo
public class ProvisCompat {
    @Inject(method = "isVisible", at = @At("HEAD"), cancellable = true, remap = false)
    private static void $skinshuffle$skipDummies(LivingEntity living, CallbackInfoReturnable<Boolean> cir) {
        if(living instanceof DummyClientPlayerEntity) {
            cir.setReturnValue(false);
        }
    }
}
