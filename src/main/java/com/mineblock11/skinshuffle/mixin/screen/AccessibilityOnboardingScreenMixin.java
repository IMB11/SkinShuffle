package com.mineblock11.skinshuffle.mixin.screen;

import com.mineblock11.skinshuffle.MixinStatics;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.util.NetworkingUtil;
import com.mineblock11.skinshuffle.util.ToastHelper;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AccessibilityOnboardingScreen.class)
public class AccessibilityOnboardingScreenMixin {
    @Inject(method = "render", at = @At("HEAD"))
    public void refreshConfig(CallbackInfo ci) {
        if (!MixinStatics.APPLIED_SKIN_MANAGER_CONFIGURATION) {
            MixinStatics.APPLIED_SKIN_MANAGER_CONFIGURATION = true;
            SkinPresetManager.apply();

            if(!NetworkingUtil.isLoggedIn()) {
                ToastHelper.showOfflineModeToast();
            }
        }
    }
}
