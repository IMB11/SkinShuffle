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

package com.mineblock11.skinshuffle.client.config.gson;

import com.google.gson.*;
import com.mineblock11.skinshuffle.client.cape.provider.CapeProvider;
import com.mojang.serialization.JsonOps;

import java.lang.reflect.Type;

/**
 * A GSON type adapter to delegate to the codec for cape providers
 */
public class CapeProviderTypeAdapter implements JsonSerializer<CapeProvider>, JsonDeserializer<CapeProvider> {
    @Override
    public CapeProvider deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return CapeProvider.CODEC.decode(JsonOps.INSTANCE, json)
                .result()
                .orElseThrow(() -> new JsonParseException("Failed to decode cape provider"))
                .getFirst();
    }

    @Override
    public JsonElement serialize(CapeProvider src, Type typeOfSrc, JsonSerializationContext context) {
        return CapeProvider.CODEC.encodeStart(JsonOps.INSTANCE, src)
                .result()
                .orElseThrow(() -> new JsonParseException("Failed to encode cape provider"));
    }
}
