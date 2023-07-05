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
import com.mineblock11.skinshuffle.client.gui.widgets.preset.AddCardWidget;
import com.mineblock11.skinshuffle.client.gui.widgets.preset.AbstractCardWidget;
import com.mineblock11.skinshuffle.client.gui.widgets.preset.PresetWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.client.skin.Skin;
import com.mineblock11.skinshuffle.networking.ClientSkinHandling;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.*;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Function;

public abstract class CarouselScreen extends SpruceScreen {
    public final Screen parent;
    public final Function<Screen, ? extends CarouselScreen> nextViewTypeFactory;
    public boolean hasEditedPreset = false;

    public CarouselScreen(Screen parent, Function<Screen, ? extends CarouselScreen> nextViewTypeFactory) {
        super(Text.translatable("skinshuffle.carousel.title"));
        this.parent = parent;
        this.nextViewTypeFactory = nextViewTypeFactory;
    }

    private double cardIndex = -1;
    private double lastCardIndex = 0;
    private double lastCardSwitchTime = 0;
    public ArrayList<AbstractCardWidget<?>> carouselWidgets = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        carouselWidgets.clear();

        var loadedPresets = SkinPresetManager.getLoadedPresets();
        for (var preset : loadedPresets) {
            var presetWidget = this.widgetFromPreset(preset);
            this.carouselWidgets.add(presetWidget);
        }

        var addPresetWidget = new AddCardWidget(this,
                Position.of(0, 0), getCardWidth(), getCardHeight());
        addPresetWidget.setCallback(() -> {
            SkinPreset unnamed = new SkinPreset(Skin.randomDefaultSkin());
            SkinPresetManager.addPreset(unnamed);
            var newWidget = this.loadPreset(unnamed);
            this.addDrawableChild(newWidget);
        });
        this.carouselWidgets.add(addPresetWidget);

        // We don't want to switch back to the selected preset when we return from a subscreen.
        if (this.cardIndex < 0) {
            this.cardIndex = loadedPresets.indexOf(SkinPresetManager.getChosenPreset());
        }
        this.lastCardIndex = this.cardIndex;

//        addPresetWidget.lastIndex = this.carouselWidgets.size() - 1; TODO TODO

//        new Thread(() -> {
            // TODO: requires more accurate equality check, maybe comparing the texture directly somehow?
//            for (var widget : carouselWidgets) {
//                if (widget instanceof PresetWidget skinWidget) {
//                    var preset = skinWidget.getPreset();
//
//                    if (preset.getSkin().equals(apiPreset.getSkin())) {
//                        SkinPresetManager.setApiPreset(preset);
//                        return;
//                    }
//                }
//            }
//
//            if (SkinPresetManager.getLoadedPresets().size() == 0) {
//                var apiPreset = SkinPreset.generateDefaultPreset();
//
//                SkinPresetManager.addPreset(apiPreset);
//                SkinPresetManager.setApiPreset(apiPreset);
//                var apiPresetWidget = this.loadPreset(apiPreset);
//                this.addDrawableChild(apiPresetWidget);
//            }
//        }).start();

