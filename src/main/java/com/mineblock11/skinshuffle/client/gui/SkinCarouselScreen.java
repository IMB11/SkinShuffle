package com.mineblock11.skinshuffle.client.gui;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.SprucePositioned;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import net.minecraft.client.gui.DrawContext;
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

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);
        graphics.fill(0, this.textRenderer.fontHeight * 3, this.width, this.height - (this.textRenderer.fontHeight * 3), 0x7F000000);
        ScissorManager.pushScaleFactor(this.scaleFactor);
        this.renderWidgets(graphics, mouseX, mouseY, delta);
        this.renderTitle(graphics, mouseX, mouseY, delta);
        Tooltip.renderAll(graphics);
        ScissorManager.popScaleFactor();;

    }

    @Override
    public void renderTitle(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawCenteredTextWithShadow(this.textRenderer, this.getTitle().asOrderedText(), this.width / 2, this.textRenderer.fontHeight, 0xFFFFFFFF);
        graphics.fillGradient(0, (int) (this.textRenderer.fontHeight * 2.5), this.width, this.textRenderer.fontHeight * 3, 0x00000000, 0x7F000000);
    }
}
