package com.mineblock11.skinshuffle.client.cape;

import com.mojang.serialization.Codec;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Cape {
    Map<Identifier, Codec<? extends Cape>> TYPES = Map.of(
            ConfigCape.SERIALIZATION_ID, ConfigCape.CODEC,
            ProvidedCape.SERIALIZATION_ID, ProvidedCape.CODEC,
            UrlCape.SERIALIZATION_ID, UrlCape.CODEC
    );
    Codec<Cape> CODEC = Identifier.CODEC.dispatch("type", Cape::getSerializationId, TYPES::get);

    Identifier getSerializationId();
    @Nullable ConfigCape saveToConfig();
    @Nullable Identifier getTexture();
    boolean isLoading();
}
