/*
 * ALL RIGHTS RESERVED
 *
 * Copyright (c) 2024 Calum H. (IMB11) and enjarai
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package dev.imb11.skinshuffle.client.gui.widgets.preset;

import dev.imb11.skinshuffle.client.config.SkinPresetManager;
import dev.imb11.skinshuffle.client.config.SkinShuffleConfig;
import dev.imb11.skinshuffle.client.gui.CarouselScreen;
import dev.imb11.skinshuffle.client.gui.CompactCarouselScreen;
import dev.imb11.skinshuffle.client.gui.PresetEditScreen;
import dev.imb11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import dev.imb11.skinshuffle.client.gui.widgets.VariableSpruceButtonWidget;
import dev.imb11.skinshuffle.client.preset.SkinPreset;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;

public abstract class PresetWidget<S extends CarouselScreen> extends AbstractCardWidget<S> {
    protected final SkinPreset skinPreset;
    protected VariableSpruceButtonWidget editButton;
    protected VariableSpruceButtonWidget copyButton;
    protected VariableSpruceButtonWidget deleteButton;
    private final boolean showButtons;
    protected double scaleFactor;

    public PresetWidget(S parent, SkinPreset skinPreset) {
        super(Position.of(0, 0), parent.getCardWidth(), parent.getCardHeight(), parent);

        this.skinPreset = skinPreset;
        this.skinPreset.getSkin().getTexture();

        this.showButtons = true;

        if (showButtons) {
            this.editButton = new VariableSpruceButtonWidget(
                    Position.of(0, 0), 0, 0,
                    Text.translatable("skinshuffle.carousel.preset_widget.edit"),
                    button -> client.setScreen(new PresetEditScreen(this, this.parent, this.skinPreset))
            );

            this.copyButton = new VariableSpruceButtonWidget(
                    Position.of(0, 0), 0, 0,
                    Text.translatable("skinshuffle.carousel.preset_widget.copy"),
                    button -> {
                        SkinPreset presetCopy = this.skinPreset.copy();
                        presetCopy.setName(this.skinPreset.getName() + " (Copy)");
                        SkinPresetManager.addPreset(presetCopy);
                        this.parent.refresh();
                    }
            );

            this.deleteButton = new VariableSpruceButtonWidget(
                    Position.of(0, 0), 0, 0,
                    Text.translatable("skinshuffle.carousel.preset_widget.delete"),
                    button -> {
                        ConfirmScreen confirmScreen = new ConfirmScreen(result -> {
                            if(result) {
                                SkinPresetManager.deletePreset(this.skinPreset);
                            }
                            this.parent.refresh();
                            this.client.setScreen(this.parent);
                        }, Text.translatable("skinshuffle.carousel.confirmations.delete_preset.title"), Text.translatable("skinshuffle.carousel.confirmations.delete_preset.message"));
                        this.client.setScreen(confirmScreen);
                    }
            );

            if(SkinPresetManager.getLoadedPresets().size() < 2) this.deleteButton.setActive(false);

            addChild(deleteButton);
            addChild(editButton);
            addChild(copyButton);
        }
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);

        for (SpruceWidget child : this.children()) {
            if(child.equals(this.deleteButton) && SkinPresetManager.getLoadedPresets().size() < 2) continue;
            child.setActive(active);
        }
    }

    @Override
    protected void renderBackground(DrawContext graphics, int mouseX, int mouseY, float delta) {
        int borderColour = this.active ? 0xDF000000 : 0x5F000000;

        if (SkinPresetManager.getChosenPreset().equals(this.skinPreset)) {
            borderColour = this.active ? 0xDF0096FF : 0x5F0096FF;
        } else if (SkinPresetManager.getApiPreset() != null) {
            if (SkinPresetManager.getApiPreset().equals(this.skinPreset)) {
                borderColour = this.active ? 0xDF00FF00 : 0x5F00FF00;
            }
        }

        graphics.drawBorder(getX(), getY(), getWidth(), getHeight(), borderColour);
        graphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, this.active ? 0x7F000000 : 0x0D000000);
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        super.renderWidget(graphics, mouseX, mouseY, delta);

        var margin = this.client.textRenderer.fontHeight / 2;
        var name = this.skinPreset.getName() != null ? this.skinPreset.getName() : "Unnamed Preset";
        var nameWidth = this.client.textRenderer.getWidth(name);
        var halfWidth = this.width / 2;
        var halfNameWidth = nameWidth / 2;
        ClickableWidget.drawScrollableText(
                graphics, this.client.textRenderer,
                Text.of(name),
                getX() + halfWidth - Math.min(halfWidth - margin, halfNameWidth), getY() + margin,
                getX() + halfWidth + Math.min(halfWidth - margin, halfNameWidth), getY() + margin + this.client.textRenderer.fontHeight,
                this.active ? 0xFFFFFFFF : 0xFF808080
        );

        var previewX = getPreviewX();
        var previewY = getPreviewY();

        float followX = (float) previewX - mouseX;
        float followY = (float) (previewY - this.height / 40 * 16) - mouseY - 20f;
        float rotation = 0;

        SkinShuffleConfig.SkinRenderStyle renderStyle = SkinShuffleConfig.get().carouselSkinRenderStyle;

        if(renderStyle.equals(SkinShuffleConfig.SkinRenderStyle.ROTATION)) {
            followX = 0;
            followY = 0;
            rotation = getEntityRotation() * SkinShuffleConfig.get().rotationMultiplier;
        }

        if(!this.active) {
            followX = 0;
            followY = 0;
            rotation = 0;
        }

        if(!this.skinPreset.getSkin().isLoading()) {
            graphics.getMatrices().push();
            GuiEntityRenderer.drawEntity(
                    graphics.getMatrices(), previewX, previewY,
                    getPreviewSize(), rotation, followX, followY,  this.skinPreset.getSkin(), renderStyle
            );
            graphics.getMatrices().pop();
        }
    }

    protected int getPreviewX() {
        return getX() + this.getWidth() / 2;
    }

    protected int getPreviewY() {
        return (int) (this.getY() + (showButtons ? this.height / 1.6 : this.height * (1.5 / 2))) + 20;
    }

    protected int getPreviewSize() {
        return this.height / 4;
    }

    public void setScaleFactor(double scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    private float getEntityRotation() {
        return active && SkinShuffleConfig.get().carouselSkinRenderStyle.equals(SkinShuffleConfig.SkinRenderStyle.ROTATION) ? (float) (GlfwUtil.getTime() - (parent != null && !(parent instanceof CompactCarouselScreen) ? parent.getLastCardSwitchTime() : 0)) * 35.0f : 0.0f;
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
