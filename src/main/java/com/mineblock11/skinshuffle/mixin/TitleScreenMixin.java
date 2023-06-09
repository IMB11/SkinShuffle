package com.mineblock11.skinshuffle.mixin;

import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceTexturedButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("HEAD"))
    public void testCarousel(CallbackInfo ci) {
        this.addDrawableChild(new SpruceButtonWidget(Position.of(5, 5), 200, 20, Text.of("carousel"), (btn) -> MinecraftClient.getInstance().setScreen(new SkinCarouselScreen())));
//        MojangSkinAPI.setSkinTexture("https://s.namemc.com/i/2b931e86a910f916.png", MojangSkinAPI.SkinModelType.CLASSIC);
    }
}
