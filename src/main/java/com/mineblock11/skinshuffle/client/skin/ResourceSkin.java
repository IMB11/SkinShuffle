/*
 *
 *     Copyright (C) 2023 Calum (mineblock11), enjarai
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301
 *     USA
 */

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
    public boolean isLoading() {
        return false;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public String getSourceString() {
        return getTexture().toString();
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
