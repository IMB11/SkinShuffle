package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

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
        var textureName = String.valueOf(Math.abs(getTexture().hashCode()));
        var configSkin = new ConfigSkin(textureName, getModel());

        var resourceManager = MinecraftClient.getInstance().getResourceManager();

        try (ResourceTexture.TextureData data = ResourceTexture.TextureData.load(resourceManager, getTexture())) {
                var nativeImage = data.getImage();
                nativeImage.writeTo(configSkin.getFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ResourceSkin to config.", e);
        }

        return configSkin;
    }
}
