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

import dev.imb11.skinshuffle.client.gui.CarouselScreen;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractCardWidget<S extends CarouselScreen> extends SpruceContainerWidget {
    protected final S parent;
    public double lastX;
    public double lastY;
    public double lastPositionTime = Double.MIN_VALUE;
    private Position position;

    private boolean dragging;
    private double dragStartX;
    private double dragStartY;

    public AbstractCardWidget(Position position, int width, int height, S parent) {
        super(position, width, height);
        this.position = position;
        this.parent = parent;
    }

    public int getDeltaX(int x) {
        var deltaTime = (GlfwUtil.getTime() - lastPositionTime) * 5;
        deltaTime = MathHelper.clamp(deltaTime, 0, 1);
        deltaTime = Math.sin(deltaTime * Math.PI / 2);
        return (int) MathHelper.lerp(deltaTime, lastX, x);
    }

    public int getDeltaY(int y) {
        var deltaTime = (GlfwUtil.getTime() - lastPositionTime) * 5;
        deltaTime = MathHelper.clamp(deltaTime, 0, 1);
        deltaTime = Math.sin(deltaTime * Math.PI / 2);
        return (int) MathHelper.lerp(deltaTime, lastY, y);
    }

    public void refreshLastPosition() {
        this.lastX = getDeltaX(position.getRelativeX());
        this.lastY = getDeltaY(position.getRelativeY());
        this.lastPositionTime = GlfwUtil.getTime();
    }

    public abstract boolean isMovable();

    public void refreshState() {
    }

    public void updateVisibility(int index) {
    }

    public void overridePosition(Position newPosition) {
        this.position = newPosition;
    }

    public void overrideDimensions(int newWidth, int newHeight) {
        this.width = newWidth;
        this.height = newHeight;
    }

    @Override
    public int getX() {
        return this.position.getX();
    }

    @Override
    public int getY() {
        return this.position.getY();
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    public void setDragStart(double x, double y) {
        this.dragStartX = x;
        this.dragStartY = y;
    }

    @Override
    public boolean isDragging() {
        return dragging;
    }

    public double getDragStartX() {
        return dragStartX;
    }

    public double getDragStartY() {
        return dragStartY;
    }

    public int getIndex() {
        return this.parent.carouselWidgets.indexOf(this);
    }
}
