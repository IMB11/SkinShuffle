package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.client.gui.widgets.CarouselMoveButton;
import com.mineblock11.skinshuffle.client.gui.widgets.SkinPresetWidget;
import com.sun.jna.platform.win32.OaIdl;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;

public class SkinCarouselScreen extends SpruceScreen {
    private static int PRESET_CARD_WIDTH, PRESET_CARD_HEIGHT, PRESET_CARD_GAP;
    public SkinCarouselScreen() {
        super(Text.translatable("skinshuffle.gui.skincarousel"));
    }

    public CarouselMoveButton leftMoveButton;
    public CarouselMoveButton rightMoveButton;
    public int cardIndex = 0;
    public ArrayList<SkinPresetWidget> loadedPresets = new ArrayList<>();

    @Override
    protected void init() {
        super.init();

        leftMoveButton = new CarouselMoveButton(Position.of((this.width / 4) + 8, (this.height / 2) - 8), false);
        rightMoveButton = new CarouselMoveButton(Position.of(this.width - (this.width / 4) - 8, (this.height / 2) - 8), true);

        leftMoveButton.setCallback(() -> {
            cardIndex = (cardIndex - 1 + (this.loadedPresets.size())) % (this.loadedPresets.size());
            if (cardIndex < 0) {
                cardIndex = this.loadedPresets.size() - 1;
            }
            System.out.println(cardIndex);
        });
        rightMoveButton.setCallback(() -> {
            cardIndex = (cardIndex + 1) % (this.loadedPresets.size());
            if (cardIndex < 0) {
                cardIndex = this.loadedPresets.size();
            }
            System.out.println(cardIndex);
        });

        this.loadedPresets.add(new SkinPresetWidget(Position.of(0, 0), this.width / 8, this.height / 4, new Object()));
        this.loadedPresets.add(new SkinPresetWidget(Position.of(0, 0), this.width / 8, this.height / 4, new Object()));
        this.loadedPresets.add(new SkinPresetWidget(Position.of(0, 0), this.width / 8, this.height / 4, new Object()));
        this.loadedPresets.add(new SkinPresetWidget(Position.of(0, 0), this.width / 8, this.height / 4, new Object()));
        this.loadedPresets.add(new SkinPresetWidget(Position.of(0, 0), this.width / 8, this.height / 4, new Object()));
        this.loadedPresets.add(new SkinPresetWidget(Position.of(0, 0), this.width / 8, this.height / 4, new Object()));

        this.addSelectableChild(leftMoveButton);
        this.addSelectableChild(rightMoveButton);
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        PRESET_CARD_WIDTH = this.width / 4;
        PRESET_CARD_HEIGHT = (int) (this.height / 1.5);
        PRESET_CARD_GAP = (int) (10 * this.scaleFactor);

        // BG stuff
        this.renderBackground(graphics);
        graphics.fill(0, this.textRenderer.fontHeight * 3, this.width, this.height - (this.textRenderer.fontHeight * 3), 0x7F000000);
        ScissorManager.pushScaleFactor(this.scaleFactor);

        // Carousel Widgets
        int xOffset = (-cardIndex + 1) * (PRESET_CARD_WIDTH + PRESET_CARD_GAP);
        int currentX = this.width / 16;
        for (SkinPresetWidget loadedPreset : this.loadedPresets) {
            graphics.drawTextWithShadow(this.textRenderer, String.valueOf(loadedPresets.indexOf(loadedPreset)), currentX + xOffset, PRESET_CARD_HEIGHT - this.textRenderer.fontHeight, 0xFFFFFFFF);
            loadedPreset.overridePosition(Position.of(currentX + xOffset, (this.height / 2) - (PRESET_CARD_HEIGHT / 2)));
            loadedPreset.overrideDimensions(PRESET_CARD_WIDTH, PRESET_CARD_HEIGHT);

            if(cardIndex == this.loadedPresets.indexOf(loadedPreset)) {
                loadedPreset.setActive(true);
            } else {
                loadedPreset.setActive(false);
            }

            loadedPreset.render(graphics, mouseX, mouseY, delta);
            currentX += PRESET_CARD_WIDTH + PRESET_CARD_GAP;
        }

        this.renderWidgets(graphics, mouseX, mouseY, delta);
        this.renderTitle(graphics, mouseX, mouseY, delta);
        Tooltip.renderAll(graphics);
        ScissorManager.popScaleFactor();

    }

    @Override
    public void renderTitle(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawCenteredTextWithShadow(this.textRenderer, this.getTitle().asOrderedText(), this.width / 2, this.textRenderer.fontHeight, 0xFFFFFFFF);
        graphics.fillGradient(0, (int) (this.textRenderer.fontHeight * 2.5), this.width, this.textRenderer.fontHeight * 3, 0x00000000, 0x7F000000);
    }
}
