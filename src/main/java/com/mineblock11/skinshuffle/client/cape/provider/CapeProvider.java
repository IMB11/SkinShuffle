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

package com.mineblock11.skinshuffle.client.cape.provider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.util.NetworkingUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import kong.unirest.Unirest;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

public interface CapeProvider {
    Codec<CapeProvider> CODEC = Codec.STRING.comapFlatMap(CapeProvider::validate, CapeProvider::getProviderID);
    CapeProvider AUTO = new CapeProvider() {
        @Override
        public byte @Nullable [] getCapeTexture(String id) {
            if(!NetworkingUtil.isLoggedIn()) return null;
            for (var provider : CapeProviders.values()) { // TODO: order using config
                byte[] texture = provider.getCapeTexture(id);
                if (texture != null) return texture;
            }
            return null;
        }

        @Override
        public String getProviderID() {
            return "auto";
        }
    };

    CapeProvider NONE = new CapeProvider() {
        @Override
        public byte @Nullable [] getCapeTexture(String id) {
            return null;
        }

        @Override
        public String getProviderID() {
            return "none";
        }
    };

    private static DataResult<CapeProvider> validate(String id) {
        return switch (id) {
            case "none" -> DataResult.success(NONE);
            case "default", "auto" -> DataResult.success(AUTO);
            case "minecraft", "MOJANG" -> DataResult.success(CapeProviders.MOJANG);
            case "optifine", "OPTIFINE" -> DataResult.success(CapeProviders.OPTIFINE);
            case "minecraftcapes", "MC_CAPES" -> DataResult.success(CapeProviders.MC_CAPES);
            default -> DataResult.error(() -> "Invalid cape provider id: " + id);
        };
    }

    default byte @Nullable [] getClientCapeTexture() {
        return getCapeTexture(MinecraftClient.getInstance().getSession().getUsername());
    }

    String getProviderID();

    Gson GSON = new Gson();

    default byte @Nullable [] getCapeTexture(String id) {
        try {
            JsonObject result = GSON.fromJson(Unirest.get("https://api.capes.dev/load/%s/%s".formatted(id, getProviderID())).asString().getBody(), JsonObject.class);
            if (result.get("exists").getAsBoolean()) {
                String imageURL = result.get("imageUrl").getAsString();
                return Unirest.get(imageURL).asBytes().getBody();
            } else return null;
        } catch (Exception e) {
            return null;
        }
    }

    default String getTranslationKey() {
        return "skinshuffle.cape_provider." + getProviderID();
    }
}
