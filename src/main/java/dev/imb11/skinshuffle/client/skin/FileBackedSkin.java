package dev.imb11.skinshuffle.client.skin;

import dev.imb11.skinshuffle.SkinShuffle;
import dev.imb11.skinshuffle.util.SkinTextureSafetyUtil;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class FileBackedSkin extends BackedSkin {
    @Nullable
    private Boolean exists;
    private long lastModifiedTime = -1;

    @Override
    protected Object getTextureUniqueness() {
        try {
            // Check if the file exists and get its last modified time
            if (Files.exists(getFile())) {
                lastModifiedTime = Files.getLastModifiedTime(getFile()).toMillis();
            }
            // Return a combination of file path and last modified time
            return getFile().toString() + "_" + lastModifiedTime;
        } catch (IOException e) {
            SkinShuffle.LOGGER.warn("Failed to get last modified time for file: " + getFile(), e);
            // Fall back to just the file path if we can't get the modified time
            return getFile();
        }
    }

    @Override
    protected @Nullable AbstractTexture loadTexture(Runnable completionCallback) {
        try (var inputStream = Files.newInputStream(getFile())) {
            var image = SkinTextureSafetyUtil.processTexture(NativeImage.read(NativeImage.Format.RGBA, inputStream));

            if (image == null) throw new RuntimeException("Texture is null!");

            //? if <1.21.5 {
            /*var texture = new NativeImageBackedTexture(image);
             *///?} else {
            var texture = new NativeImageBackedTexture(() -> String.valueOf(Math.abs(getTextureUniqueness().hashCode())), image);
            //?}

            completionCallback.run();
            return texture;
        } catch (Exception e) {
            SkinShuffle.LOGGER.warn("Failed to load skin from file: " + getFile(), e);
            return null;
        }
    }

    public void delete() {
        if (fileExists()) {
            try {
                Files.delete(getFile());
                exists = false;
                setTexture(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean fileExists() {
        return Files.isReadable(getFile());
    }

    protected abstract Path getFile();

    @Override
    public ConfigSkin saveToConfig() {
        try {
            var textureName = String.valueOf(Math.abs(getTextureUniqueness().hashCode()));
            var configSkin = new ConfigSkin(textureName, getModel());

            Files.copy(getFile(), configSkin.getFile(), StandardCopyOption.REPLACE_EXISTING);

            return configSkin;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save skin to config", e);
        }
    }
}
