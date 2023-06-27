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
import com.mineblock11.skinshuffle.client.gui.widgets.OpenCarouselWidget;
import com.mineblock11.skinshuffle.util.NetworkingUtil;
import com.mineblock11.skinshuffle.util.ToastHelper;
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
    private static boolean appliedConfiguration = false;
    @Shadow
    @Final
    private boolean doBackgroundFade;
    @Unique
    private OpenCarouselWidget openCarouselWidget;

    protected TitleScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "render", at = @At("HEAD"))
    public void refreshConfig(CallbackInfo ci) {
        if (!appliedConfiguration && this.doBackgroundFade) {
            appliedConfiguration = true;
            SkinPresetManager.apply();

            if(!NetworkingUtil.isLoggedIn()) {
                ToastHelper.showOfflineModeToast();
            }
        }
    }

    @Override
    public void close() {
        if (this.openCarouselWidget != null) {
            this.openCarouselWidget.disposed();
            this.openCarouselWidget = null;
        }
    }

    @Inject(method = "onDisplayed", at = @At("TAIL"), cancellable = false)
    public void updateVisibility(CallbackInfo ci) {
        if (!SkinShuffleConfig.get().displayInTitleScreen && this.openCarouselWidget != null) {
            this.children().remove(this.openCarouselWidget);
            this.openCarouselWidget = null;
        }
    }

    @Inject(method = "init", at = @At("TAIL"))
    public void addButton(CallbackInfo ci) {
        /*
            TODO: Maybe different types of buttons?
             - Small icon button
             - Bedrock-style skin preview
         */

        OpenCarouselWidget.safelyCreateWidget(this, openCarouselWidget -> {
            this.openCarouselWidget = openCarouselWidget;
            this.addDrawableChild(openCarouselWidget);

            new Thread(() -> {
                SkinPresetManager.loadPresets();
                openCarouselWidget.setSelectedPreset(SkinPresetManager.getChosenPreset());
            }).start();
        });
    }
}
