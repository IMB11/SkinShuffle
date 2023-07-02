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

public abstract class AbstractCardWidget extends SpruceContainerWidget {
    protected final CarouselScreen parent;
    public double lastX;
    public double lastY;
    public double lastPositionTime;
    private Position position;

    public AbstractCardWidget(Position position, int width, int height, CarouselScreen parent) {
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

    public void refreshLastIndex() {
        this.lastX = getDeltaX(position.getRelativeX());
        this.lastY = getDeltaY(position.getRelativeY());
        this.lastPositionTime = GlfwUtil.getTime();
    }

    public abstract boolean isMovable();

    public void refreshState() {
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
}
