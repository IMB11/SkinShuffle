package com.mineblock11.skinshuffle.client.config.gson;

import com.google.gson.*;
import com.mineblock11.skinshuffle.client.cape.provider.CapeProvider;
import com.mojang.serialization.JsonOps;

import java.lang.reflect.Type;

/**
 * A GSON type adapter to delegate to the codec for cape providers
 */
public class CapeProviderTypeAdapter implements JsonSerializer<CapeProvider>, JsonDeserializer<CapeProvider> {
    @Override
    public CapeProvider deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return CapeProvider.CODEC.decode(JsonOps.INSTANCE, json)
                .result()
                .orElseThrow(() -> new JsonParseException("Failed to decode cape provider"))
                .getFirst();
    }

    @Override
    public JsonElement serialize(CapeProvider src, Type typeOfSrc, JsonSerializationContext context) {
        return CapeProvider.CODEC.encodeStart(JsonOps.INSTANCE, src)
                .result()
                .orElseThrow(() -> new JsonParseException("Failed to encode cape provider"));
    }
}
