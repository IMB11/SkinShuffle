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

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.gui.CarouselScreen;
import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.widget.SpruceButtonWidget;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.GlfwUtil;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class AddCardWidget extends AbstractCardWidget {
//    private final DummyClientPlayerEntity entity;
    private Runnable action;

    public AddCardWidget(CarouselScreen parent, Position position, int width, int height) {
        super(position, width, height, parent);

        addChild(new SpruceButtonWidget(Position.of(3, getHeight() - 24), width - 6, 20, Text.translatable("skinshuffle.carousel.create"), button -> {
            action.run();
        }));
    }

    public void setCallback(@Nullable Runnable action) {
        this.action = action;
    }

    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        for (SpruceWidget child : this.children()) {
            child.setActive(active);
        }
    }

    @Override
    protected void renderBackground(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawBorder(getX(), getY(), getWidth(), getHeight(), this.active ? 0xDF000000 : 0x5F000000);
        graphics.fill(getX() + 1, getY() + 1, getX() + getWidth() - 1, getY() + getHeight() - 1, this.active ? 0x7F000000 : 0x0D000000);
        graphics.drawTexture(SkinShuffle.id("textures/gui/carousel_add.png"), getX() + (this.getWidth() / 2) - 16, getY() + (this.getHeight() / 2) - 16, 0, !this.active ? 32 : 0, 32, 32, 32, 64);

        var text = Text.translatable("skinshuffle.carousel.new");
        graphics.drawTextWithShadow(this.client.textRenderer, text, getX() + (this.width / 2) - this.client.textRenderer.getWidth(text) / 2, getY() + this.client.textRenderer.fontHeight / 2, this.active ? 0xFFFFFFFF : 0xFF808080);
    }

    private float getEntityRotation() {
        return isActive() ? (float) (GlfwUtil.getTime() - parent.getLastCardSwitchTime()) * 35.0f : 0.0f;
    }

    @Override
    public boolean isMovable() {
        return false;
    }
}
