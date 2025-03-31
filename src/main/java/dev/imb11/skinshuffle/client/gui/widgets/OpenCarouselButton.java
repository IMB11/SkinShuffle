package dev.imb11.skinshuffle.client.gui.widgets;

import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.gui.GeneratedScreens;
import dev.imb11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;

public class OpenCarouselButton extends ButtonWidget {
    private final SkinPreset selectedPreset;
    private final double currentTime;

    public OpenCarouselButton(int x, int y, int width, int height) {
        super(x, y, width, height, Text.translatable("skinshuffle.button"), (btn) -> {
            var client = MinecraftClient.getInstance();
            client.setScreen(GeneratedScreens.getCarouselScreen(client.currentScreen));
        }, textSupplier -> Text.empty());

        this.currentTime = GlfwUtil.getTime();
        this.selectedPreset = SkinPresetManager.getChosenPreset();
    }

//    public void setSelectedPreset(SkinPreset preset) {
//        this.selectedPreset = preset;
//
//        if (selectedPreset != null) {
//            this.selectedPreset.getSkin().getTexture();
//        }
//    }

    private float getEntityRotation() {
        return (float) ((GlfwUtil.getTime() - currentTime) * 35.0f);
    }


    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        if (selectedPreset != null) {
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

                context.getMatrices().push();
                GuiEntityRenderer.drawEntity(
                        context.getMatrices(), this.getX() + (this.getWidth() / 2), this.getY() + 5,
                        45, rotation, followX, followY, this.selectedPreset.getSkin(), renderStyle
                );
                context.getMatrices().pop();
            } else {
                // Make sure to call getTexture anyway, otherwise the skin will never load
                selectedPreset.getSkin().getTexture();
            }
        }
    }
}
