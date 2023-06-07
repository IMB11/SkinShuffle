package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.SkinShuffle;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.AbstractSprucePressableButtonWidget;
import dev.lambdaurora.spruceui.widget.AbstractSpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CarouselMoveButton extends AbstractSpruceWidget {
    private static Identifier ARROW_TEXTURES = new Identifier(SkinShuffle.MOD_ID, "textures/gui/carousel_arrows.png");
    private final boolean isRight;

    public CarouselMoveButton(Position position, boolean isRight) {
        super(position);
        this.isRight = isRight;
    }

    @Override
    protected void renderWidget(DrawContext guiGraphics, int mouseX, int mouseY, float delta) {
        guiGraphics.drawTexture(ARROW_TEXTURES, getX(), getY(), (isRight ? 16 : 0), (isFocusedOrHovered() ? 16 : 0), 16, 16);
    }
}
