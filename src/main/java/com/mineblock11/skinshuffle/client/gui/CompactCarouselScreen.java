package com.mineblock11.skinshuffle.client.gui;

import net.minecraft.client.gui.screen.Screen;

public class CompactCarouselScreen extends CarouselScreen {
    public CompactCarouselScreen(Screen parent) {
        super(parent, LargeCarouselScreen::new);
    }

    @Override
    protected int getRows() {
        return 2;
    }
}
