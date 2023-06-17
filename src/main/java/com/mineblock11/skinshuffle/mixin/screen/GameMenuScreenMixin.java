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
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
public class GameMenuScreenMixin extends Screen {
    @Unique
    public OpenCarouselWidget openCarouselWidget;

    protected GameMenuScreenMixin(Text title) {
        super(title);
    }

    @Override
    public void onDisplayed() {
        SkinPresetManager.loadPresets();

        if(!SkinShuffleConfig.get().displayInPauseMenu && this.openCarouselWidget != null) {
            this.children().remove(this.openCarouselWidget);
            this.openCarouselWidget = null;
        }
    }

    @Inject(method = "init", cancellable = false, at = @At("TAIL"))
    private void addButton(CallbackInfo ci) {
        if(SkinShuffleConfig.get().displayInPauseMenu) {
            OpenCarouselWidget.safelyCreateWidget(this, openCarouselWidget -> {
                this.openCarouselWidget = openCarouselWidget;
                this.addDrawableChild(openCarouselWidget);
            });
        }
    }
}
