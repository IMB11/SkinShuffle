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

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.gui.GeneratedScreens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WarningIndicatorButton extends IconButtonWidget {
    @Override
    public Text getMessage() {
        return Text.translatable("skinshuffle.indicator");
    }

    public WarningIndicatorButton(int x, int y, Screen parent) {
        super(x, y, 20, 20,
                0, 0, 0, 2,
                16, 16, 16, 16, 32,
                SkinShuffle.id("textures/gui/warning-icon.png"),
                button -> {
                    var client = MinecraftClient.getInstance();
                    client.setScreen(GeneratedScreens.getReconnectScreen(parent));
                }
        );

        var client = MinecraftClient.getInstance();

        this.setTooltip(Tooltip.of(Text.literal(I18n.translate("skinshuffle.reconnect.warning",
                client.isInSingleplayer() ? I18n.translate("skinshuffle.reconnect.rejoin") : I18n.translate("skinshuffle.reconnect.reconnect"))).formatted(Formatting.RED, Formatting.BOLD)));
    }

    /*? if <1.20.4 {*/
    /*@Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderButton(context, mouseX, mouseY, delta);
    *//*?} else {*/@Override
    public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        super.renderWidget(context, mouseX, mouseY, delta);
    /*?}*/

        context.drawTexture(
                //? >=1.21.2 {
                RenderLayer::getGuiTextured,
                //?}
                this.iconTexture,
                this.getIconX(),
                this.getIconY(),
                this.iconU,
                this.iconV + (active ? (hovered ? 16 : 0) : this.iconDisabledVOffset),
                0,
                this.iconWidth,
                this.iconHeight,
                this.iconTextureWidth,
                this.iconTextureHeight);
    }
}