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

package com.mineblock11.skinshuffle.compat;

import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;

/**
 * Compatability for Entity Texture Features.
 * <p>
 * <a href="https://modrinth.com/mod/entitytexturefeatures">Modrinth</a>
 */
public class ETFCompatHandler implements CompatHandler {
    // Disable the config option in the screen.
    public static boolean DISABLE_RENDER_DESYNC = false;

    @Override
    public String getID() {
        return "entity_texture_features";
    }

    @Override
    public void execute() {
        // ETF's blink and other model features do not work when render desync is enabled.
        DISABLE_RENDER_DESYNC = true;
        SkinShuffleConfig.get().renderClientSkinRegardless = false;
        SkinShuffleConfig.GSON.save();
    }
}
