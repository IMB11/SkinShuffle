package dev.imb11.skinshuffle.client.gui;

import dev.imb11.skinshuffle.client.config.CarouselView;
import dev.imb11.skinshuffle.client.gui.widgets.CarouselMoveButton;
import dev.imb11.skinshuffle.client.gui.widgets.preset.AbstractCardWidget;
import dev.imb11.skinshuffle.client.gui.widgets.preset.LargePresetWidget;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
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

//        leftMoveButton = new CarouselMoveButton(Position.of((getCardWidth() / 2), this.height - 20), false);
//        rightMoveButton = new CarouselMoveButton(Position.of(this.width - (getCardWidth() / 2), this.height - 20), true);
//
//        leftMoveButton.setCallback(() -> {
//            scrollCarousel(1, true);
//            snapCarousel();
//        });
//        rightMoveButton.setCallback(() -> {
//            scrollCarousel(-1, true);
//            snapCarousel();
//        });

//        this.addDrawableChild(leftMoveButton);
//        this.addDrawableChild(rightMoveButton);

//        this.leftMoveButton.setActive(this.carouselWidgets.size() != 1);
//        this.rightMoveButton.setActive(this.carouselWidgets.size() != 1);
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
