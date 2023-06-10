package com.mineblock11.skinshuffle.client.gui;

import com.mineblock11.skinshuffle.client.gui.widgets.CarouselMoveButton;
import com.mineblock11.skinshuffle.client.gui.widgets.SkinPresetWidget;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import com.mineblock11.skinshuffle.client.skin.UrlSkin;
import com.mineblock11.skinshuffle.client.skin.source.SkinDexSource;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.screen.SpruceScreen;
import dev.lambdaurora.spruceui.util.ScissorManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;

public class SkinCarouselScreen extends SpruceScreen {
    public SkinCarouselScreen() {
        super(Text.translatable("skinshuffle.gui.skincarousel"));
    }

    public CarouselMoveButton leftMoveButton;
    public CarouselMoveButton rightMoveButton;
    private int cardIndex = 0;
    private double lastCardIndex = 0;
    private double lastCardSwitchTime = 0;
    public ArrayList<SkinPresetWidget> loadedPresets = new ArrayList<>();

    @Override
    protected void init() {
        super.init();
        loadedPresets.clear();

        leftMoveButton = new CarouselMoveButton(Position.of((getCardWidth() / 2), (this.height / 2) - 8), false);
        rightMoveButton = new CarouselMoveButton(Position.of(this.width - (getCardWidth() / 2), (this.height / 2) - 8), true);

        leftMoveButton.setCallback(() -> {
            var cardIndex = (this.cardIndex - 1 + (this.loadedPresets.size())) % (this.loadedPresets.size());
            if (cardIndex < 0) {
                cardIndex = this.loadedPresets.size() - 1;
            }
            setCardIndex(cardIndex);
        });
        rightMoveButton.setCallback(() -> {
            var cardIndex = (this.cardIndex + 1) % (this.loadedPresets.size());
            if (cardIndex < 0) {
                cardIndex = this.loadedPresets.size();
            }
            setCardIndex(cardIndex);
        });

        var preset = new SkinPreset(new UrlSkin("https://www.minecraftskins.com/uploads/skins/2023/06/06/among-us-character-21667114.png?v577", "default"));
        preset.setName("sus");
        loadPreset(preset);
        var preset2 = new SkinPreset(new UrlSkin("https://s.namemc.com/i/2b931e86a910f916.png", "default"));
        preset2.setName("w a t");
        loadPreset(preset2);
        var preset3 = new SkinPreset(new UrlSkin("https://s.namemc.com/i/37529af66bcdd70d.png", "default"));
        preset3.setName("Technoblade");
        loadPreset(preset3);

        this.addSelectableChild(leftMoveButton);
        this.addSelectableChild(rightMoveButton);
    }

    @Override
    public void render(DrawContext graphics, int mouseX, int mouseY, float delta) {
        var cardAreaWidth = getCardWidth() + getCardGap();

        // BG stuff
        this.renderBackground(graphics);
        graphics.fill(0, this.textRenderer.fontHeight * 3, this.width, this.height - (this.textRenderer.fontHeight * 3), 0x7F000000);
        ScissorManager.pushScaleFactor(this.scaleFactor);

        // Carousel Widgets
        double deltaIndex = getDeltaCardIndex();
        int xOffset = (int) ((-deltaIndex + 1) * cardAreaWidth);
        int currentX = this.width / 2 - cardAreaWidth - getCardWidth() / 2;
        for (SkinPresetWidget loadedPreset : this.loadedPresets) {
//            graphics.drawTextWithShadow(this.textRenderer, String.valueOf(loadedPresets.indexOf(loadedPreset)), currentX + xOffset, this.height/2 - this.textRenderer.fontHeight /2 , 0xFFFFFFFF);
            loadedPreset.overridePosition(Position.of(currentX + xOffset, (this.height / 2) - (getCardHeight() / 2)));

            loadedPreset.setActive(cardIndex == this.loadedPresets.indexOf(loadedPreset));
            loadedPreset.setScaleFactor(this.scaleFactor);

            loadedPreset.render(graphics, mouseX, mouseY, delta);
            currentX += cardAreaWidth;
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

    private int getCardWidth() {
        return this.width / 4;
    }

    private int getCardHeight() {
        return (int) (this.height / 1.5);
    }

    private int getCardGap() {
        return (int) (10 * this.scaleFactor);
    }

    private double getDeltaCardIndex() {
        var deltaTime = (GlfwUtil.getTime() - lastCardSwitchTime) * 5;
        deltaTime = MathHelper.clamp(deltaTime, 0, 1);
        deltaTime = Math.sin(deltaTime * Math.PI / 2);
        return MathHelper.lerp(deltaTime, lastCardIndex, cardIndex);
    }

    public void loadPreset(SkinPreset preset) {
        this.loadedPresets.add(new SkinPresetWidget(this, getCardWidth(), getCardHeight(), preset));
    }

    public void setCardIndex(int index) {
        lastCardIndex = getDeltaCardIndex();
        lastCardSwitchTime = GlfwUtil.getTime();
        cardIndex = index;
    }

    public double getLastCardSwitchTime() {
        return lastCardSwitchTime;
    }
}
