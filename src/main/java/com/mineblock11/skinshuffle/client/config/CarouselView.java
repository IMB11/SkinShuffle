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

package com.mineblock11.skinshuffle.client.config;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.gui.CarouselScreen;
import com.mineblock11.skinshuffle.client.gui.CompactCarouselScreen;
import com.mineblock11.skinshuffle.client.gui.LargeCarouselScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public enum CarouselView {
    LARGE(LargeCarouselScreen::new, SkinShuffle.id("textures/gui/large-view-button.png"),
            Text.translatable("skinshuffle.carousel.view_type_button.large.tooltip")),
    COMPACT(CompactCarouselScreen::new, SkinShuffle.id("textures/gui/compact-view-button.png"),
            Text.translatable("skinshuffle.carousel.view_type_button.compact.tooltip"));

    public final Function<Screen, ? extends CarouselScreen> factory;
    public final Identifier iconTexture;
    public final Text tooltip;

    CarouselView(Function<Screen, ? extends CarouselScreen> factory, Identifier iconTexture, Text tooltip) {
        this.factory = factory;
        this.iconTexture = iconTexture;
        this.tooltip = tooltip;
    }
}
