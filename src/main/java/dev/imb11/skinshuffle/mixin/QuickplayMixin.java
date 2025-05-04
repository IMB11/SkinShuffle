package dev.imb11.skinshuffle.mixin;

import dev.imb11.skinshuffle.MixinStatics;
import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.util.NetworkingUtil;
import dev.imb11.skinshuffle.util.ToastHelper;
import net.minecraft.client.QuickPlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(QuickPlay.class)
public class QuickplayMixin {
    @Inject(method = "startQuickPlay", at = @At("HEAD"), cancellable = false)
    private static void quickplaySupport(CallbackInfo ci) {
        if (!MixinStatics.APPLIED_SKIN_MANAGER_CONFIGURATION) {
            MixinStatics.APPLIED_SKIN_MANAGER_CONFIGURATION = true;

            SkinPresetManager.apply();

            if (!NetworkingUtil.isLoggedIn()) {
                ToastHelper.showOfflineModeToast();
            }
        }
    }
}
