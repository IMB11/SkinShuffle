package dev.imb11.skinshuffle.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.authlib.minecraft.UserApiService;
import com.mojang.authlib.yggdrasil.YggdrasilUserApiService;
import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.api.data.SkinQueryResult;
import dev.imb11.skinshuffle.mixin.accessor.MinecraftClientAccessor;
import dev.imb11.skinshuffle.mixin.accessor.MinecraftClientAuthAccessor;
import dev.imb11.skinshuffle.mixin.accessor.YggdrasilUserApiServiceAccessor;
import dev.imb11.skinshuffle.util.NetworkingUtil;
import dev.imb11.skinshuffle.util.SkinCacheRegistry;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import net.minecraft.client.MinecraftClient;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

public class MojangSkinAPI {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Set the player's skin texture from a URL.
     *
     * @param skinURL The URL of the skin texture.
     * @param model   The skin model type.
     */
    public static boolean setSkinTexture(String skinURL, String model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        if (service instanceof YggdrasilUserApiService apiService) {
            try {
                com.mojang.authlib.minecraft.client.MinecraftClient client = ((YggdrasilUserApiServiceAccessor) apiService).getMinecraftClient();
                String token = ((MinecraftClientAuthAccessor) client).getAccessToken();

                JsonObject obj = new JsonObject();
                obj.addProperty("variant", model);
                obj.addProperty("url", skinURL);
                var result = Unirest.post("https://api.minecraftservices.com/minecraft/profile/skins")
                        .body(GSON.toJson(obj))
                        .contentType("application/json")
                        .header("Authorization", "Bearer " + token).asString().getBody();
                SkinShuffle.LOGGER.info("Set player skin: " + skinURL);
            } catch (Exception e) {
                SkinShuffle.LOGGER.error("Cannot connect to Mojang API.", e);
                return false;
            }
        } else {
            SkinShuffle.LOGGER.error("Cannot connect to Mojang API - offline mode is active.");
            return false;
        }

        return true;
    }

    /**
     * Get the player's skin texture.
     *
     * @param uuid
     * @return Is a default skin? Skin URL, Model Type
     */
    public static SkinQueryResult getPlayerSkinTexture(String uuid) {
        try {
            String jsonResponse = Unirest.get("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false")
                    .asString().getBody();

            if (jsonResponse.isBlank()) {
                throw new IOException("Empty response from Mojang API.");
            }

            Gson gson = new Gson();
            JsonObject object = gson.fromJson(jsonResponse, JsonObject.class);
            JsonObject textureJSON = new JsonObject();
            textureJSON.addProperty("invalid", true);

            @Nullable String textureSignature = null;
            @Nullable String textureValue = null;

            for (JsonElement properties : object.get("properties").getAsJsonArray()) {
                if (properties.getAsJsonObject().get("name").getAsString().equals("textures")) {
                    try {
                        textureSignature = properties.getAsJsonObject().get("signature").getAsString();
                        textureValue = properties.getAsJsonObject().get("value").getAsString();

                        if (textureValue != null) {
                            String jsonContent = new String(Base64.getDecoder().decode(textureValue), StandardCharsets.UTF_8);
                            textureJSON = gson.fromJson(jsonContent, JsonObject.class);
                        } else {
                            SkinShuffle.LOGGER.warn("Received null texture value from Mojang API");
                        }
                    } catch (Exception e) {
                        SkinShuffle.LOGGER.error("Failed to decode texture data", e);
                    }
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
            String modelType = "classic";

            try {
                modelType = skin
                        .get("metadata").getAsJsonObject()
                        .get("model").getAsString();
            } catch (Exception ignored) {
            }

            String capeURL = null;

            try {
                var cape = textureJSON
                        .get("textures").getAsJsonObject().get("CAPE").getAsJsonObject();
                capeURL = cape.get("url").getAsString();
            } catch (Exception ignored) {
            }

            return new SkinQueryResult(false, skinURL, modelType, textureSignature, textureValue);
        } catch (Exception e) {
            SkinShuffle.LOGGER.error(e.getMessage());
            return SkinQueryResult.EMPTY_RESULT;
        }
    }

    /**
     * Get the player's uuid using their username.
     *
     * @param username
     * @return An Optional containing the UUID of the provided username, or an empty Optional if the username is invalid.
     */
    public static Optional<UUID> getUUIDFromUsername(String username) {
        try {
            String jsonResponse = Unirest.get("https://api.mojang.com/users/profiles/minecraft/" + username)
                    .asString().getBody();

            Gson gson = new Gson();
            JsonObject object = gson.fromJson(jsonResponse, JsonObject.class);

            if (object.has("error")) {
                throw new RuntimeException(object.get("errorMessage").getAsString());
            }

            var idString = object.get("id").getAsString();

            return Optional.of(UUID.fromString(idString.replaceFirst(
                    "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5"
            )));
        } catch (Exception e) {
            SkinShuffle.LOGGER.error(e.toString());
            return Optional.empty();
        }
    }

    /**
     * Set a skin texture from a file - will use URL if file has not been modified since previous upload.
     *
     * @param skinFile The file to upload.
     * @param model    The type of skin model.
     */
    public static boolean setSkinTexture(File skinFile, String model) {
        UserApiService service = ((MinecraftClientAccessor) MinecraftClient.getInstance()).getUserApiService();

        if (model.equals("default")) {
            model = "classic";
        }

        try {
            var cachedURL = SkinCacheRegistry.getCachedUploadedSkin(skinFile);
            if (cachedURL != null) {
                return setSkinTexture(cachedURL, model);
            }
        } catch (IOException e) {
            SkinShuffle.LOGGER.error("Failed to hash file.");
            return false;
        }

        if (NetworkingUtil.isLoggedIn()) {
            try {
                com.mojang.authlib.minecraft.client.MinecraftClient client = ((YggdrasilUserApiServiceAccessor) service).getMinecraftClient();
                String token = ((MinecraftClientAuthAccessor) client).getAccessToken();

                HttpResponse<String> response = Unirest.post("https://api.minecraftservices.com/minecraft/profile/skins")
                        .header("Authorization", "Bearer " + token)
                        .field("variant", model)
                        .field("file", skinFile)
                        .asString();
                JsonObject responseObject = GSON.fromJson(response.getBody(), JsonObject.class);

                String skinURL = responseObject
                        .get("skins").getAsJsonArray()
                        .get(0).getAsJsonObject()
                        .get("url").getAsString();

                SkinCacheRegistry.saveUploadedSkin(skinFile, skinURL);

                SkinShuffle.LOGGER.info("Uploaded texture: " + skinURL);
                SkinShuffle.LOGGER.info("Set player skin: " + skinURL);
                return true;
            } catch (Exception e) {
                SkinShuffle.LOGGER.error(e.toString());
                return false;
            }
        } else {
            SkinShuffle.LOGGER.error("Cannot connect to Mojang API.");
            return false;
        }
    }
}
