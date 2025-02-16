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

package dev.imb11.skinshuffle.client.gui.widgets;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class IconButtonWidget extends ButtonWidget {
    protected final Identifier iconTexture;
    protected final int iconU;
    protected final int iconV;
    protected final int iconDisabledVOffset;
    protected final int iconTextureWidth;
    protected final int iconTextureHeight;
    private final int iconXOffset;
    private final int iconYOffset;
    public final int iconWidth;
    public final int iconHeight;

    public IconButtonWidget(int x, int y, int width, int height, int iconU, int iconV, int iconXOffset, int iconYOffset, int iconDisabledVOffset, int iconWidth, int iconHeight, int iconTextureWidth, int iconTextureHeight, Identifier iconTexture, ButtonWidget.PressAction onPress) {
        super(x, y, width, height, Text.of(""), onPress, DEFAULT_NARRATION_SUPPLIER);
        this.iconTextureWidth = iconTextureWidth;
        this.iconTextureHeight = iconTextureHeight;
        this.iconU = iconU;
        this.iconV = iconV;
        this.iconDisabledVOffset = iconDisabledVOffset;
        this.iconTexture = iconTexture;
        this.iconXOffset = iconXOffset;
        this.iconYOffset = iconYOffset;
        this.iconWidth = iconWidth;
        this.iconHeight = iconHeight;
    }

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);

        // Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int textureWidth, int textureHeight, int color
        context.drawTexture(
                //? >=1.21.2 {
                RenderLayer::getGuiTextured,
                //?}
                this.iconTexture,
                this.getIconX(),
                this.getIconY(),
                this.iconU,
                this.iconV + (active ? (hovered ? 16 : 0) : this.iconDisabledVOffset),
                //? <1.21.2 {
                /*0,
                *///?}
                this.iconWidth,
                this.iconHeight,
                this.iconTextureWidth,
                this.iconTextureHeight
        );
    }

    @Override
    public void drawMessage(DrawContext context, TextRenderer textRenderer, int color) {
        int i = this.getX() + 2;
        int j = this.getX() + this.getWidth() - this.iconWidth - 6;
        drawScrollableText(context, textRenderer, this.getMessage(), i, this.getY(), j, this.getY() + this.getHeight(), color);
    }

    int getIconX() {
        return this.getX() + (this.width / 2 - this.iconWidth / 2) + this.iconXOffset;
    }

    int getIconY() {
        return this.getY() + this.iconYOffset;
    }
}