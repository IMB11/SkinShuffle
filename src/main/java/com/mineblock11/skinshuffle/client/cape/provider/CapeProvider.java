package com.mineblock11.skinshuffle.client.cape.provider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mineblock11.skinshuffle.SkinShuffle;
import kong.unirest.Unirest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import org.jetbrains.annotations.Nullable;

public interface CapeProvider {
    default @Nullable byte[] getClientCapeTexture() {
        return getCapeTexture(MinecraftClient.getInstance().getSession().getUsername());
    }

    String getProviderID();

    Gson GSON = new Gson();

    default @Nullable byte[] getCapeTexture(String username) {
        try {
            JsonObject result = GSON.fromJson(Unirest.get("https://api.capes.dev/load/%s/%s".formatted(username, getProviderID())).asString().getBody(), JsonObject.class);
            if (result.get("exists").getAsBoolean()) {
                String imageURL = result.get("imageUrl").getAsString();
                return Unirest.get(imageURL).asBytes().getBody();
            } else return null;
        } catch (Exception ignored) {
            SkinShuffle.LOGGER.info("Failed to run cape provider \"{}\"", getProviderID());
            return null;
        }
    }
}
