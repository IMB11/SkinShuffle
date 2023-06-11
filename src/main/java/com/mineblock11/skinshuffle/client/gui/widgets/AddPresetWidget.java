package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.SkinShuffle;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSprucePressableButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class AddPresetWidget extends AbstractSprucePressableButtonWidget {
    private Runnable action;
    private Position position;

    public AddPresetWidget(Position position, int width, int height) {
        super(position, width, height, Text.empty());
        this.position = position;
    }

    @Override
    public void onPress() {
        action.run();
    }

    public void setCallback(@Nullable Runnable action) {
        this.action = action;
    }

    @Override
    protected void renderBackground(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawBorder(getX(), getY(), getWidth(), getHeight(), this.active ? 0xDF000000 : 0x5F000000);
        graphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, this.active ? 0x7F000000 : 0x0D000000);
    }

    @Override
    protected void renderButton(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTexture(SkinShuffle.id("textures/gui/carousel_add.png"), getX() + (this.getWidth() / 2) - 16, getY() + (this.getHeight() / 2) - 16, 0, this.hovered || !this.active ? 32 : 0, 32, 32, 32, 64);
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
}
