/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.PresetEditScreen;
import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.UUID;

public class SkinPresetWidget extends PresetWidget {
    private final SkinPreset skinPreset;
    private SpruceButtonWidget editButton;
    private SpruceButtonWidget copyButton;
    private SpruceButtonWidget deleteButton;
    private CarouselMoveButton moveLeftButton;
    private CarouselMoveButton moveRightButton;
    private final boolean showButtons;
    private Position position = Position.of(0, 0);
    private LivingEntity entity;
    private double scaleFactor;

    public SkinPresetWidget(SkinCarouselScreen parent, int width, int height, SkinPreset skinPreset, boolean showButtons) {
        super(Position.of(0, 0), width, height, parent);

        this.skinPreset = skinPreset;

        var skin = skinPreset.getSkin();
        entity = new DummyClientPlayerEntity(
                null, UUID.randomUUID(),
                skin::getTexture, skin::getModel
        );

        this.showButtons = showButtons;

        if(showButtons) {
            this.editButton = new SpruceButtonWidget(
                    Position.of(getWidth() / 8, getHeight() - 48), getWidth() - (this.getWidth() / 4), 20,
                    Text.translatable("skinshuffle.carousel.preset_widget.edit"),
                    button -> client.setScreen(new PresetEditScreen(this.parent, this.skinPreset))
            );

            this.copyButton = new SpruceButtonWidget(
                    Position.of(3, getHeight() - 24), getWidth() / 2 - 5, 20,
                    Text.translatable("skinshuffle.carousel.preset_widget.copy"),
                    button -> {
                        SkinPreset presetCopy = new SkinPreset(this.skinPreset.getSkin(), this.skinPreset.getName());
                        SkinPresetManager.addPreset(presetCopy);
                        this.parent.refresh();
                    }
            );

            this.deleteButton = new SpruceButtonWidget(
                    Position.of(getWidth() / 2 + 2, getHeight() - 24), getWidth() / 2 - 5, 20,
                    Text.translatable("skinshuffle.carousel.preset_widget.delete"),
                    button -> {
                        ConfirmScreen confirmScreen = new ConfirmScreen(result -> {
                            if(result) {
                                SkinPresetManager.deletePreset(this.skinPreset);
                            }
                            this.parent.refresh();
                            this.client.setScreen(this.parent);
                        }, Text.translatable("skinshuffle.carousel.confirmations.delete_preset.title"), Text.translatable("skinshuffle.carousel.confirmations.delete_preset.message"));
                        this.client.setScreen(confirmScreen);
                    }
            );

            this.moveLeftButton = new CarouselMoveButton(Position.of(0, getHeight() - 46), false);
            this.moveRightButton = new CarouselMoveButton(Position.of(getWidth(), getHeight() - 46), true);

            this.moveLeftButton.setCallback(() -> {
                var i = parent.carouselWidgets.indexOf(this);
                parent.swapPresets(i, i - 1);
                parent.scrollCarousel(-1, false);
            });
            this.moveRightButton.setCallback(() -> {
                var i = parent.carouselWidgets.indexOf(this);
                parent.swapPresets(i, i + 1);
                parent.scrollCarousel(1, false);
            });

            if(SkinPresetManager.getLoadedPresets().size() < 2) this.deleteButton.setActive(false);

            addChild(deleteButton);
            addChild(editButton);
            addChild(copyButton);
            addChild(moveLeftButton);
            addChild(moveRightButton);
        }
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        for (SpruceWidget child : this.children()) {
            if(child.equals(this.deleteButton) && SkinPresetManager.getLoadedPresets().size() < 2) continue;
            child.setActive(active);
        }

        int i = this.parent.carouselWidgets.indexOf(this);
        moveLeftButton.setVisible(active && this.isMovable() && i > 0 && this.parent.carouselWidgets.get(i - 1).isMovable());
        moveRightButton.setVisible(active && this.isMovable() && i < this.parent.carouselWidgets.size() - 1 && this.parent.carouselWidgets.get(i + 1).isMovable());
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
    protected boolean onMouseClick(double mouseX, double mouseY, int button) {
        var it = this.iterator();

        SpruceWidget element;
        do {
            if (!it.hasNext()) {
                if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
                    this.setDragging(true);
                }

                return true;
            }

            element = it.next();
        } while (!element.mouseClicked(mouseX, mouseY, button));

        this.setFocused(element);

        return true;
    }

    @Override
    protected void renderBackground(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawBorder(getX(), getY(), getWidth(), getHeight(), this.active ? 0xDF000000 : 0x5F000000);
        graphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, this.active ? 0x7F000000 : 0x0D000000);
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(graphics, mouseX, mouseY, delta);

        var margin = this.client.textRenderer.fontHeight / 2;
        var name = this.skinPreset.getName() != null ? this.skinPreset.getName() : "Unnamed Preset";
        var nameWidth = this.client.textRenderer.getWidth(name);
        var halfWidth = this.width / 2;
        var halfNameWidth = nameWidth / 2;
        ClickableWidget.drawScrollableText(
                graphics, this.client.textRenderer,
                Text.of(name),
                getX() + halfWidth - Math.min(halfWidth - margin, halfNameWidth), getY() + margin,
                getX() + halfWidth + Math.min(halfWidth - margin, halfNameWidth), getY() + margin + this.client.textRenderer.fontHeight,
                this.active ? 0xFFFFFFFF : 0xFF808080
        );

        var previewX = getX() + this.getWidth() / 2;
        var previewY = (int) (this.getY() + (showButtons ? this.height / 1.6 : this.height * (1.5 / 2)));

        float followX = (float) previewX - mouseX;
        float followY = (float) (previewY - this.height / 40 * 16) - mouseY;
        float rotation = 0;

        SkinShuffleConfig.SkinRenderStyle renderStyle = SkinShuffleConfig.get().carouselSkinRenderStyle;

        if(renderStyle.equals(SkinShuffleConfig.SkinRenderStyle.ROTATION)) {
            followX = 0;
            followY = 0;
            rotation = getEntityRotation() * SkinShuffleConfig.get().rotationMultiplier;
        }

        if(!isActive()) {
            followX = 0;
            followY = 0;
            rotation = 0;
        }

        GuiEntityRenderer.drawEntity(
                graphics.getMatrices(), previewX, previewY,
                this.height / 4, rotation, followX, followY, entity
        );
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    private float getEntityRotation() {
        return isActive() && SkinShuffleConfig.get().carouselSkinRenderStyle.equals(SkinShuffleConfig.SkinRenderStyle.ROTATION) ? (float) (GlfwUtil.getTime() - (parent != null ? parent.getLastCardSwitchTime() : 0)) * 35.0f : 0.0f;
    }

    public SkinPreset getPreset() {
        return this.skinPreset;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return super.getNavigationFocus();
    }

    @Override
    public void refreshState() {

    }

    @Override
    public boolean isMovable() {
        return true;
    }
}
