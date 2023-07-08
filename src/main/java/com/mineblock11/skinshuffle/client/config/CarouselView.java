package com.mineblock11.skinshuffle.client.config;

import com.mineblock11.skinshuffle.client.gui.CarouselScreen;
import com.mineblock11.skinshuffle.client.gui.CompactCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.LargeCarouselScreen;
import net.minecraft.client.gui.screen.Screen;

import java.util.function.Function;

public enum CarouselView {
    LARGE(LargeCarouselScreen::new),
    COMPACT(CompactCarouselScreen::new);

    public final Function<Screen, ? extends CarouselScreen> factory;

    CarouselView(Function<Screen, ? extends CarouselScreen> factory) {
        this.factory = factory;
    }
}
