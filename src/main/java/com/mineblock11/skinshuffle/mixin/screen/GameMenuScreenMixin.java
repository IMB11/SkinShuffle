/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle.mixin.screen;

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.GeneratedScreens;
import com.mineblock11.skinshuffle.client.gui.widgets.OpenCarouselButton;
import com.mineblock11.skinshuffle.client.gui.widgets.WarningIndicatorButton;
import com.mineblock11.skinshuffle.networking.ClientSkinHandling;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.text.Text;
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
    private WarningIndicatorButton warningIndicator;

    @Inject(method = "render", at = @At("HEAD"))
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if(warningIndicator != null) {
            warningIndicator.visible = ClientSkinHandling.isReconnectRequired();
        }
    }

    @Override
    public void closeHook(CallbackInfo ci) {
        if (this.openCarouselWidgets != null) {
            for (ClickableWidget openCarouselWidget : this.openCarouselWidgets) {
                if(openCarouselWidget instanceof  OpenCarouselButton button) {
                    button.disposed();
                }
            }
            this.openCarouselWidgets = null;
        }
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

        this.openCarouselWidgets = GeneratedScreens.createCarouselWidgets((Screen)(Object)this);

        for (ClickableWidget carouselWidget : this.openCarouselWidgets) {
            this.addDrawableChild(carouselWidget);
            if(carouselWidget instanceof OpenCarouselButton button) {
                new Thread(() -> {
                    SkinPresetManager.loadPresets();
                    button.setSelectedPreset(SkinPresetManager.getChosenPreset());
                }).start();
            }
            if(carouselWidget instanceof WarningIndicatorButton warningIndicatorButton) {
                this.warningIndicator = warningIndicatorButton;
            }
        }
    }
}
