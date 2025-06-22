package dev.imb11.skinshuffle.client.gui.widgets.presets;

import dev.imb11.skinshuffle.client.gui.carousels.LargeCarouselScreen;
import dev.imb11.skinshuffle.client.gui.widgets.buttons.CarouselMoveButton;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;

public class LargePresetWidget extends PresetWidget<LargeCarouselScreen> {
    protected CarouselMoveButton moveLeftButton;
    protected CarouselMoveButton moveRightButton;

    public LargePresetWidget(LargeCarouselScreen parent, SkinPreset skinPreset) {
        super(parent, skinPreset);

        editButton.overridePosition((getWidth() / 8) + 4, getHeight() - 48);
        editButton.overrideDimensions(getWidth() - (this.getWidth() / 4) - 8, 20);

        copyButton.overridePosition(3, getHeight() - 24);
        copyButton.overrideDimensions(getWidth() / 2 - 5, 20);

        deleteButton.overridePosition(getWidth() / 2 + 2, getHeight() - 24);
        deleteButton.overrideDimensions(getWidth() / 2 - 5, 20);

        this.moveLeftButton = new CarouselMoveButton(Position.of(2, getHeight() - 46), false);
        this.moveRightButton = new CarouselMoveButton(Position.of(getWidth() - 2, getHeight() - 46), true);

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

        addChild(moveLeftButton);
        addChild(moveRightButton);
    }

    @Override
    protected float getPreviewScaling() {
        return 1.0f;
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        int i = this.parent.carouselWidgets.indexOf(this);
        moveLeftButton.setVisible(active && this.isMovable() && i > 0 && this.parent.carouselWidgets.get(i - 1).isMovable());
        moveRightButton.setVisible(active && this.isMovable() && i < this.parent.carouselWidgets.size() - 1 && this.parent.carouselWidgets.get(i + 1).isMovable());
    }

    @Override
    protected int[] getPreviewBounds() {
        // Create a centered rectangular area for the large view
        int margin = 8;
        int previewWidth = getWidth() / 3;  // Center third of the widget
        int previewHeight = getHeight() - margin * 2 - 70; // Height minus margins and button space

        int centerX = getX() + getWidth() / 2;
        int x1 = centerX - previewWidth / 2;
        int x2 = centerX + previewWidth / 2;
        int y1 = getY() + margin + this.client.textRenderer.fontHeight + 5; // Below the title
        int y2 = y1 + previewHeight;

        return new int[]{x1, y1, x2, y2};
    }
}
