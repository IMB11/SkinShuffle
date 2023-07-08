/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

package com.mineblock11.skinshuffle.client.gui.widgets.preset;

import com.mineblock11.skinshuffle.client.gui.CarouselScreen;
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
