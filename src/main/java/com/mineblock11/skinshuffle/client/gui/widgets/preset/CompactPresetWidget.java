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

package com.mineblock11.skinshuffle.client.gui.widgets.preset;

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.gui.CompactCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.widgets.VariableSpruceButtonWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.lwjgl.glfw.GLFW;

public class CompactPresetWidget extends PresetWidget<CompactCarouselScreen> {
    private static final Random WIGGLE_RANDOM = Random.create();

    protected VariableSpruceButtonWidget selectButton;
    private final float wiggleAmount = WIGGLE_RANDOM.nextFloat() + 1f;
    private final float wiggleSpeed = WIGGLE_RANDOM.nextFloat() * 4f + 16f;

    public CompactPresetWidget(CompactCarouselScreen parent, SkinPreset skinPreset) {
        super(parent, skinPreset);

        editButton.overridePosition((getWidth() / 2) + 4, getHeight() - 48);
        editButton.overrideDimensions(getWidth() / 2 - (4 * 2), 20);

        selectButton = new VariableSpruceButtonWidget(
                Position.of((getWidth() / 2) + 4, getHeight() - 24), getWidth() / 2 - (4 * 2), 20,
                Text.translatable("skinshuffle.carousel.preset_widget.select"),
                button -> {
                    SkinPresetManager.setChosenPreset(getPreset(), parent.hasEditedPreset);
                    SkinPresetManager.savePresets();
                }
        );
        addChild(selectButton);

        copyButton.overridePosition((getWidth() / 2) + 4, getHeight() - 48);
        copyButton.overrideDimensions(getWidth() / 2 - (4 * 2), 20);
        copyButton.setVisible(false);

        deleteButton.overridePosition((getWidth() / 2) + 4, getHeight() - 24);
        deleteButton.overrideDimensions(getWidth() / 2 - (4 * 2), 20);
        deleteButton.setMessage(Text.translatable("skinshuffle.carousel.preset_widget.delete").formatted(Formatting.RED));
        deleteButton.setVisible(false);
    }

    @Override
    public void updateVisibility(int index) {
        var editMode = parent.isEditMode();

        selectButton.setVisible(!editMode);
        editButton.setVisible(!editMode);
        copyButton.setVisible(editMode);
        deleteButton.setVisible(editMode);
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        var matrices = graphics.getMatrices();
        matrices.push();

        if (parent.isEditMode() && isMovable()) {
            var x = getX() + getWidth() / 2;
            var y = getY() + getHeight() / 2;
            matrices.translate(x, y, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float) Math.sin(GlfwUtil.getTime() * wiggleSpeed) * wiggleAmount));
            matrices.translate(-x, -y, 0);
        }
        super.render(graphics, mouseX, mouseY, delta);

        matrices.pop();
    }

    @Override
    protected boolean onMouseClick(double mouseX, double mouseY, int button) {
        for (SpruceWidget widget : this) {
            if (widget.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }

        if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
            this.setDragging(true);
            this.setDragStart(mouseX - getX(), mouseY - getY());
            return true;
        }

        return false;
    }

    private int getPreviewMargin() {
        return 4;
    }

    @Override
    protected int getPreviewX() {
        return getX() + width / 4;
    }

    @Override
    protected int getPreviewY() {
        return getY() + height - getPreviewMargin();
    }

    @Override
    protected int getPreviewSize() {
        return (height - getPreviewMargin() * 2) * 10 / 22;
    }
}
