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

package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.widgets.AddPresetWidget;
import com.mineblock11.skinshuffle.client.gui.widgets.CarouselMoveButton;
import com.mineblock11.skinshuffle.client.gui.widgets.SkinPresetWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.ArrayList;
import java.util.Collections;

public class SkinCarouselScreen extends SpruceScreen {
    private final Screen parent;

    public SkinCarouselScreen(Screen parent) {
        super(Text.translatable("skinshuffle.carousel.title"));
        this.parent = parent;
    }

    public CarouselMoveButton leftMoveButton;
    public CarouselMoveButton rightMoveButton;
    private int cardIndex = 0;
    private double lastCardIndex = 0;
    private double lastCardSwitchTime = 0;
    public ArrayList<SpruceWidget> carouselWidgets = new ArrayList<>();
    @Override
    protected void init() {
        super.init();
        carouselWidgets.clear();

        leftMoveButton = new CarouselMoveButton(Position.of((getCardWidth() / 2), (this.height / 2) - 8), false);
        rightMoveButton = new CarouselMoveButton(Position.of(this.width - (getCardWidth() / 2), (this.height / 2) - 8), true);

        leftMoveButton.setCallback(() -> {
            var cardIndex = (this.cardIndex - 1 + (this.carouselWidgets.size())) % (this.carouselWidgets.size());
            if (cardIndex < 0) {
                cardIndex = this.carouselWidgets.size() - 1;
            }
            setCardIndex(cardIndex);
        });
        rightMoveButton.setCallback(() -> {
            var cardIndex = (this.cardIndex + 1) % (this.carouselWidgets.size());
            if (cardIndex < 0) {
                cardIndex = this.carouselWidgets.size();
            }
            setCardIndex(cardIndex);
        });

        var addPresetWidget = new AddPresetWidget(this,
                Position.of(0, 0), getCardWidth(), getCardHeight());
        addPresetWidget.setCallback(() -> {
            SkinPreset unnamed = SkinPreset.generateDefaultPreset();
            unnamed.setName("Unnamed Preset");
            SkinPresetManager.addPreset(unnamed);
            this.addDrawableChild(this.loadPreset(unnamed));
            Collections.swap(this.carouselWidgets, this.carouselWidgets.size() - 1, this.carouselWidgets.size() - 2);
        });

        var loadedPresets = SkinPresetManager.getLoadedPresets();

        loadedPresets.forEach(this::loadPreset);

        this.cardIndex = loadedPresets.indexOf(SkinPresetManager.getChosenPreset());
        this.lastCardIndex = this.cardIndex;

        this.addDrawableChild(leftMoveButton);
        this.addDrawableChild(rightMoveButton);
        this.addDrawableChild(addPresetWidget);

        for (SpruceWidget presetCards : this.carouselWidgets) {
            this.addDrawableChild(presetCards);
        }

        this.carouselWidgets.add(addPresetWidget);

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this.width / 2 - 128 - 5, this.height - 23), 128, 20, ScreenTexts.CANCEL, button -> {
            this.close();
        }));

        this.addDrawableChild(new SpruceIconButtonWidget(Position.of(2, 2), 20, 20, Text.empty(), (btn) -> this.client.setScreen(SkinShuffleConfigScreen.get(this))) {
            @Override
            protected int renderIcon(DrawContext graphics, int mouseX, int mouseY, float delta) {
                graphics.drawTexture(SkinShuffle.id("textures/gui/config-button-icon.png"), this.getX() + this.getWidth() / 2 - (14 / 2), this.getY() + this.getHeight() / 2 - (14 / 2), 14, 14, 0, 0, 15, 15, 15, 15);
                return 14;
            }
        });

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this.width / 2 + 5, this.height - 23), 128, 20, Text.translatable("skinshuffle.carousel.save_button"), button -> {
            SpruceWidget chosenPresetWidget = this.carouselWidgets.get(cardIndex);

            if(chosenPresetWidget instanceof AddPresetWidget) {
                this.close();
                return;
            }

            assert chosenPresetWidget instanceof SkinPresetWidget;
            SkinPresetWidget presetWidget = (SkinPresetWidget) chosenPresetWidget;

            SkinPresetManager.setChosenPreset(presetWidget.getPreset());

            this.close();
        }));

        this.leftMoveButton.setActive(this.carouselWidgets.size() != 1);
        this.rightMoveButton.setActive(this.carouselWidgets.size() != 1);
    }

    @Override
    public void close() {
        this.client.setScreen(parent);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        var cardAreaWidth = getCardWidth() + getCardGap();

        // BG stuff
        this.renderBackground(graphics);

        graphics.fill(0, this.textRenderer.fontHeight * 3, this.width, this.height - (this.textRenderer.fontHeight * 3), 0x7F000000);
        graphics.fillGradient(0, (int) (this.textRenderer.fontHeight * 2.75), this.width, this.textRenderer.fontHeight * 3, 0x00000000, 0x7F000000);
        graphics.fillGradient(0, (int) (this.height - (this.textRenderer.fontHeight * 3)), this.width, (int) (this.height - (this.textRenderer.fontHeight * 2.75)), 0x7F000000, 0x00000000);
        ScissorManager.pushScaleFactor(this.scaleFactor);

        // Carousel Widgets
        double deltaIndex = getDeltaCardIndex();
        int xOffset = (int) ((-deltaIndex + 1) * cardAreaWidth);
        int currentX = this.width / 2 - cardAreaWidth - getCardWidth() / 2;

        for (SpruceWidget widget : this.carouselWidgets) {
//            graphics.drawTextWithShadow(this.textRenderer, String.valueOf(loadedPresets.indexOf(loadedPreset)), currentX + xOffset, this.height/2 - this.textRenderer.fontHeight /2 , 0xFFFFFFFF);
            if(widget instanceof SkinPresetWidget loadedPreset) {
                loadedPreset.overridePosition(Position.of(currentX + xOffset, (this.height / 2) - (getCardHeight() / 2)));
                loadedPreset.setScaleFactor(this.scaleFactor);
            } else if (widget instanceof AddPresetWidget addPresetWidget) {
                addPresetWidget.overridePosition(Position.of(currentX + xOffset, (this.height / 2) - (getCardHeight() / 2)));
            }

            widget.setActive(cardIndex == this.carouselWidgets.indexOf(widget));

            currentX += cardAreaWidth;
        }

        this.renderWidgets(graphics, mouseX, mouseY, delta);
        this.renderTitle(graphics, mouseX, mouseY, delta);
        Tooltip.renderAll(graphics);
        ScissorManager.popScaleFactor();

    }

    @Override
    public void renderTitle(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawCenteredTextWithShadow(this.textRenderer, this.getTitle().asOrderedText(), this.width / 2, this.textRenderer.fontHeight, 0xFFFFFFFF);
    }

    private int getCardWidth() {
        return this.width / 4;
    }

    private int getCardHeight() {
        return (int) (this.height / 1.5);
    }

    private int getCardGap() {
        return (int) (10 * this.scaleFactor);
    }

    private double getDeltaCardIndex() {
        var deltaTime = (GlfwUtil.getTime() - lastCardSwitchTime) * 5;
        deltaTime = MathHelper.clamp(deltaTime, 0, 1);
        deltaTime = Math.sin(deltaTime * Math.PI / 2);
        return MathHelper.lerp(deltaTime, lastCardIndex, cardIndex);
    }

    public SkinPresetWidget loadPreset(SkinPreset preset) {
        var widget = new SkinPresetWidget(this, getCardWidth(), getCardHeight(), preset);
        this.carouselWidgets.add(widget);
        return widget;
    }

    public void setCardIndex(int index) {
        lastCardIndex = getDeltaCardIndex();
        lastCardSwitchTime = GlfwUtil.getTime();
        cardIndex = index;
    }

    public double getLastCardSwitchTime() {
        return lastCardSwitchTime;
    }

    public void refresh() {
        this.children().clear();
        this.init();
    }
}
