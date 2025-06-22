package dev.imb11.skinshuffle.client.gui.widgets;

import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.gui.GeneratedScreens;
import dev.imb11.skinshuffle.client.gui.renderer.SkinPreviewRenderer;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class OpenCarouselButton extends ButtonWidget {
    private final SkinPreset selectedPreset;
    private final SkinPreviewRenderer renderer;

    public OpenCarouselButton(int x, int y, int width, int height) {
        super(x, y, width, height, Text.translatable("skinshuffle.button"), (btn) -> {
            var client = MinecraftClient.getInstance();
            client.setScreen(GeneratedScreens.getCarouselScreen(client.currentScreen));
        }, textSupplier -> Text.empty());

        this.selectedPreset = SkinPresetManager.getChosenPreset();
        this.renderer = new SkinPreviewRenderer(MinecraftClient.getInstance());
    }

    @Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        if (selectedPreset != null) {
            // Create a rectangular area above the button with 1:3 ratio (width:height)
            int skinWidth = 60;  // Width of the skin preview area
            int skinHeight = 120; // Height of the skin preview area (3x width for 1:3 ratio)
            
            int skinCenterX = getX() + getWidth() / 2;
            
            // Position the skin area above the button
            int x1 = skinCenterX - skinWidth / 2;
            int x2 = skinCenterX + skinWidth / 2;
            int y2 = getY() - 5; // 5 pixels above the button
            int y1 = y2 - skinHeight; // Extend upward by skinHeight

            renderer.renderSkinPreview(
                    context,
                    selectedPreset,
                    mouseX, mouseY,
                    x1, y1, x2, y2,
                    0.8f,
                    SkinShuffleConfig.get().widgetSkinRenderStyle,
                    false
            );
        }
    }
}