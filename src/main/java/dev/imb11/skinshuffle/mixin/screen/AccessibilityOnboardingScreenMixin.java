package dev.imb11.skinshuffle.mixin.screen;

import com.mojang.authlib.GameProfile;
import dev.imb11.skinshuffle.MixinStatics;
import dev.imb11.skinshuffle.api.MojangSkinAPI;
import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.util.NetworkingUtil;
import dev.imb11.skinshuffle.util.ToastHelper;
import net.minecraft.client.gui.screen.AccessibilityOnboardingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Mixin(AccessibilityOnboardingScreen.class)
public abstract class AccessibilityOnboardingScreenMixin extends Screen {
    protected AccessibilityOnboardingScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void refreshConfig(CallbackInfo ci) {
        if (!MixinStatics.APPLIED_SKIN_MANAGER_CONFIGURATION) {
            MixinStatics.APPLIED_SKIN_MANAGER_CONFIGURATION = true;
            try {
                assert client != null;
                var tex = MojangSkinAPI.getPlayerSkinTexture(String.valueOf(client.getGameProfile().getId()));
                var texProperty = tex.toProperty();
                var dummyProfile = new GameProfile(UUID.randomUUID(), "dummyname");
                dummyProfile.getProperties().put("textures", texProperty);
                //? if <1.21.4 {
                MixinStatics.INITIAL_SKIN_TEXTURES = client.getSkinProvider().fetchSkinTextures(dummyProfile).thenApply(Optional::of);
                 //?} else {
                /*MixinStatics.INITIAL_SKIN_TEXTURES = client.getSkinProvider().fetchSkinTextures(dummyProfile);
                *///?}
            } catch (Exception ignored) {
                MixinStatics.INITIAL_SKIN_TEXTURES = CompletableFuture.completedFuture(Optional.of(client.getSkinProvider().getSkinTextures(client.getGameProfile())));
            }
            SkinPresetManager.apply();

            if (!NetworkingUtil.isLoggedIn()) {
                ToastHelper.showOfflineModeToast();
            }
        }
    }
}
