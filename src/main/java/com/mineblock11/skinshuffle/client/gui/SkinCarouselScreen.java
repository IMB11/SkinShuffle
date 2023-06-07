package com.mineblock11.skinshuffle.client.gui;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SprucePositioned;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.HashMap;

public class SkinCarouselScreen extends SpruceScreen {
    public SkinCarouselScreen() {
        super(Text.translatable("skinshuffle.gui.skincarousel"));
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new CarouselMoveButton(Position.of((this.width / 6) - 8, (this.height / 2) - 8), false));
        this.addDrawableChild(new CarouselMoveButton(Position.of(this.width - (this.width / 6) - 8, (this.height / 2) - 8), true));
    }
}
