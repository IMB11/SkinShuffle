package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.client.gui.widgets.preset.AbstractCardWidget;
import com.mineblock11.skinshuffle.client.gui.widgets.preset.LargePresetWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.gui.screen.Screen;

public class LargeCarouselScreen extends CarouselScreen {
    public LargeCarouselScreen(Screen parent) {
        super(parent, CompactCarouselScreen::new);
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
