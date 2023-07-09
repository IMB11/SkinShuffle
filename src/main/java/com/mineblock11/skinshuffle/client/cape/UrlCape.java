package com.mineblock11.skinshuffle.client.cape;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.skin.BackedSkin;
import com.mineblock11.skinshuffle.client.skin.ConfigSkin;
import com.mineblock11.skinshuffle.client.skin.UrlSkin;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kong.unirest.Unirest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;

public class UrlCape extends BackedCape {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("url");
    public static final Codec<UrlCape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("url").forGetter(UrlCape::getUrl)
    ).apply(instance, UrlCape::new));

    protected String url;

    private UrlCape(String url) {
        this.url = url;
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
    public ConfigCape saveToConfig() {
        try {
            var textureName = String.valueOf(Math.abs(getTextureUniqueness().hashCode()));
            var configCape = new ConfigCape(textureName);

            var bytes = Unirest.get(this.url).asBytes().getBody();

            Files.write(configCape.getFile(), bytes);

            return configCape;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save cape to config", e);
        }
    }

    @Override
    protected @Nullable AbstractTexture loadTexture(Runnable completionCallback) {
        try {
            Path cacheFolder = FabricLoader.getInstance().getGameDir().resolve(".cache/");
            if (!cacheFolder.toFile().exists()) {
                Files.createDirectory(cacheFolder);
            }

            var bytes = Unirest.get(this.url).asBytes().getBody();
            return new NativeImageBackedTexture(NativeImage.read(bytes));
        } catch (Exception e) {
            SkinShuffle.LOGGER.warn("Failed to load cape from URL: " + url, e);
            return null;
        }
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlCape urlSkin = (UrlCape) o;

        return Objects.equals(url, urlSkin.url);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result;
        return result;
    }
}
