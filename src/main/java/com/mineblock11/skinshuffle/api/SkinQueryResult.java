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

package com.mineblock11.skinshuffle.api;

import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.Nullable;

public record SkinQueryResult(boolean usesDefaultSkin, @Nullable String skinURL, @Nullable String modelType, @Nullable String textureSignature, @Nullable String textureValue) {
    public static final SkinQueryResult EMPTY_RESULT = new SkinQueryResult(true, null, null, null, null);

    public Property toProperty() {
        return new Property("textures", textureValue, textureSignature);
    }
}
