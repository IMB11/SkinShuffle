package com.mineblock11.skinshuffle.client.skin;

import com.mojang.serialization.Codec;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public interface Skin {
    Map<Identifier, Codec<? extends Skin>> TYPES = Map.of(
            UrlSkin.SERIALIZATION_ID, UrlSkin.CODEC,
            ResourceSkin.SERIALIZATION_ID, ResourceSkin.CODEC,
            ConfigSkin.SERIALIZATION_ID, ConfigSkin.CODEC,
            FileSkin.SERIALIZATION_ID, FileSkin.CODEC
    );
    Codec<Skin> CODEC = Identifier.CODEC.dispatch("type", Skin::getSerializationId, TYPES::get);

    @Nullable Identifier getTexture();

    String getModel();

    Identifier getSerializationId();

    ConfigSkin saveToConfig();
}
