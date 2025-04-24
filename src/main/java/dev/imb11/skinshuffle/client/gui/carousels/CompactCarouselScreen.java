package dev.imb11.skinshuffle.client.gui.carousels;

import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.client.config.CarouselView;
import dev.imb11.skinshuffle.client.gui.widgets.buttons.SkinShuffleIconButton;
import dev.imb11.skinshuffle.client.gui.widgets.presets.AbstractCardWidget;
import dev.imb11.skinshuffle.client.gui.widgets.presets.CompactPresetWidget;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class CompactCarouselScreen extends CarouselScreen {
    private static final Text SHUFFLE_BUTTON_TOOLTIP_ENABLE = Text.translatable("skinshuffle.carousel.shuffle_button.tooltip.enable");
    private static final Text SHUFFLE_BUTTON_TOOLTIP_DISABLE = Text.translatable("skinshuffle.carousel.shuffle_button.tooltip.disable");

    private boolean editMode;
    private SpruceIconButtonWidget shuffleButton;

    public CompactCarouselScreen(Screen parent) {
        super(parent, CarouselView.COMPACT, CarouselView.LARGE);
    }

    @Override
    protected void init() {
        super.init();

        this.shuffleButton = this.addDrawableChild(new SkinShuffleIconButton(Position.of(46, 2), 20, 20, Text.empty(),
                (btn) -> setEditMode(!isEditMode()), (btn) -> SkinShuffle.id("textures/gui/shuffle-mode-" + (isEditMode() ? "on" : "off") + ".png")));

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this.width / 2 - 64, this.height - 23), 128, 20, ScreenTexts.DONE, button -> {
            this.handleCloseBehaviour();
        }));

        this.cancelButton.setVisible(false);
        this.selectButton.setVisible(false);

        this.setEditMode(false);
    }

    public boolean isEditMode() {
        return editMode;
    }

    private void setEditMode(boolean editMode) {
        this.editMode = editMode;
        this.shuffleButton.setTooltip(editMode ? SHUFFLE_BUTTON_TOOLTIP_DISABLE : SHUFFLE_BUTTON_TOOLTIP_ENABLE);
    }

    @Override
    public int getRows() {
        return height / 120;
    }

    public int getCardHeight() {
        return (this.height - 80 - getCardGap() * (getRows() - 1)) / getRows();
    }

    @Override
    protected boolean supportsDragging() {
        return isEditMode();
    }

    @Override
    protected AbstractCardWidget widgetFromPreset(SkinPreset preset) {
        return new CompactPresetWidget(this, preset);
    }
}
