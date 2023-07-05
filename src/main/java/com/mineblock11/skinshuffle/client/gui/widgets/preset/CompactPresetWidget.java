package com.mineblock11.skinshuffle.client.gui.widgets.preset;

import com.mineblock11.skinshuffle.client.gui.CompactCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.widgets.CarouselMoveButton;
import com.mineblock11.skinshuffle.client.gui.widgets.VariableSpruceButtonWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class CompactPresetWidget extends PresetWidget<CompactCarouselScreen> {
    protected VariableSpruceButtonWidget selectButton;
    protected CarouselMoveButton moveRightButton;
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
        deleteButton.setMessage(Text.translatable("skinshuffle.carousel.preset_widget.delete").formatted(Formatting.RED));
        deleteButton.setVisible(false);

        moveRightButton = new CarouselMoveButton(
                Position.of(getX() + getWidth() + parent.getCardGap() / 2, getY() + getHeight() / 2), CarouselMoveButton.Type.LEFT_RIGHT);
        moveDownButton = new CarouselMoveButton(
                Position.of(getX() + getWidth() / 2, getY() + getHeight() + parent.getCardGap() / 2), CarouselMoveButton.Type.UP_DOWN);

        moveRightButton.setCallback(() -> parent.swapPresets(getIndex(), getIndex() + parent.getRows()));
        moveDownButton.setCallback(() -> parent.swapPresets(getIndex(), getIndex() + 1));

        addChild(moveRightButton);
        addChild(moveDownButton);
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public void updateVisibility(int index) {
        var editMode = parent.isEditMode();

        selectButton.setVisible(!editMode);
        editButton.setVisible(!editMode);
        copyButton.setVisible(editMode);
        deleteButton.setVisible(editMode);

        moveRightButton.setVisible(editMode && parent.getPresetWidget(index + parent.getRows()).map(PresetWidget::isMovable).orElse(false));
        moveDownButton.setVisible(editMode && index % parent.getRows() == 0 && parent.getPresetWidget(index + 1).map(PresetWidget::isMovable).orElse(false));
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
