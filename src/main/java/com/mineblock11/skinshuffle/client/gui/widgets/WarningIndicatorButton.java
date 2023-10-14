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

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.gui.GeneratedScreens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
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

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(this.iconTexture, this.getIconX(), this.getIconY(), this.iconU, this.iconV + (hovered ? 16 : 0), 0, this.iconWidth, this.iconHeight, this.iconTextureWidth, this.iconTextureHeight);
    }
}
