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

package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.GeneratedScreens;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;

public class OpenCarouselButton extends ButtonWidget {
    private SkinPreset selectedPreset;
    private DummyClientPlayerEntity entity;
    private double currentTime = 0;

    public OpenCarouselButton(int x, int y, int width, int height) {
        super(x, y, width, height, Text.translatable("skinshuffle.button"), (btn) -> {
            var client = MinecraftClient.getInstance();
            client.setScreen(GeneratedScreens.getCarouselScreen(client.currentScreen));
        }, textSupplier -> Text.empty());

        currentTime = GlfwUtil.getTime();
    }

    public void disposed() {
        if (this.entity != null) {
            this.entity.kill();
        }
    }

    public void setSelectedPreset(SkinPreset preset) {
        this.selectedPreset = preset;
        this.entity = new DummyClientPlayerEntity(this.selectedPreset);
    }

    private float getEntityRotation() {
        return (float) ((GlfwUtil.getTime() - currentTime) * 35.0f);
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
        if (this.entity != null) {
            // Don't want to render the entity if the skin is still loading
            if (!selectedPreset.getSkin().isLoading()) {
                float followX = (float) (this.getX() + (this.getWidth() / 2)) - mouseX;
                float followY = (float) (this.getY() - 90) - mouseY;
                float rotation = 0;

                SkinShuffleConfig.SkinRenderStyle renderStyle = SkinShuffleConfig.get().widgetSkinRenderStyle;

                if (renderStyle.equals(SkinShuffleConfig.SkinRenderStyle.ROTATION)) {
                    followX = 0;
                    followY = 0;
                    rotation = getEntityRotation() * SkinShuffleConfig.get().rotationMultiplier;
                }

                GuiEntityRenderer.drawEntity(
                        context.getMatrices(), this.getX() + (this.getWidth() / 2), this.getY() - 12,
                        45, rotation, followX, followY, entity
                );
            } else {
                // Make sure to call getTexture anyway, otherwise the skin will never load
                selectedPreset.getSkin().getTexture();
            }
        }
    }
}
