package dev.imb11.skinshuffle.client.gui.widgets.presets;

import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.gui.PresetEditScreen;
import dev.imb11.skinshuffle.client.gui.carousels.CarouselScreen;
import dev.imb11.skinshuffle.client.gui.carousels.CompactCarouselScreen;
import dev.imb11.skinshuffle.client.gui.renderer.SkinPreviewRenderer;
import dev.imb11.skinshuffle.client.gui.widgets.buttons.VariableButton;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.render.SpruceGuiGraphics;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;

public abstract class PresetWidget<S extends CarouselScreen> extends AbstractCardWidget<S> {
    protected final SkinPreset skinPreset;
    private final boolean showButtons;
    private final SkinPreviewRenderer renderer;
    protected VariableButton editButton;
    protected VariableButton copyButton;
    protected VariableButton deleteButton;
    protected double scaleFactor;

    public PresetWidget(S parent, SkinPreset skinPreset) {
        super(Position.of(0, 0), parent.getCardWidth(), parent.getCardHeight(), parent);

        this.skinPreset = skinPreset;
        this.skinPreset.getSkin().getTexture();
        this.renderer = new SkinPreviewRenderer(client);

        this.showButtons = true;

        if (showButtons) {
            this.editButton = new VariableButton(
                    Position.of(0, 0), 0, 0,
                    Text.translatable("skinshuffle.carousel.preset_widget.edit"),
                    button -> client.setScreen(new PresetEditScreen(this, this.parent, this.skinPreset))
            );

            this.copyButton = new VariableButton(
                    Position.of(0, 0), 0, 0,
                    Text.translatable("skinshuffle.carousel.preset_widget.copy"),
                    button -> {
                        SkinPreset presetCopy = this.skinPreset.copy();
                        presetCopy.setName(this.skinPreset.getName() + " (Copy)");
                        SkinPresetManager.addPreset(presetCopy);
                        this.parent.refresh();
                    }
            );

            this.deleteButton = new VariableButton(
                    Position.of(0, 0), 0, 0,
                    Text.translatable("skinshuffle.carousel.preset_widget.delete"),
                    button -> {
                        ConfirmScreen confirmScreen = new ConfirmScreen(result -> {
                            if (result) {
                                SkinPresetManager.deletePreset(this.skinPreset);
                            }
                            this.parent.refresh();
                            this.client.setScreen(this.parent);
                        }, Text.translatable("skinshuffle.carousel.confirmations.delete_preset.title"), Text.translatable("skinshuffle.carousel.confirmations.delete_preset.message"));
                        this.client.setScreen(confirmScreen);
                    }
            );

            if (SkinPresetManager.getLoadedPresets().size() < 2) this.deleteButton.setActive(false);

            addChild(deleteButton);
            addChild(editButton);
            addChild(copyButton);
        }
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        for (SpruceWidget child : this.children()) {
            if (child.equals(this.deleteButton) && SkinPresetManager.getLoadedPresets().size() < 2) continue;
            child.setActive(active);
        }
    }

    @Override
    protected void renderBackground(SpruceGuiGraphics graphics, int mouseX, int mouseY, float delta) {
        int borderColour = this.active ? 0xDF000000 : 0x5F000000;

        if (SkinPresetManager.getChosenPreset().equals(this.skinPreset)) {
            borderColour = this.active ? 0xDF0096FF : 0x5F0096FF;
        } else if (SkinPresetManager.getApiPreset() != null) {
            if (SkinPresetManager.getApiPreset().equals(this.skinPreset)) {
                borderColour = this.active ? 0xDF00FF00 : 0x5F00FF00;
            }
        }

        this.drawBorder(graphics.vanilla(), getX(), getY(), getWidth(), getHeight(), borderColour);
        graphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, this.active ? 0x7F000000 : 0x0D000000);
    }

    public void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    @Override
    protected void renderWidget(SpruceGuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(graphics, mouseX, mouseY, delta);

        // Render name
        var margin = this.client.textRenderer.fontHeight / 2;
        var name = this.skinPreset.getName() != null ? this.skinPreset.getName() : "Unnamed Preset";
        var nameWidth = this.client.textRenderer.getWidth(name);
        var halfWidth = this.width / 2;
        var halfNameWidth = nameWidth / 2;
        ClickableWidget.drawScrollableText(
                graphics.vanilla(), this.client.textRenderer,
                Text.of(name),
                getX() + halfWidth - Math.min(halfWidth - margin, halfNameWidth), getY() + margin,
                getX() + halfWidth + Math.min(halfWidth - margin, halfNameWidth), getY() + margin + this.client.textRenderer.fontHeight,
                this.active ? 0xFFFFFFFF : 0xFF808080
        );        // Get render style
        SkinShuffleConfig.SkinRenderStyle renderStyle =
                SkinShuffleConfig.get().carouselSkinRenderStyle;

        // Get the preview bounds from the subclass
        int[] bounds = getPreviewBounds();
        int x1 = bounds[0];
        int y1 = bounds[1];
        int x2 = bounds[2];
        int y2 = bounds[3];

        renderer.renderSkinPreview(
                graphics.vanilla(),
                this.skinPreset,
                mouseX, mouseY,
                x1, y1, x2, y2,
                getPreviewScaling(),
                renderStyle,
                false
        );
    }

    protected abstract float getPreviewScaling();

    /**
     * Get the rectangular area where the skin preview should be rendered.
     * Subclasses should override this to define their preview area.
     * @return array of [x1, y1, x2, y2] representing the preview bounds
     */
    protected abstract int[] getPreviewBounds();

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public SkinPreset getPreset() {
        return this.skinPreset;
    }

    @Override
    public ScreenRect getNavigationFocus() {
        return super.getNavigationFocus();
    }

    @Override
    public void refreshState() {
        super.refreshState();
    }

    @Override
    public boolean isMovable() {
        return true;
    }
}