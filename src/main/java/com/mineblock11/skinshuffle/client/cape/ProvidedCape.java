package com.mineblock11.skinshuffle.client.cape;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.cape.provider.CapeProvider;
import com.mineblock11.skinshuffle.client.cape.provider.OptifineProvider;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import kong.unirest.Unirest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Represents either Optifine or MinecraftCapes cape.
 */
public class ProvidedCape extends BackedCape {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("provided");
    public static final Codec<ProvidedCape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("provider_type").forGetter(providedCape -> providedCape.providerType.name())
    ).apply(instance, (String type) -> { return new ProvidedCape(CapeProvider.CapeProviderType.valueOf(type)); }));

    protected CapeProvider provider;
    protected CapeProvider.CapeProviderType providerType;

    private ProvidedCape(CapeProvider.CapeProviderType provider) {
        this.providerType = provider;
        switch (provider) {
            case OPTIFINE -> this.provider = new OptifineProvider();
        }
    }

    @Override
    public Identifier getSerializationId() {
        return SERIALIZATION_ID;
    }

    @Override
    protected Object getTextureUniqueness() {
        return this;
    }

    @Override
    public ConfigCape saveToConfig() {
        try {
            var textureName = String.valueOf(Math.random() * 1000 + this.hashCode());
            var configCape = new ConfigCape(textureName);

            var bytes = this.provider.getClientCapeTexture();

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

            return new NativeImageBackedTexture(NativeImage.read(this.provider.getClientCapeTexture()));
        } catch (Exception e) {
            SkinShuffle.LOGGER.warn("Failed to load cape from provider: " + providerType.name(), e);
            return null;
        }
    }
}
