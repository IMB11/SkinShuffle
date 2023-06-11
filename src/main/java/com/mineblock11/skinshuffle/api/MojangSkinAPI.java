package com.mineblock11.skinshuffle.api;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;
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
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import net.minecraft.client.MinecraftClient;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class MojangSkinAPI {
    private static final Path SKIN_CACHE_PATH = SkinShuffle.DATA_DIR.resolve("uploaded-skin-cache.json");
    private static Triplet<Boolean, @Nullable String, @Nullable String> cachedResult;
    private static Map<String, String> UPLOADED_SKIN_CACHE;
    private static final Gson GSON = new Gson();

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
    }

    /**
     * Get the player's skin texture.
     * @return Is a default skin? Skin URL, Model Type
     */
    public static Triplet<Boolean, @Nullable String, @Nullable String> getPlayerSkinTexture() {
        if (cachedResult != null) return cachedResult;
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
                    String jsonContent = new String(Base64.decodeBase64(properties.getAsJsonObject().get("value").getAsString()), StandardCharsets.UTF_8);
                    textureJSON = gson.fromJson(jsonContent, JsonObject.class);
                    break;
                }
            }

            if (textureJSON.has("invalid")) {
                cachedResult = new Triplet<>(true, null, null);
                return cachedResult;
            }

            if (!textureJSON
                    .get("textures").getAsJsonObject()
                    .has("SKIN")
            ) {
                cachedResult = new Triplet<>(true, null, null);
                return cachedResult;
            }

            var skin = textureJSON
                    .get("textures").getAsJsonObject()
                    .get("SKIN").getAsJsonObject();

            if (skin.get("url").getAsString().equals("Steve?") || skin.get("url").getAsString().equals("Alex?")) {
                cachedResult = new Triplet<>(true, null, null);
                return cachedResult;
            }

            String skinURL = skin.get("url").getAsString();
            String modelType = "default";

            try {
                modelType = skin
                        .get("metadata").getAsJsonObject()
                        .get("model").getAsString();
            } catch (Exception ignored) {
            }

            cachedResult = new Triplet<>(false, skinURL, modelType);
            return cachedResult;
        } catch (Exception e) {
            SkinShuffle.LOGGER.error(e.getMessage());
            return new Triplet<>(true, null, null);
        }
    }

    /**
     * Write the skin cache to a file.
     * @throws IOException
     */
    public static void writeSkinCache() throws IOException {
        if (UPLOADED_SKIN_CACHE == null) {
            UPLOADED_SKIN_CACHE = new HashMap<>();
            UPLOADED_SKIN_CACHE.put("warning", "Please do not edit this file. This file is automatically generated.");
        }
        Gson gson = new Gson();
        String jsonString = gson.toJson(UPLOADED_SKIN_CACHE);
        java.nio.file.Files.writeString(SKIN_CACHE_PATH, jsonString);
    }

    /**
     * Load the skin cache from a file.
     * @throws IOException
     */
    public static void loadSkinCache() throws IOException {
        if (!SKIN_CACHE_PATH.toFile().exists()) writeSkinCache();
        Gson gson = new Gson();
        String jsonString = java.nio.file.Files.readString(SKIN_CACHE_PATH);
        UPLOADED_SKIN_CACHE = gson.fromJson(jsonString, Map.class);
    }

    /**
     * Set a skin texture from a file - will use URL if file has not been modified since previous upload.
     * @param skinFile The file to upload.
     * @param model The type of skin model.
     */
    public static void setSkinTexture(File skinFile, String model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();
        String fileHash;
        try {
            fileHash = Files.asByteSource(skinFile).hash(Hashing.sha1()).toString();
            if (UPLOADED_SKIN_CACHE.containsKey(fileHash)) {
                setSkinTexture(UPLOADED_SKIN_CACHE.get(fileHash), model);
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
                UPLOADED_SKIN_CACHE.put(fileHash, skinURL);
                writeSkinCache();
                SkinShuffle.LOGGER.info("Uploaded texture: " + skinURL);
                SkinShuffle.LOGGER.info("Set player skin: " + skinURL);
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
