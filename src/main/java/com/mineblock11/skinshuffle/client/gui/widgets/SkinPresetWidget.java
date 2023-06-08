package com.mineblock11.skinshuffle.client.gui.widgets;

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSpruceWidget;
import net.minecraft.client.gui.DrawContext;

public class SkinPresetWidget extends AbstractSpruceWidget {
    private final Object skinPreset;
    private Position position;

    public SkinPresetWidget(Position position, int width, int height, Object skinPreset) {
        super(position);

        this.skinPreset = skinPreset;
        this.width = width;
        this.height = height;
    }

    public void overridePosition(Position newPosition) {
        this.position = newPosition;
    }

    public void overrideDimensions(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
    }

    @Override
    public int getX() {
        return this.position.getX();
    }

    @Override
    public int getY() {
        return this.position.getY();
    }

    @Override
    protected void renderBackground(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawBorder(getX(), getY(), getWidth(), getHeight(), 0xDF000000);
        graphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, 0x7F000000);
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTextWithShadow(this.client.textRenderer, "{" + getX() + "," + getY() + "}", getX(), getY(), 0xFFFFFFFF);
    }
}
