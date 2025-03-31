package dev.imb11.skinshuffle.mixin.screen;

import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Screen.class)
public abstract class ScreenMixin {

    @Shadow
    protected abstract void remove(Element child);

    @Shadow
    protected abstract <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement);

    @Inject(method = "close", at = @At("HEAD"))
    protected void closeHook(CallbackInfo ci) {

    }

    @Inject(method = "onDisplayed", at = @At("HEAD"))
    protected void onDisplayedHook(CallbackInfo ci) {

    }
}
