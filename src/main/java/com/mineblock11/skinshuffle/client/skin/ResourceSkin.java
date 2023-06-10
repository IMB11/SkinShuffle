package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record ResourceSkin(Identifier texture, String model) implements Skin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("resource");
    public static final Codec<ResourceSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(ResourceSkin::getTexture),
            Codec.STRING.fieldOf("model").forGetter(ResourceSkin::getModel)
    ).apply(instance, ResourceSkin::new));

    @Override
    public @Nullable Identifier getTexture() {
        return texture;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public Identifier getSerializationId() {
        return SERIALIZATION_ID;
    }

    @Override
    public ConfigSkin saveToConfig() {
        // TODO: not sure how to handle this one
        throw new UnsupportedOperationException("Cannot save a resource skin to config");
    }
}
