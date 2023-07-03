package com.mineblock11.skinshuffle.client.gui.widgets.preset;

import com.mineblock11.skinshuffle.client.gui.CompactCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.PresetEditScreen;
import com.mineblock11.skinshuffle.client.gui.widgets.CarouselMoveButton;
import com.mineblock11.skinshuffle.client.gui.widgets.VariableSpruceButtonWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class CompactPresetWidget extends PresetWidget<CompactCarouselScreen> {
    protected VariableSpruceButtonWidget selectButton;
    protected CarouselMoveButton moveLeftButton;
    protected CarouselMoveButton moveRightButton;
    protected CarouselMoveButton moveUpButton;
    protected CarouselMoveButton moveDownButton;

    public CompactPresetWidget(CompactCarouselScreen parent, SkinPreset skinPreset) {
        super(parent, skinPreset);

        editButton.overridePosition((getWidth() / 2) + 4, getHeight() - 48);
        editButton.overrideDimensions(getWidth() / 2 - (4 * 2), 20);

        selectButton = new VariableSpruceButtonWidget(
                Position.of((getWidth() / 2) + 4, getHeight() - 24), getWidth() / 2 - (4 * 2), 20,
                Text.translatable("skinshuffle.carousel.preset_widget.select"),
                button -> {}
        );
        addChild(selectButton);

        copyButton.overridePosition((getWidth() / 2) + 4, getHeight() - 48);
        copyButton.overrideDimensions(getWidth() / 2 - (4 * 2), 20);
        copyButton.setVisible(false);

        deleteButton.overridePosition((getWidth() / 2) + 4, getHeight() - 24);
        deleteButton.overrideDimensions(getWidth() / 2 - (4 * 2), 20);
        deleteButton.setVisible(false);

        moveLeftButton = new CarouselMoveButton(Position.of(getX(), getY() + getHeight() / 2), CarouselMoveButton.Direction.RIGHT);
        moveRightButton = new CarouselMoveButton(Position.of(getX() + getWidth(), getY() + getHeight() / 2), CarouselMoveButton.Direction.LEFT);
        moveUpButton = new CarouselMoveButton(Position.of(getX() + getWidth() / 2, getY()), CarouselMoveButton.Direction.DOWN);
        moveDownButton = new CarouselMoveButton(Position.of(getX() + getWidth() / 2, getY() + getHeight()), CarouselMoveButton.Direction.UP);

        addChild(moveLeftButton);
        addChild(moveRightButton);
        addChild(moveUpButton);
        addChild(moveDownButton);
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        updateVisibility();

        super.render(graphics, mouseX, mouseY, delta);
    }

    protected void updateVisibility() {
        var editMode = parent.isEditMode();

        selectButton.setVisible(!editMode);
        editButton.setVisible(!editMode);
        copyButton.setVisible(editMode);
        deleteButton.setVisible(editMode);

        moveLeftButton.setVisible(editMode);
        moveRightButton.setVisible(editMode);
        moveUpButton.setVisible(editMode);
        moveDownButton.setVisible(editMode);
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
