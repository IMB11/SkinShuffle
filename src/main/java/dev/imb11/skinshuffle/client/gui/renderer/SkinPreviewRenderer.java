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

    public SkinPreviewRenderer(MinecraftClient client) {
        this.client = client;
    }

    public static float getEntityRotation() {
        return (float) GlfwUtil.getTime() * 35f;
    }

    /**
     * Draws a bordered preview region.
     */
    public void renderPreviewArea(DrawContext graphics, int previewCenterX, int previewCenterY,
                                  int previewSpanX, int previewSpanY) {
        graphics.drawBorder(previewCenterX - previewSpanX, previewCenterY - previewSpanY,
                previewSpanX * 2, previewSpanY * 2, 0xDF000000);
        graphics.fill(previewCenterX - previewSpanX + 1, previewCenterY - previewSpanY + 1,
                previewCenterX + previewSpanX - 1, previewCenterY + previewSpanY - 1, 0x7F000000);
    }

    public void renderSkinPreview(DrawContext graphics, SkinPreset preset,
                                  int mouseX, int mouseY,
                                  int x1, int y1, int x2, int y2, float sizeScaling,
                                  SkinShuffleConfig.SkinRenderStyle renderStyle,
                                  boolean isLoading) {
        this.renderSkinPreview(graphics, preset, mouseX, mouseY, x1, y1, x2, y2, sizeScaling, renderStyle, isLoading, 1.0f);
    }

    /**
     * Renders the given {@link SkinPreset} in the rectangle [x1 ,y1]–[x2 ,y2].
     *
     * @param graphics    Draw context
     * @param preset      Skin to draw
     * @param mouseX      Cursor X (for follow-camera mode)
     * @param mouseY      Cursor Y (for follow-camera mode)
     * @param x1          Left edge of the render box
     * @param y1          Top edge of the render box
     * @param x2          Right edge of the render box
     * @param y2          Bottom edge of the render box
     * @param sizeScaling The scaling to apply to the calculated sizing.
     * @param renderStyle Style (STATIC / FOLLOW / ROTATION)
     * @param isLoading   Whether the calling screen is still fetching a texture
     */
    public void renderSkinPreview(DrawContext graphics, SkinPreset preset,
                                  int mouseX, int mouseY,
                                  int x1, int y1, int x2, int y2, float sizeScaling,
                                  SkinShuffleConfig.SkinRenderStyle renderStyle,
                                  boolean isLoading, float alpha) {

        // If the skin (or the screen) is still loading, just show the spinner.
        if (preset.getSkin().isLoading() || isLoading) {
            int centerX = (x1 + x2) / 2;
            int centerY = (y1 + y2) / 2;
            renderLoadingIndicator(graphics, centerX, centerY);

            // Kick the async texture load once per frame.
            if (preset.getSkin().isLoading()) {
                preset.getSkin().getTexture();
            }
            return;
        }

        int size = Math.min(x2 - x1, y2 - y1);
        // Scale up the size a bit to ensure the full model is visible
        size = (int) (size * sizeScaling);
        float rotation = 180;

        if (SkinShuffleConfig.SkinRenderStyle.ROTATION.equals(renderStyle)) {
            rotation = getEntityRotation() * SkinShuffleConfig.get().rotationMultiplier;
        }

        // Pass the raw mouse coordinates - let GuiEntityRenderer handle the mouse calculations
        GuiEntityRenderer.drawEntity(
                graphics, x1, y1, x2, y2, size,
                rotation, mouseX, mouseY, preset.getSkin(), renderStyle, alpha
        );
    }

    public void renderLoadingIndicator(DrawContext graphics, int centerX, int centerY) {
        TextRenderer textRenderer = client.textRenderer;
        Text txt = Text.translatable("skinshuffle.edit.loading");

        int textWidth = textRenderer.getWidth(txt);
        float totalDeltaTick = SkinShuffleClient.TOTAL_TICK_DELTA * 5f;
        float hue = (totalDeltaTick % 360) / 360f;

        int color = java.awt.Color.HSBtoRGB(hue, 0.75f, 1f) | 0xFF000000;
        graphics.drawTextWithShadow(textRenderer, txt,
                centerX - textWidth / 2, centerY - textRenderer.fontHeight - 80, color);
    }
}
