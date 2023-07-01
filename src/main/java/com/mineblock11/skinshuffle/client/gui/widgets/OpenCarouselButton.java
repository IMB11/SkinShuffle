package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.cursed.DummyClientPlayerEntity;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.UUID;
import java.util.function.Supplier;

public class OpenCarouselButton extends ButtonWidget {
    private SkinPreset selectedPreset;
    private DummyClientPlayerEntity entity;
    private double currentTime = 0;

    public OpenCarouselButton(int x, int y, int width, int height) {
        super(x, y, width, height, Text.translatable("skinshuffle.button"), (btn) -> {
            var client = MinecraftClient.getInstance();
            client.setScreen(new SkinCarouselScreen(client.currentScreen));
        }, textSupplier -> null);

        currentTime = GlfwUtil.getTime();
    }

    public void disposed() {
        this.entity.kill();
    }

    public void setSelectedPreset(SkinPreset preset) {
        this.selectedPreset = preset;
        var skin = selectedPreset.getSkin();
        this.entity = new DummyClientPlayerEntity(
                null, UUID.randomUUID(),
                skin::getTexture, skin::getModel
        );
    }

    private float getEntityRotation() {
        return (float) ((GlfwUtil.getTime() - currentTime) * 35.0f);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (this.entity != null) {
            // Don't want to render the entity if the skin is still loading
            if (!selectedPreset.getSkin().isLoading()) {
                float followX = (float) (this.getX() + (this.getWidth() / 2)) - mouseX;
                float followY = (float) (this.getY() - 90) - mouseY;
                float rotation = 0;

                SkinShuffleConfig.SkinRenderStyle renderStyle = SkinShuffleConfig.get().widgetSkinRenderStyle;

                if (renderStyle.equals(SkinShuffleConfig.SkinRenderStyle.ROTATION)) {
                    followX = 0;
                    followY = 0;
                    rotation = getEntityRotation() * SkinShuffleConfig.get().rotationMultiplier;
                }

                GuiEntityRenderer.drawEntity(
                        context.getMatrices(), this.getX() + (this.getWidth() / 2), this.getY() - 12,
                        45, rotation, followX, followY, entity
                );
            } else {
                // Make sure to call getTexture anyway, otherwise the skin will never load
                selectedPreset.getSkin().getTexture();
            }
        }
    }
}
