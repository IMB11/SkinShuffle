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
import kong.unirest.Unirest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class UrlSkin extends BackedSkin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("url");
    public static final Codec<UrlSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("url").forGetter(skin -> skin.url),
            Codec.STRING.fieldOf("model").forGetter(UrlSkin::getModel)
    ).apply(instance, UrlSkin::new));

    protected String url;
    private final String model;

    public UrlSkin(String url, String model) {
        this.url = url;
        this.model = model;
    }

    protected UrlSkin(String model) {
        this.model = model;
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
    protected Object getTextureUniqueness() {
        return url;
    }

    @Override
    public ConfigSkin saveToConfig() {
        try {
            var textureName = String.valueOf(Math.abs(getTextureUniqueness().hashCode()));
            var configSkin = new ConfigSkin(textureName, getModel());

            var bytes = Unirest.get(this.url).asBytes().getBody();

            Files.write(configSkin.getFile(), bytes);

            return configSkin;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save skin to config", e);
        }
    }

    @Override
    protected @Nullable AbstractTexture loadTexture(Runnable completionCallback) {
        try {
            Path cacheFolder = FabricLoader.getInstance().getGameDir().resolve(".cache/");
            if (!cacheFolder.toFile().exists()) {
                Files.createDirectory(cacheFolder);
            }

            Path temporaryFilePath = cacheFolder.resolve(Math.abs(url.hashCode()) + ".png");

            // Downloading the file this way corrupts it, and is not needed, as PlayerSkinTexture will download it for us
//            var bytes = Unirest.get(this.url).asBytes().getBody();

//            Files.write(temporaryFilePath, bytes);

            return new PlayerSkinTexture(temporaryFilePath.toFile(), url, new Identifier("minecraft:textures/entity/player/wide/steve.png"), true, () -> {
                completionCallback.run();

                try {
                    if (temporaryFilePath.toFile().exists()) {
                        Files.delete(temporaryFilePath);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            SkinShuffle.LOGGER.warn("Failed to load skin from URL: " + url, e);
            return null;
        }
    }

    public String getUrl() {
        return url;
    }
}
