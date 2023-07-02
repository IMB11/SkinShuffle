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
import com.mineblock11.skinshuffle.client.preset.SkinPreset;

public class CarouselPresetWidget extends PresetWidget {
    public CarouselPresetWidget(CarouselScreen parent, int width, int height, SkinPreset skinPreset) {
        super(parent, width, height, skinPreset);

        editButton.overridePosition((getWidth() / 8) + 4, getHeight() - 48);
        editButton.overrideDimensions(getWidth() - (this.getWidth() / 4) - 8, 20);

        copyButton.overridePosition(3, getHeight() - 24);
        copyButton.overrideDimensions(getWidth() / 2 - 5, 20);

        deleteButton.overridePosition(getWidth() / 2 + 2, getHeight() - 24);
        deleteButton.overrideDimensions(getWidth() / 2 - 5, 20);
    }
}
