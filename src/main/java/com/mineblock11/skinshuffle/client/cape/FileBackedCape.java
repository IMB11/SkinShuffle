package com.mineblock11.skinshuffle.client.cape;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.skin.ConfigSkin;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class FileBackedCape extends BackedCape {
    @Nullable
    private Boolean exists;

    @Override
    protected Object getTextureUniqueness() {
        return getFile();
    }

    @Override
    protected @Nullable AbstractTexture loadTexture(Runnable completionCallback) {
        try (var inputStream = Files.newInputStream(getFile())) {
            var image = NativeImage.read(inputStream);
            var texture = new NativeImageBackedTexture(image);

            completionCallback.run();
            return texture;
        } catch (Exception e) {
            SkinShuffle.LOGGER.warn("Failed to load cape from file: " + getFile(), e);
            return null;
        }
    }

    protected abstract Path getFile();

    @Override
    public ConfigCape saveToConfig() {
        try {
            var textureName = String.valueOf(Math.abs(getTextureUniqueness().hashCode()));
            var configCape = new ConfigCape(textureName);

            Files.copy(getFile(), configCape.getFile(), StandardCopyOption.REPLACE_EXISTING);

            return configCape;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save cape to config", e);
        }
    }
}
