package com.mineblock11.skinshuffle.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAccessor;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAuthAccessor;
import com.mineblock11.skinshuffle.mixin.accessor.YggdrasilUserApiServiceAccessor;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import coresearch.cvurl.io.multipart.MultipartBody;
import coresearch.cvurl.io.multipart.Part;
import coresearch.cvurl.io.request.CVurl;
import net.minecraft.client.MinecraftClient;

public class MojangSkinAPI {
    private static final CVurl CLIENT = new CVurl();

    public enum SkinModelType {
        CLASSIC("classic"),
        SLIM("slim");

        public String getValue() {
            return value;
        }

        private final String value;

        SkinModelType(String value) {
            this.value = value;
        }
    }

    public static void setSkinTexture(String skinURL, SkinModelType model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        if(service instanceof YggdrasilUserApiService apiService) {
            try {
                com.mojang.authlib.minecraft.client.MinecraftClient client = ((YggdrasilUserApiServiceAccessor) apiService).getMinecraftClient();
                String token = ((MinecraftClientAuthAccessor) client).getAccessToken();

                Gson gson = new Gson();
                JsonObject obj = new JsonObject();
                obj.addProperty("variant", model.getValue());
                obj.addProperty("url", skinURL);
                CLIENT.post("https://api.minecraftservices.com/minecraft/profile/skins")
                        .body(gson.toJson(obj))
                        .header("Authorization", "Bearer " + token);
            } catch (Exception e) {
                throw new RuntimeException("Cannot connect to Mojang API.", e);
            }
        } else {
            throw new RuntimeException("Cannot connect to Mojang API - offline mode is active.");
        }
    }
    public static void uploadSkinTexture(byte[] textureBytes, SkinModelType model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        if(service instanceof YggdrasilUserApiService apiService) {
            try {
                com.mojang.authlib.minecraft.client.MinecraftClient client = ((YggdrasilUserApiServiceAccessor) apiService).getMinecraftClient();
                String token = ((MinecraftClientAuthAccessor) client).getAccessToken();

                MultipartBody multipartBody = MultipartBody.create()
                        .formPart("variant", Part.of(model.getValue()))
                        .formPart("file", Part.of(textureBytes));
                CLIENT.post("https://api.minecraftservices.com/minecraft/profile/skins")
                        .body(multipartBody)
                        .header("Authorization", "Bearer " + token);
            } catch (Exception e) {
                throw new RuntimeException("Cannot connect to Mojang API.", e);
            }
        } else {
            throw new RuntimeException("Cannot connect to Mojang API - offline mode is active.");
        }
    }
}
