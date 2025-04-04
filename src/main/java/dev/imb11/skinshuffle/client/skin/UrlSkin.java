package dev.imb11.skinshuffle.client.skin;

import dev.imb11.skinshuffle.SkinShuffle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import kong.unirest.Unirest;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

public class UrlSkin extends FileBackedSkin {
    public static final Int2ObjectMap<String> MODEL_CACHE = new Int2ObjectOpenHashMap<>();

    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("url");

    public static final MapCodec<UrlSkin> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("url").forGetter(skin -> skin.url),
            Codec.STRING.optionalFieldOf("model").forGetter(skin -> Optional.ofNullable(skin.model))
    ).apply(instance, UrlSkin::new));

    protected String url;
    protected String model;

    public UrlSkin(String url, String model) {
        this.url = url;
        this.model = model;
    }

    private UrlSkin(String url, Optional<String> model) {
        this.url = url;
        this.model = model.orElse(null);
    }

    protected UrlSkin(@Nullable String model) {
        this.model = model;
    }

    @Override
    public String getModel() {
        if (model == null) {
            tryLoadModelFromCache();
        }

        return model == null ? "classic" : model;
    }

    @Override
    public void setModel(String value) {
        this.model = value;
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
            if (model == null) {
                throw new RuntimeException("Model is not loaded yet");
            }

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
            Path cacheFolder = SkinShuffle.DATA_DIR.resolve("skins/downloaded");
            if (!cacheFolder.toFile().exists()) {
                Files.createDirectories(cacheFolder);
            }

            Path temporaryFilePath = getFile();
            // Download texture from URL into temporaryFilePath
            String url = getUrl();
            InputStream in = new URI(url).toURL().openStream();
            Files.copy(in, temporaryFilePath, StandardCopyOption.REPLACE_EXISTING);
            in.close();

            return super.loadTexture(completionCallback);
        } catch (Exception e) {
            SkinShuffle.LOGGER.warn("Failed to load skin from URL: {}", url, e);
            return null;
        }
    }

    @Override
    protected Path getFile() {
        return SkinShuffle.DATA_DIR.resolve("skins/downloaded").resolve(getTextureUniqueness().hashCode() + ".png");
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlSkin urlSkin = (UrlSkin) o;

        if (!Objects.equals(url, urlSkin.url)) return false;
        return Objects.equals(model, urlSkin.model);
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (model != null ? model.hashCode() : 0);
        return result;
    }

    protected void cacheModel() {
        MODEL_CACHE.put(getTextureUniqueness().hashCode(), getModel());
    }

    protected void tryLoadModelFromCache() {
        var model = MODEL_CACHE.get(getTextureUniqueness().hashCode());
        if (model != null) {
            setModel(model);
        }
    }
}
