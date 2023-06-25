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
