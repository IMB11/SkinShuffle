package com.mineblock11.skinshuffle.client.cape.provider;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import kong.unirest.Unirest;
import org.jetbrains.annotations.Nullable;

public class OptifineProvider implements CapeProvider {
    private static Gson GSON = new Gson();
    @Override
    public @Nullable byte[] getCapeTexture(String username) {
        JsonObject result = GSON.fromJson(Unirest.get("https://api.capes.dev/load/%s/optifine".formatted(username)).asString().getBody(), JsonObject.class);
        if(result.get("exists").getAsBoolean()) {

        }
        else return null;
    }
}
