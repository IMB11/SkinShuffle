package com.mineblock11.skinshuffle.mixin;

import com.mineblock11.skinshuffle.api.MojangSkinAPI;
import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("HEAD"))
    public void testCarousel(CallbackInfo ci) {
        MinecraftClient.getInstance().setScreen(new SkinCarouselScreen());
        MojangSkinAPI.setSkinTexture("https://s.namemc.com/i/2b931e86a910f916.png", MojangSkinAPI.SkinModelType.CLASSIC);
    }
}
