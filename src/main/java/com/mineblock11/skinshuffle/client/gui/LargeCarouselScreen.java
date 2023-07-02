package com.mineblock11.skinshuffle.client.gui;

import net.minecraft.client.gui.screen.Screen;

public class LargeCarouselScreen extends CarouselScreen {
    public LargeCarouselScreen(Screen parent) {
        super(parent, CompactCarouselScreen::new);
    }

    @Override
    protected void init() {
        super.init();


    }

    @Override
    protected int getRows() {
        return 1;
    }
}
