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
        super(parent, CarouselView.COMPACT);
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
