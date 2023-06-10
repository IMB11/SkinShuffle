package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSpruceWidget;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import dev.lambdaurora.spruceui.widget.container.AbstractSpruceParentWidget;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class SkinPresetWidget extends SpruceContainerWidget {
    private final SkinPreset skinPreset;
    private final SkinCarouselScreen parent;
    private Position position = Position.of(0, 0);
    private LivingEntity entity;
    private double scaleFactor;

    public SkinPresetWidget(SkinCarouselScreen parent, int width, int height, SkinPreset skinPreset) {
        super(Position.of(0, 0), width, height);

        this.parent = parent;
        this.skinPreset = skinPreset;

        var skin = skinPreset.getSkin();
        entity = new DummyClientPlayerEntity(
                null, UUID.randomUUID(),
                skin::getTexture, skin::getModel
        );

        addChild(new SpruceButtonWidget(
                Position.of(getWidth() / 8, getHeight() - 48), getWidth() - (this.getWidth() / 4), 20,
                Text.translatable("skinshuffle.carousel.preset_widget.edit"),
                button -> {
                    // TODO
                }
        ));

        addChild(new SpruceButtonWidget(
                Position.of(3, getHeight() - 24), getWidth() / 2 - 5, 20,
                Text.translatable("skinshuffle.carousel.preset_widget.copy"),
                button -> {

                }
        ));

        addChild(new SpruceButtonWidget(
                Position.of(getWidth() / 2 + 2, getHeight() - 24), getWidth() / 2 - 5, 20,
                Text.translatable("skinshuffle.carousel.preset_widget.delete"),
                button -> {
                    ConfirmScreen confirmScreen = new ConfirmScreen(result -> {
                        if(result) {
                            // delete preset
                        }
                        this.client.setScreen(parent);
                    }, Text.translatable("skinshuffle.carousel.confirmations.delete_preset.title"), Text.translatable("skinshuffle.carousel.confirmations.delete_preset.message"));

                    this.client.setScreen(confirmScreen);
                }
        ));
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        for (SpruceWidget child : this.children()) {
            child.setActive(active);
        }
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
        graphics.drawBorder(getX(), getY(), getWidth(), getHeight(), this.active ? 0xDF000000 : 0x5F000000);
        graphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, this.active ? 0x7F000000 : 0x0D000000);
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(graphics, mouseX, mouseY, delta);

        graphics.drawTextWithShadow(this.client.textRenderer, this.skinPreset.getName() != null ? this.skinPreset.getName() : "Unnamed Preset", getX() + (this.width / 2) - (this.client.textRenderer.getWidth(this.skinPreset.getName() != null ? this.skinPreset.getName() : "Unnamed Preset")) / 2, getY() + this.client.textRenderer.fontHeight / 2, this.active ? 0xFFFFFFFF : 0xFF808080);

        GuiEntityRenderer.drawEntity(
                graphics.getMatrices(), getX() + (this.getWidth() / 2), (int) (this.getY() + this.height / 1.6),
                this.height / 4, getEntityRotation(), 0, 0, entity
        );
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    private float getEntityRotation() {
        return isActive() ? (float) (GlfwUtil.getTime() - parent.getLastCardSwitchTime()) * 35.0f : 0.0f;
    }

    private static class SkinPresetButton extends SpruceButtonWidget {
        public SkinPresetButton(Position position, int width, int height, Text message, PressAction action) {
            super(position, width, height, message, action);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!this.isActive() || !this.isVisible() || !this.isFocusedOrHovered())
                return false;

            return this.onMouseClick(mouseX, mouseY, button);
        }
    }
}
