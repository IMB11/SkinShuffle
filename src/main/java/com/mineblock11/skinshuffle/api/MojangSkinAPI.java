package com.mineblock11.skinshuffle.api;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAccessor;
import com.mineblock11.skinshuffle.mixin.accessor.MinecraftClientAuthAccessor;
import com.mineblock11.skinshuffle.mixin.accessor.YggdrasilUserApiServiceAccessor;
import com.mineblock11.skinshuffle.util.Triplet;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import coresearch.cvurl.io.multipart.MultipartBody;
import coresearch.cvurl.io.multipart.Part;
import coresearch.cvurl.io.request.CVurl;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;
import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;

public class MojangSkinAPI {
    private static final CVurl CLIENT = new CVurl();

    public static void setSkinTexture(String skinURL, SkinModelType model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        if (service instanceof YggdrasilUserApiService apiService) {
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

    // is default skin, skin url, model type
    public static Triplet<Boolean, @Nullable String, @Nullable SkinModelType> getPlayerSkinTexture() {
        MinecraftClient client = MinecraftClient.getInstance();

        try {
            String UUID = client.getSession().getUuid();
            String jsonResponse = CLIENT.get("https://sessionserver.mojang.com/session/minecraft/profile/" + UUID).asString().get().getBody();
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
                return new Triplet<>(true, null, null);
            }

            if(!textureJSON
                    .get("textures").getAsJsonObject()
                    .has("SKIN")
            ) {
                return new Triplet<>(true, null, null);
            }

            var skin = textureJSON
                    .get("textures").getAsJsonObject()
                    .get("SKIN").getAsJsonObject();

            if(skin.get("url").getAsString().equals("Steve?") || skin.get("url").getAsString().equals("Alex?")) {
                return new Triplet<>(true, null, null);
            }

            String skinURL = skin.get("url").getAsString();
            String modelType = "default";

            try {
                 modelType = skin
                        .get("metadata").getAsJsonObject()
                        .get("model").getAsString();
            } catch (Exception ignored) {}

            return new Triplet<>(false, skinURL, SkinModelType.valueOf(modelType.toUpperCase()));
        } catch (Exception e) {
            SkinShuffle.LOGGER.error(e.getMessage());
            return new Triplet<>(true, null, null);
        }
    }

    public static void uploadSkinTexture(byte[] textureBytes, SkinModelType model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        if (service instanceof YggdrasilUserApiService apiService) {
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
                SkinShuffle.LOGGER.error(e.getMessage());
            }
        } else {
            SkinShuffle.LOGGER.error("Cannot connect to Mojang API.");
        }
    }

    public enum SkinModelType {
        DEFAULT("default"),
        SLIM("slim");

        private final String value;

        SkinModelType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
