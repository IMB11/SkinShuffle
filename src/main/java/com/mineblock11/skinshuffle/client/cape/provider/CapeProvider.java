package com.mineblock11.skinshuffle.client.cape.provider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mineblock11.skinshuffle.SkinShuffle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import kong.unirest.Unirest;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import org.jetbrains.annotations.Nullable;

public interface CapeProvider {
    Codec<CapeProvider> CODEC = Codec.STRING.comapFlatMap(CapeProvider::validate, CapeProvider::getProviderID);
    CapeProvider DEFAULT = new CapeProvider() {
        @Override
        public byte @Nullable [] getCapeTexture(String id) {
            for (var provider : CapeProviders.values()) { // TODO: order using config
                byte[] texture = provider.getCapeTexture(id);
                if (texture != null) return texture;
            }
            return null;
        }

        @Override
        public String getProviderID() {
            return "default";
        }
    };

    private static DataResult<CapeProvider> validate(String id) {
        switch (id) {
            case "default" -> {
                return DataResult.success(DEFAULT);
            }
            case "minecraft" -> {
                return DataResult.success(CapeProviders.MOJANG);
            }
            case "optifine" -> {
                return DataResult.success(CapeProviders.OPTIFINE);
            }
            case "minecraftcapes" -> {
                return DataResult.success(CapeProviders.MC_CAPES);
            }
        }
        return DataResult.error(() -> "Invalid cape provider id: " + id);
    }

    default byte @Nullable [] getClientCapeTexture() {
        return getCapeTexture(MinecraftClient.getInstance().getSession().getUsername());
    }

    String getProviderID();

    Gson GSON = new Gson();

    default byte @Nullable [] getCapeTexture(String id) {
        try {
            JsonObject result = GSON.fromJson(Unirest.get("https://api.capes.dev/load/%s/%s".formatted(id, getProviderID())).asString().getBody(), JsonObject.class);
            System.out.println("https://api.capes.dev/load/%s/%s".formatted(id, getProviderID()));
            if (result.get("exists").getAsBoolean()) {
                String imageURL = result.get("imageUrl").getAsString();
                return Unirest.get(imageURL).asBytes().getBody();
            } else return null;
        } catch (Exception e) {
            SkinShuffle.LOGGER.info("Failed to run cape provider \"{}\"", getProviderID());
            SkinShuffle.LOGGER.info(e.toString());
            return null;
        }
    }

    default String getTranslationKey() {
        return "skinshuffle.cape_provider." + getProviderID();
    }
}
