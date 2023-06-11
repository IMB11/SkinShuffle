package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.skin.ResourceSkin;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSprucePressableButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class AddPresetWidget extends SpruceContainerWidget {
    private final SkinCarouselScreen parent;
//    private final DummyClientPlayerEntity entity;
    private Runnable action;
    private Position position;

    public AddPresetWidget(SkinCarouselScreen parent, Position position, int width, int height) {
        super(position, width, height);
        this.position = position;
        this.parent = parent;


        addChild(new SpruceButtonWidget(Position.of(3, getHeight() - 24), width - 6, 20, Text.translatable("skinshuffle.carousel.create"), button -> {
            action.run();
        }));
    }

    public void setCallback(@Nullable Runnable action) {
        this.action = action;
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        for (SpruceWidget child : this.children()) {
            child.setActive(active);
        }
    }

    @Override
    protected void renderBackground(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawBorder(getX(), getY(), getWidth(), getHeight(), this.active ? 0xDF000000 : 0x5F000000);
        graphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, this.active ? 0x7F000000 : 0x0D000000);
        graphics.drawTexture(SkinShuffle.id("textures/gui/carousel_add.png"), getX() + (this.getWidth() / 2) - 16, getY() + (this.getHeight() / 2) - 16, 0, !this.active ? 32 : 0, 32, 32, 32, 64);

        var text = Text.translatable("skinshuffle.carousel.new");
        graphics.drawTextWithShadow(this.client.textRenderer, text, getX() + (this.width / 2) - this.client.textRenderer.getWidth(text) / 2, getY() + this.client.textRenderer.fontHeight / 2, this.active ? 0xFFFFFFFF : 0xFF808080);
    }

    private float getEntityRotation() {
        return isActive() ? (float) (GlfwUtil.getTime() - parent.getLastCardSwitchTime()) * 35.0f : 0.0f;
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
