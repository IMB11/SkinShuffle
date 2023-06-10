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
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class SkinPresetWidget extends SpruceContainerWidget {
    private final SkinPreset skinPreset;
    private final SkinCarouselScreen parent;
    private Position position;
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
                Position.of(4, height - 48), width - 8, 20,
                Text.translatable("gui.skinshuffle.skin_carousel.skin_preset_widget.edit_preset"),
                button -> {
                    // TODO
                }
        ));

        addChild(new SpruceButtonWidget(
                Position.of(4, height - 24), width / 2 - 6, 20,
                Text.translatable("gui.skinshuffle.skin_carousel.skin_preset_widget.copy_preset"),
                button -> {
                    // TODO
                }
        ));

        addChild(new SpruceButtonWidget(
                Position.of(width / 2 + 2, height - 24), width / 2 - 6, 20,
                Text.translatable("gui.skinshuffle.skin_carousel.skin_preset_widget.delete_preset"),
                button -> {
                    // TODO
                }
        ));
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
        graphics.drawTextWithShadow(this.client.textRenderer, this.skinPreset.getName() != null ? this.skinPreset.getName() : "Unnamed Preset", getX() + (this.width / 2) - (this.client.textRenderer.getWidth(this.skinPreset.getName() != null ? this.skinPreset.getName() : "Unnamed Preset")) / 2, getY() + this.client.textRenderer.fontHeight / 2, this.active ? 0xFFFFFFFF : 0xFF808080);

        GuiEntityRenderer.drawEntity(
                graphics.getMatrices(), getX() + (this.getWidth() / 2), this.getY() + this.height / 2,
                this.height / 5, getEntityRotation(), 0, 0, entity
        );

        super.renderWidget(graphics, mouseX, mouseY, delta);
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    private float getEntityRotation() {
        return isActive() ? (float) (GlfwUtil.getTime() - parent.getLastCardSwitchTime()) * 35.0f : 0.0f;
    }
}
