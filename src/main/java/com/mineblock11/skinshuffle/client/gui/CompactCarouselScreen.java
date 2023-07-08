package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.CarouselView;
import com.mineblock11.skinshuffle.client.gui.widgets.preset.AbstractCardWidget;
import com.mineblock11.skinshuffle.client.gui.widgets.preset.CompactPresetWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;

public class CompactCarouselScreen extends CarouselScreen {
    private boolean editMode;

    public CompactCarouselScreen(Screen parent) {
        super(parent, CarouselView.LARGE);
    }

    @Override
    protected void init() {
        super.init();

        this.addDrawableChild(new SpruceIconButtonWidget(Position.of(46, 2), 20, 20, Text.empty(), (btn) -> setEditMode(!isEditMode())) {
            @Override
            protected int renderIcon(DrawContext graphics, int mouseX, int mouseY, float delta) {
                graphics.drawTexture(SkinShuffle.id("textures/gui/todo-todo-todo.png"), this.getX() + this.getWidth() / 2 - (14 / 2), this.getY() + this.getHeight() / 2 - (14 / 2), 14, 14, 0, 0, 15, 15, 15, 15);
                return 14;
            }
        });

        this.addDrawableChild(new SpruceButtonWidget(Position.of(this.width / 2 - 64, this.height - 23), 128, 20, ScreenTexts.DONE, button -> {
            this.close();
        }));

        this.cancelButton.setVisible(false);
        this.selectButton.setVisible(false);
    }

    private void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    public boolean isEditMode() {
        return editMode;
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
