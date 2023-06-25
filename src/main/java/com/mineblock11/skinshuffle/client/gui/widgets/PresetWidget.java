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

package com.mineblock11.skinshuffle.client.gui.widgets;

import com.mineblock11.skinshuffle.client.gui.SkinCarouselScreen;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.container.SpruceContainerWidget;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.util.math.MathHelper;

public abstract class PresetWidget extends SpruceContainerWidget {
    protected final SkinCarouselScreen parent;
    public double lastIndex;
    public double lastIndexTime;

    public PresetWidget(Position position, int width, int height, SkinCarouselScreen parent) {
        super(position, width, height);
        this.parent = parent;
    }

    public double getDeltaIndex() {
        var deltaTime = (GlfwUtil.getTime() - lastIndexTime) * 5;
        deltaTime = MathHelper.clamp(deltaTime, 0, 1);
        deltaTime = Math.sin(deltaTime * Math.PI / 2);
        return MathHelper.lerp(deltaTime, lastIndex, parent.carouselWidgets.indexOf(this));
    }

    public void refreshLastIndex() {
        this.lastIndex = getDeltaIndex();
        this.lastIndexTime = GlfwUtil.getTime();
    }

    public abstract boolean isMovable();

    public void refreshState() {
    }
}
