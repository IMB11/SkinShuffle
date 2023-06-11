package com.mineblock11.skinshuffle.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAccessor;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAuthAccessor;
import com.mineblock11.skinshuffle.mixin.accessor.YggdrasilUserApiServiceAccessor;
import com.mineblock11.skinshuffle.util.AuthUtil;
import com.mineblock11.skinshuffle.util.Triplet;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import kong.unirest.ContentType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class MojangSkinAPI {
    public static void setSkinTexture(String skinURL, String model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        if (service instanceof YggdrasilUserApiService apiService) {
            try {
                com.mojang.authlib.minecraft.client.MinecraftClient client = ((YggdrasilUserApiServiceAccessor) apiService).getMinecraftClient();
                String token = ((MinecraftClientAuthAccessor) client).getAccessToken();

                Gson gson = new Gson();
                JsonObject obj = new JsonObject();
                obj.addProperty("variant", model.equals("default") ? "classic" : "slim");
                obj.addProperty("url", skinURL);
                var result = Unirest.post("https://api.minecraftservices.com/minecraft/profile/skins")
                        .body(gson.toJson(obj))
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token).asString().getBody();
                SkinShuffle.LOGGER.info(result);
            } catch (Exception e) {
                throw new RuntimeException("Cannot connect to Mojang API.", e);
            }
        } else {
            throw new RuntimeException("Cannot connect to Mojang API - offline mode is active.");
        }
    }

    private static Triplet<Boolean, @Nullable String, @Nullable String> cachedResult;

    // is default skin, skin url, model type
    public static Triplet<Boolean, @Nullable String, @Nullable String> getPlayerSkinTexture() {
        if(cachedResult != null) return cachedResult;
        MinecraftClient client = MinecraftClient.getInstance();

        try {
            String UUID = client.getSession().getUuid();
            String jsonResponse = Unirest.get("https://sessionserver.mojang.com/session/minecraft/profile/" + UUID)
                    .asString().getBody();

            Gson gson = new Gson();
            JsonObject object = gson.fromJson(jsonResponse, JsonObject.class);
            JsonObject textureJSON = new JsonObject();
            textureJSON.addProperty("invalid", true);

            for (JsonElement properties : object.get("properties").getAsJsonArray()) {
                if (properties.getAsJsonObject().get("name").getAsString().equals("textures")) {
                    String jsonContent = new String(Base64.decodeBase64(properties.getAsJsonObject().get("value").getAsString()), "UTF-8");
                    textureJSON = gson.fromJson(jsonContent, JsonObject.class);
                    break;
                }
            }

            if(textureJSON.has("invalid")) {
                cachedResult = new Triplet<>(true, null, null);
                return cachedResult;
            }

            if(!textureJSON
                    .get("textures").getAsJsonObject()
                    .has("SKIN")
            ) {
                cachedResult = new Triplet<>(true, null, null);
                return cachedResult;
            }

            var skin = textureJSON
                    .get("textures").getAsJsonObject()
                    .get("SKIN").getAsJsonObject();

            if(skin.get("url").getAsString().equals("Steve?") || skin.get("url").getAsString().equals("Alex?")) {
                cachedResult = new Triplet<>(true, null, null);
                return cachedResult;
            }

            String skinURL = skin.get("url").getAsString();
            String modelType = "default";

            try {
                 modelType = skin
                        .get("metadata").getAsJsonObject()
                        .get("model").getAsString();
            } catch (Exception ignored) {}

            cachedResult =  new Triplet<>(false, skinURL, modelType);
            return cachedResult;
        } catch (Exception e) {
            SkinShuffle.LOGGER.error(e.getMessage());
            return new Triplet<>(true, null, null);
        }
    }

    public static void setSkinTexture(File skinFile, String model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        if (AuthUtil.isLoggedIn()) {
            try {
                com.mojang.authlib.minecraft.client.MinecraftClient client = ((YggdrasilUserApiServiceAccessor) service).getMinecraftClient();
                String token = ((MinecraftClientAuthAccessor) client).getAccessToken();

                HttpResponse<String> response = Unirest.post("https://api.minecraftservices.com/minecraft/profile/skins")
                        .header("Authorization", "Bearer " + token)
                        .field("variant", model.equals("default") ? "classic" : "slim")
                        .field("file", skinFile)
                        .asString();
                SkinShuffle.LOGGER.info(response.getBody());
            } catch (Exception e) {
                SkinShuffle.LOGGER.error(e.getMessage());
            }
        } else {
            SkinShuffle.LOGGER.error("Cannot connect to Mojang API.");
        }
    }

    public static void resetCache() {
        cachedResult = null;
    }
}
