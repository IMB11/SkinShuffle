package dev.imb11.skinshuffle.client.gui.renderer;

import dev.imb11.skinshuffle.client.SkinShuffleClient;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;

/**
 * Utility class for rendering skin previews in UI screens.
 */
public class SkinPreviewRenderer {
    
    private final MinecraftClient client;
    
    /**
     * Constructor that accepts a MinecraftClient instance.
     */
    public SkinPreviewRenderer(MinecraftClient client) {
        this.client = client;
    }
    
    /**
     * Renders a preview area for a skin with a border.
     */
    public void renderPreviewArea(DrawContext graphics, int previewCenterX, int previewCenterY,
                                int previewSpanX, int previewSpanY) {
        graphics.drawBorder(previewCenterX - previewSpanX, previewCenterY - previewSpanY,
                previewSpanX * 2, previewSpanY * 2, 0xDF000000);
        graphics.fill(previewCenterX - previewSpanX + 1, previewCenterY - previewSpanY + 1,
                previewCenterX + previewSpanX - 1, previewCenterY + previewSpanY - 1, 0x7F000000);
    }
    
    /**
     * Renders a skin preview with the given parameters.
     */
    public void renderSkinPreview(DrawContext graphics, SkinPreset preset, int mouseX, int mouseY,
                                 int previewCenterX, int previewCenterY, int previewSpanY,
                                 SkinShuffleConfig.SkinRenderStyle renderStyle, boolean isLoading) {
        if (preset.getSkin().isLoading() || isLoading) {
            renderLoadingIndicator(graphics, previewCenterX, previewCenterY);
            // Still try to load the texture in the background
            if (preset.getSkin().isLoading()) {
                preset.getSkin().getTexture();
            }
            return;
        }

        var entityX = previewCenterX;
        var entityY = (previewCenterY + previewSpanY / 10 * 8) + 30;

        float followX = entityX - mouseX;
        float followY = entityY - previewSpanY * 1.2f - mouseY - 10f;
        float rotation = 0;

        if (renderStyle.equals(SkinShuffleConfig.SkinRenderStyle.ROTATION)) {
            followX = 0;
            followY = 0;
            rotation = getEntityRotation() * SkinShuffleConfig.get().rotationMultiplier;
        }

        graphics.getMatrices().push();
        GuiEntityRenderer.drawEntity(
                graphics.getMatrices(), entityX, entityY, previewSpanY / 10 * 8,
                rotation, followX, followY, preset.getSkin(), renderStyle
        );
        graphics.getMatrices().pop();
    }
    
    /**
     * Renders a loading indicator with rainbow color effect.
     */
    public void renderLoadingIndicator(DrawContext graphics, int centerX, int centerY) {
        TextRenderer textRenderer = client.textRenderer;
        var txt = Text.translatable("skinshuffle.edit.loading");
        int textWidth = textRenderer.getWidth(txt);
        float totalDeltaTick = SkinShuffleClient.TOTAL_TICK_DELTA * 5f;
        float hue = (totalDeltaTick % 360) / 360;
        float saturation = 0.75f;
        float lightness = 1f;
        int color = java.awt.Color.HSBtoRGB(hue, saturation, lightness);
        color = (color & 0x00FFFFFF) | 0xFF000000;
        graphics.drawTextWithShadow(textRenderer, txt, centerX - (textWidth / 2), centerY - (textRenderer.fontHeight), color);
    }
    
    /**
     * Get the current rotation for entities based on time.
     */
    public static float getEntityRotation() {
        return (float) GlfwUtil.getTime() * 35f;
    }
}