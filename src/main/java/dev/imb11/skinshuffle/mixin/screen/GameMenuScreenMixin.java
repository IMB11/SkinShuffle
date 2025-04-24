package dev.imb11.skinshuffle.mixin.screen;

import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.gui.GeneratedScreens;
import dev.imb11.skinshuffle.client.gui.widgets.buttons.WarningIndicatorButton;
import dev.imb11.skinshuffle.networking.ClientSkinHandling;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

@Mixin(GameMenuScreen.class)
public abstract class GameMenuScreenMixin extends ScreenMixin {
    @Unique
    private ArrayList<ClickableWidget> openCarouselWidgets;
    @Unique
    private WarningIndicatorButton warningIndicator;

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (warningIndicator != null) {
            warningIndicator.visible = ClientSkinHandling.isReconnectRequired();
        }
    }

    @Override
    public void closeHook(CallbackInfo ci) {
        this.openCarouselWidgets = null;
    }

    @Override
    public void onDisplayedHook(CallbackInfo ci) {
        if (!SkinShuffleConfig.get().displayInTitleScreen && this.openCarouselWidgets != null) {
            for (ClickableWidget openCarouselWidget : this.openCarouselWidgets) {
                this.remove(openCarouselWidget);
            }
            this.openCarouselWidgets = null;
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addButton(CallbackInfo ci) {
        /*
            TODO: Maybe different types of buttons?
             - Small icon button
             - Bedrock-style skin preview
         */

        this.openCarouselWidgets = GeneratedScreens.createCarouselWidgets((Screen) (Object) this);

        for (ClickableWidget carouselWidget : this.openCarouselWidgets) {
            this.addDrawableChild(carouselWidget);
            if (carouselWidget instanceof WarningIndicatorButton warningIndicatorButton) {
                this.warningIndicator = warningIndicatorButton;
            }
        }
    }
}
