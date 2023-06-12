package com.mineblock11.skinshuffle.mixin.screen;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.widgets.OpenCarouselWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin extends Screen {
    @Shadow @Final private boolean doBackgroundFade;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Unique
    private boolean appliedConfiguration = false;

    @Inject(method = "init", at = @At("HEAD"))
    public void loadConfig(CallbackInfo ci) {
        // Config must be refreshed here as it requires resource manager.
        SkinShuffleConfig.loadPresets();
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void refreshConfig(CallbackInfo ci) {
        if(!appliedConfiguration && this.doBackgroundFade) {
            appliedConfiguration = true;
            SkinShuffleConfig.apply();
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addButton(CallbackInfo ci) {
        /*
            TODO: Maybe different types of buttons?
             - Small icon button
             - Bedrock-style skin preview
         */

        OpenCarouselWidget.safelyCreateWidget(this, this::addDrawableChild);
    }
}
