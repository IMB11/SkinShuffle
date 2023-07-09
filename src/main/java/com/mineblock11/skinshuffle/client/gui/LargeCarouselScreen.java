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

import com.mineblock11.skinshuffle.client.config.CarouselView;
import com.mineblock11.skinshuffle.client.gui.widgets.CarouselMoveButton;
import com.mineblock11.skinshuffle.client.gui.widgets.preset.AbstractCardWidget;
import com.mineblock11.skinshuffle.client.gui.widgets.preset.LargePresetWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import net.minecraft.client.gui.screen.Screen;

public class LargeCarouselScreen extends CarouselScreen {
    public CarouselMoveButton leftMoveButton;
    public CarouselMoveButton rightMoveButton;

    public LargeCarouselScreen(Screen parent) {
        super(parent, CarouselView.LARGE, CarouselView.COMPACT);
    }

    @Override
    protected void init() {
        super.init();

        leftMoveButton = new CarouselMoveButton(Position.of((getCardWidth() / 2), (this.height / 2) - 8), false);
        rightMoveButton = new CarouselMoveButton(Position.of(this.width - (getCardWidth() / 2), (this.height / 2) - 8), true);

        leftMoveButton.setCallback(() -> {
            scrollCarousel(-1, true);
            snapCarousel();
        });
        rightMoveButton.setCallback(() -> {
            scrollCarousel(1, true);
            snapCarousel();
        });

        this.addDrawableChild(leftMoveButton);
        this.addDrawableChild(rightMoveButton);

        this.leftMoveButton.setActive(this.carouselWidgets.size() != 1);
        this.rightMoveButton.setActive(this.carouselWidgets.size() != 1);
    }

    @Override
    protected int getRows() {
        return 1;
    }

    @Override
    protected AbstractCardWidget widgetFromPreset(SkinPreset preset) {
        return new LargePresetWidget(this, preset);
    }
}