        for (SpruceWidget presetCards : this.carouselWidgets) {
            this.addDrawableChild(presetCards);
        }

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this.width / 2 - 128 - 5, this.height - 23), 128, 20, ScreenTexts.DONE, button -> {
            this.close();
        }));

        this.addDrawableChild(new SpruceIconButtonWidget(Position.of(2, 2), 20, 20, Text.empty(), (btn) -> this.client.setScreenAndRender(GeneratedScreens.getConfigScreen(this))) {
            @Override
            protected int renderIcon(DrawContext graphics, int mouseX, int mouseY, float delta) {
                graphics.drawTexture(SkinShuffle.id("textures/gui/config-button-icon.png"), this.getX() + this.getWidth() / 2 - (14 / 2), this.getY() + this.getHeight() / 2 - (14 / 2), 14, 14, 0, 0, 15, 15, 15, 15);
                return 14;
            }
        });

        this.addDrawableChild(new SpruceIconButtonWidget(Position.of(24, 2), 20, 20, Text.empty(), (btn) -> this.client.setScreenAndRender(nextViewTypeFactory.apply(this.parent))) {
            @Override
            protected int renderIcon(DrawContext graphics, int mouseX, int mouseY, float delta) {
                graphics.drawTexture(SkinShuffle.id("textures/gui/todo-todo-todo.png"), this.getX() + this.getWidth() / 2 - (14 / 2), this.getY() + this.getHeight() / 2 - (14 / 2), 14, 14, 0, 0, 15, 15, 15, 15);
                return 14;
            }
        });

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this.width / 2 + 5, this.height - 23), 128, 20, Text.translatable("skinshuffle.carousel.save_button"), button -> {
            SpruceWidget chosenPresetWidget = this.carouselWidgets.get((int) Math.round(cardIndex));

            if(chosenPresetWidget instanceof AddCardWidget) {
                this.close();
                return;
            }

            assert chosenPresetWidget instanceof PresetWidget;
            PresetWidget presetWidget = (PresetWidget) chosenPresetWidget;

            SkinPresetManager.setChosenPreset(presetWidget.getPreset(), this.hasEditedPreset);
            SkinPresetManager.savePresets();

            if(this.client.world != null && !ClientSkinHandling.isInstalledOnServer()) {
                this.client.setScreen(GeneratedScreens.getReconnectScreen(this.parent));
            } else {
                this.close();
            }
        }));

        refreshPresetState();
    }

    @Override
    public void close() {
        // Save all presets to config when closing the screen, skipping any we can't
        for (var preset : SkinPresetManager.getLoadedPresets()) {
            try {
                var configSkin = preset.getSkin().saveToConfig();
                preset.setSkin(configSkin);
            } catch (Exception ignored) {}
        }

        SkinPresetManager.savePresets();

        this.client.setScreenAndRender(parent);
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
        int rows = getRows();
        int fullHeight = getCardHeight() * rows + getCardGap() * (rows - 1);

        double deltaIndex = getDeltaScrollIndex() / rows;
        int scrollOffset = (int) ((-deltaIndex + 1) * cardAreaWidth);
        var scrollOffsetPosition = Position.of(scrollOffset, 0);

        int i = 0;
        int leftI = this.width / 2 - cardAreaWidth - getCardWidth() / 2;
        int rowI = 0;
        for (AbstractCardWidget<?> widget : this.carouselWidgets) {
//            var widgetDeltaIndex = widget.getDeltaIndex() - i++;
//            var widgetXOffset = (int) (widgetDeltaIndex * cardAreaWidth);
            var widgetYOffset = (getCardHeight() + getCardGap()) * rowI;
//            var position = Position.of(
//                    widget.isDragging() ? mouseX - widget.getWidth() / 2 : leftI + scrollOffset,
//                    this.height / 2 - getCardHeight() / 2
//            );
            var position = Position.of(
                    scrollOffsetPosition, widget.getDeltaX(leftI),
                    widget.getDeltaY(this.height / 2 - fullHeight / 2 + widgetYOffset)
            );

//            graphics.drawTextWithShadow(this.textRenderer, String.valueOf(loadedPresets.indexOf(loadedPreset)), leftI + scrollOffset, this.height/2 - this.textRenderer.fontHeight /2 , 0xFFFFFFFF);
            if(widget instanceof PresetWidget<?> loadedPreset) {
                loadedPreset.overridePosition(position);
                loadedPreset.setScaleFactor(this.scaleFactor);
            } else if (widget instanceof AddCardWidget addCardWidget) {
                addCardWidget.overridePosition(position);
            }

            widget.setActive(Math.round(cardIndex) == i || getRows() > 1);
            widget.updateVisibility(i);

            if (++rowI >= rows) {
                rowI = 0;
                leftI += cardAreaWidth;
            }
            i++;
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

    public void scrollCarousel(double amount, boolean wrapAround) {
        if (this.carouselWidgets.size() == 1) {
            return;
        }

        amount *= getRows();
        var cardIndex = this.cardIndex + amount;

        if (wrapAround) {
            cardIndex = (cardIndex + this.carouselWidgets.size()) % this.carouselWidgets.size();
        }
        //noinspection IntegerDivisionInFloatingPointContext
        cardIndex = MathHelper.clamp(cardIndex, 0, (this.carouselWidgets.size() - 1) / getRows() * getRows());

        setCardIndex(cardIndex);
    }

    public void snapCarousel() {
        long cardIndex = Math.round(this.cardIndex) / getRows() * getRows();
        setCardIndex(cardIndex);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        var sign = SkinShuffleConfig.get().invertCarouselScroll ? -1 : 1;
        scrollCarousel(-amount / 4 * SkinShuffleConfig.get().carouselScrollSensitivity * sign, false);
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        carouselWidgets.forEach(widget -> widget.setDragging(false));

        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected abstract int getRows();

    public int getCardWidth() {
        return this.width / 4;
    }

    public int getCardHeight() {
        return ((int) (this.height / 1.5) - getCardGap() * (getRows() - 1)) / getRows();
    }

    public int getCardGap() {
        return (int) (10 * this.scaleFactor) / getRows();
    }

    protected abstract AbstractCardWidget widgetFromPreset(SkinPreset preset);

    private double getDeltaScrollIndex() {
        var deltaTime = (GlfwUtil.getTime() - lastCardSwitchTime) * 5;
        deltaTime = MathHelper.clamp(deltaTime, 0, 1);
        deltaTime = Math.sin(deltaTime * Math.PI / 2);
        return MathHelper.lerp(deltaTime, lastCardIndex, cardIndex);
    }

    public AbstractCardWidget loadPreset(SkinPreset preset) {
        var widget = widgetFromPreset(preset);
        this.carouselWidgets.get(this.carouselWidgets.size() - 1).refreshLastPosition();
        this.carouselWidgets.add(this.carouselWidgets.set(this.carouselWidgets.size() - 1, widget));
//        widget.lastIndex = this.carouselWidgets.size() - 2; // TODO TODO
        refreshPresetState();
        return widget;
    }

    public void swapPresets(int index1, int index2) {
        this.carouselWidgets.get(index1).refreshLastPosition();
        this.carouselWidgets.get(index2).refreshLastPosition();
        Collections.swap(this.carouselWidgets, index1, index2);
        SkinPresetManager.swapPresets(index1, index2);
        refreshPresetState();
    }

    public Optional<PresetWidget<?>> getPresetWidget(int index) {
        if (index < 0 || index >= this.carouselWidgets.size()) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.carouselWidgets.get(index))
                .filter(widget -> widget instanceof PresetWidget<?>)
                .map(widget -> (PresetWidget<?>) widget);
    }

    public void refreshPresetState() {
        this.carouselWidgets.forEach(AbstractCardWidget::refreshState);
    }

    public void setCardIndex(double index) {
        lastCardIndex = getDeltaScrollIndex();
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
