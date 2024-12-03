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
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.AbstractSpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class CarouselMoveButton extends AbstractSpruceWidget {
    private static Identifier ARROW_TEXTURES = SkinShuffle.id("textures/gui/carousel_arrows.png");
    private final Type type;
    private @Nullable Runnable action;

    public CarouselMoveButton(Position position, boolean isRight) {
        super(position);
        this.width = 16;
        this.height = 16;
        this.type = isRight ? Type.RIGHT : Type.LEFT;
        if (isRight) {
            position.setRelativeX(position.getRelativeX() - width);
        }
    }

    public CarouselMoveButton(Position position, Type type) {
        super(position);
        this.width = type.width * 2;
        this.height = type.height * 2;
        this.type = type;
        position.setRelativeX(position.getRelativeX() - width / 2);
        position.setRelativeY(position.getRelativeY() - height / 2);
    }

    public void setCallback(@Nullable Runnable action) {
        this.action = action;
    }

    @Override
    protected boolean onMouseClick(double mouseX, double mouseY, int button) {
        if(this.action != null) {
            try {
                this.action.run();
                this.playDownSound();
            } catch (Exception e) {
                throw new RuntimeException("Failed to trigger callback for CarouselMoveButton{x=" + getX() + ", y=" + getY() +"}\n" + e);
            }
        }
        return false;
    }

    @Override
    protected void renderWidget(DrawContext guiGraphics, int mouseX, int mouseY, float delta) {
        var matrices = guiGraphics.getMatrices();
        matrices.push();
        // Translate the matrix forward so its above rendered playermodels
        matrices.translate(0, 0, 10000);
        guiGraphics.drawTexture(
                //? >=1.21.2 {
                RenderLayer::getGuiTextured,
                //?}
                ARROW_TEXTURES, getX(), getY(), width, height, this.type.u,
                (this.active ? (this.hovered || this.focused ? this.type.height : 0) : this.type.height),
                this.type.width, this.type.height, 64, 64
        );
        matrices.pop();
    }

    @Override
    protected @Nullable Text getNarrationMessage() {
        return Text.translatable("skinshuffle.carousel." + this.type.name);
    }

    public enum Type {
        LEFT("left", 0),
        RIGHT("right", 8),
        UP("up", 16),
        DOWN("down", 24),
        LEFT_RIGHT("left_right", 32, 16, 16),
        UP_DOWN("up_down", 48, 16, 16);

        public final String name;
        public final int u;
        public final int width;
        public final int height;

        Type(String name, int u) {
            this.name = name;
            this.u = u;
            this.width = 8;
            this.height = 8;
        }

        Type(String name, int u, int width, int height) {
            this.name = name;
            this.u = u;
            this.width = width;
            this.height = height;
        }
    }
}
