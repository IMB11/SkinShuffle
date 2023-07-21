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

import dev.lambdaurora.spruceui.Position;
import dev.lambdaurora.spruceui.Tooltip;
import dev.lambdaurora.spruceui.widget.SpruceIconButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ActualSpruceIconButtonWidget extends SpruceIconButtonWidget {
    private final Function<ActualSpruceIconButtonWidget, Identifier> iconTexture;

    public ActualSpruceIconButtonWidget(Position position, int width, int height, Text message, PressAction action, Function<ActualSpruceIconButtonWidget, Identifier> iconTexture) {
        super(position, width, height, message, action);
        this.iconTexture = iconTexture;
    }

    @Override
    protected int renderIcon(DrawContext graphics, int mouseX, int mouseY, float delta) {
        graphics.drawTexture(iconTexture.get(), this.getX() + this.getWidth() / 2 - (16 / 2), this.getY() + this.getHeight() / 2 - (16 / 2), 16, 16, 0, isMouseHovered() ? 16 : 0, 16, 16, 16, 32);
        return 16;
    }

    @Override
    protected void renderWidget(DrawContext graphics, int mouseX, int mouseY, float delta) {
        this.renderButton(graphics, mouseX, mouseY, delta);

        var tooltip = getTooltip();
        if (!this.dragging && isMouseHovered() && tooltip.isPresent()) {
            Tooltip.create(mouseX, mouseY, List.of(tooltip.get().asOrderedText())).queue();
        }
    }
}
