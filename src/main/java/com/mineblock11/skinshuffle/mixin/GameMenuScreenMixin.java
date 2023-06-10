package com.mineblock11.skinshuffle.mixin;

import com.mineblock11.skinshuffle.client.gui.widgets.OpenCarouselWidget;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", cancellable = false, at = @At("TAIL"))
    private void addButton(CallbackInfo ci) {
        OpenCarouselWidget.safelyCreateWidget(this, this::addDrawableChild);
    }
}
