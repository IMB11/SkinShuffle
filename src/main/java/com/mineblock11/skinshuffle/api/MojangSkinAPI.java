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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAccessor;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAuthAccessor;
import com.mineblock11.skinshuffle.mixin.accessor.YggdrasilUserApiServiceAccessor;
import com.mineblock11.skinshuffle.util.AuthUtil;
import com.mineblock11.skinshuffle.util.SkinCacheRegistry;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class MojangSkinAPI {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Set the player's skin texture from a URL.
     * @param skinURL The URL of the skin texture.
     * @param model The skin model type.
     */
    public static void setSkinTexture(String skinURL, String model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        if (service instanceof YggdrasilUserApiService apiService) {
            try {
                com.mojang.authlib.minecraft.client.MinecraftClient client = ((YggdrasilUserApiServiceAccessor) apiService).getMinecraftClient();
                String token = ((MinecraftClientAuthAccessor) client).getAccessToken();

                JsonObject obj = new JsonObject();
                obj.addProperty("variant", model.equals("default") ? "classic" : "slim");
                obj.addProperty("url", skinURL);
                var result = Unirest.post("https://api.minecraftservices.com/minecraft/profile/skins")
                        .body(GSON.toJson(obj))
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token).asString().getBody();
                SkinShuffle.LOGGER.info("Set player skin: " + skinURL);
            } catch (Exception e) {
                throw new RuntimeException("Cannot connect to Mojang API.", e);
            }
        } else {
            throw new RuntimeException("Cannot connect to Mojang API - offline mode is active.");
        }

        if(MinecraftClient.getInstance().world != null) {
            ClientPlayNetworking.send(SkinShuffle.id("preset_changed"), PacketByteBufs.empty());
        }
    }

    /**
     * Get the player's skin texture.
     * @return Is a default skin? Skin URL, Model Type
     * @param uuid
     */
    public static SkinQueryResult getPlayerSkinTexture(String uuid) {
        try {
            String jsonResponse = Unirest.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false")
                    .asString().getBody();

            Gson gson = new Gson();
            JsonObject object = gson.fromJson(jsonResponse, JsonObject.class);
            JsonObject textureJSON = new JsonObject();
            textureJSON.addProperty("invalid", true);

            @Nullable String textureSignature = null;
            @Nullable String textureValue = null;

            for (JsonElement properties : object.get("properties").getAsJsonArray()) {
                if (properties.getAsJsonObject().get("name").getAsString().equals("textures")) {
                    textureSignature = properties.getAsJsonObject().get("signature").getAsString();
                    textureValue = properties.getAsJsonObject().get("value").getAsString();

                    String jsonContent = new String(Base64.getDecoder().decode(textureValue), StandardCharsets.UTF_8);
                    textureJSON = gson.fromJson(jsonContent, JsonObject.class);
                    break;
                }
            }

            if (textureJSON.has("invalid")) {
                return SkinQueryResult.EMPTY_RESULT;
            }

            if (!textureJSON
                    .get("textures").getAsJsonObject()
                    .has("SKIN")
            ) {
                return SkinQueryResult.EMPTY_RESULT;
            }

            var skin = textureJSON
                    .get("textures").getAsJsonObject()
                    .get("SKIN").getAsJsonObject();

            if (skin.get("url").getAsString().equals("Steve?") || skin.get("url").getAsString().equals("Alex?")) {
                return SkinQueryResult.EMPTY_RESULT;
            }

            String skinURL = skin.get("url").getAsString();
            String modelType = "default";

            try {
                modelType = skin
                        .get("metadata").getAsJsonObject()
                        .get("model").getAsString();
            } catch (Exception ignored) {
            }

            return new SkinQueryResult(false, skinURL, modelType, textureSignature, textureValue);
        } catch (Exception e) {
            SkinShuffle.LOGGER.error(e.getMessage());
            return SkinQueryResult.EMPTY_RESULT;
        }
    }

    /**
     * Set a skin texture from a file - will use URL if file has not been modified since previous upload.
     * @param skinFile The file to upload.
     * @param model The type of skin model.
     */
    public static void setSkinTexture(File skinFile, String model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        try {
            var cachedURL = SkinCacheRegistry.getCachedUploadedSkin(skinFile);
            if (cachedURL != null) {
                setSkinTexture(cachedURL, model);
                return;
            }
        } catch (IOException e) {
            SkinShuffle.LOGGER.error("Failed to hash file.");
            return;
        }

        if (AuthUtil.isLoggedIn()) {
            try {
                com.mojang.authlib.minecraft.client.MinecraftClient client = ((YggdrasilUserApiServiceAccessor) service).getMinecraftClient();
                String token = ((MinecraftClientAuthAccessor) client).getAccessToken();

                HttpResponse<String> response = Unirest.post("https://api.minecraftservices.com/minecraft/profile/skins")
                        .header("Authorization", "Bearer " + token)
                        .field("variant", model.equals("default") ? "classic" : "slim")
                        .field("file", skinFile)
                        .asString();
                JsonObject responseObject = GSON.fromJson(response.getBody(), JsonObject.class);
                String skinURL = responseObject
                        .get("skins").getAsJsonArray()
                        .get(0).getAsJsonObject()
                        .get("url").getAsString();

                SkinCacheRegistry.saveUploadedSkin(skinFile, skinURL);

                if(MinecraftClient.getInstance().world != null) {
                    ClientPlayNetworking.send(SkinShuffle.id("preset_changed"), PacketByteBufs.create().writeString(skinURL));
                }

                SkinShuffle.LOGGER.info("Uploaded texture: " + skinURL);
                SkinShuffle.LOGGER.info("Set player skin: " + skinURL);
            } catch (Exception e) {
                SkinShuffle.LOGGER.error(e.getMessage());
            }
        } else {
            SkinShuffle.LOGGER.error("Cannot connect to Mojang API.");
        }
    }
}
