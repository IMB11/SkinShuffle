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

package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mineblock11.skinshuffle.client.gui.GeneratedScreens;
import com.mineblock11.skinshuffle.client.gui.cursed.GuiEntityRenderer;
import com.mineblock11.skinshuffle.client.preset.SkinPreset;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;
import nl.enjarai.cicada.api.cursed.DummyClientPlayerEntity;

import java.util.UUID;

public class OpenCarouselButton extends ButtonWidget {
    private SkinPreset selectedPreset;
    private DummyClientPlayerEntity entity;
    private double currentTime = 0;

    public OpenCarouselButton(int x, int y, int width, int height) {
        super(x, y, width, height, Text.translatable("skinshuffle.button"), (btn) -> {
            var client = MinecraftClient.getInstance();
            client.setScreen(GeneratedScreens.getCarouselScreen(client.currentScreen));
        }, textSupplier -> Text.empty());

        currentTime = GlfwUtil.getTime();
    }

    public void disposed() {
        if (this.entity != null) {
            this.entity.kill();
        }
    }

    public void setSelectedPreset(SkinPreset preset) {
        this.selectedPreset = preset;
        this.entity = new DummyClientPlayerEntity(null, UUID.randomUUID(), preset.getSkin().getTexture(), preset.getSkin().getModel());
    }

    private float getEntityRotation() {
        return (float) ((GlfwUtil.getTime() - currentTime) * 35.0f);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        if (this.entity != null && selectedPreset != null) {
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
