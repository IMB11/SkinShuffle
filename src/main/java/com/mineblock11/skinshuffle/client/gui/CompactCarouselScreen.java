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
import com.mineblock11.skinshuffle.client.config.CarouselView;
import com.mineblock11.skinshuffle.client.gui.widgets.ActualSpruceIconButtonWidget;
import com.mineblock11.skinshuffle.client.gui.widgets.preset.AbstractCardWidget;
import com.mineblock11.skinshuffle.client.gui.widgets.preset.CompactPresetWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class CompactCarouselScreen extends CarouselScreen {
    private static final Text SHUFFLE_BUTTON_TOOLTIP_ENABLE = Text.translatable("skinshuffle.carousel.shuffle_button.tooltip.enable");
    private static final Text SHUFFLE_BUTTON_TOOLTIP_DISABLE = Text.translatable("skinshuffle.carousel.shuffle_button.tooltip.disable");

    private boolean editMode;
    private SpruceIconButtonWidget shuffleButton;

    public CompactCarouselScreen(Screen parent) {
        super(parent, CarouselView.COMPACT, CarouselView.LARGE);
    }

    @Override
    protected void init() {
        super.init();

        this.shuffleButton = this.addDrawableChild(new ActualSpruceIconButtonWidget(Position.of(46, 2), 20, 20, Text.empty(),
                (btn) -> setEditMode(!isEditMode()), (btn) -> SkinShuffle.id("textures/gui/shuffle-mode-" + (isEditMode() ? "on" : "off") + ".png")));

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this.width / 2 - 64, this.height - 23), 128, 20, ScreenTexts.DONE, button -> {
            this.close();
        }));

        this.cancelButton.setVisible(false);
        this.selectButton.setVisible(false);

        this.setEditMode(false);
    }

    private void setEditMode(boolean editMode) {
        this.editMode = editMode;
        this.shuffleButton.setTooltip(editMode ? SHUFFLE_BUTTON_TOOLTIP_DISABLE : SHUFFLE_BUTTON_TOOLTIP_ENABLE);
    }

    public boolean isEditMode() {
        return editMode;
    }

    @Override
    public int getRows() {
        return height / 120;
    }

    public int getCardHeight() {
        return (this.height - 80 - getCardGap() * (getRows() - 1)) / getRows();
    }

    @Override
    protected boolean supportsDragging() {
        return isEditMode();
    }

    @Override
    protected AbstractCardWidget widgetFromPreset(SkinPreset preset) {
        return new CompactPresetWidget(this, preset);
    }
}
