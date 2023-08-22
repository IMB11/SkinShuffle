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
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public abstract class FileBackedSkin extends BackedSkin {
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
        if (exists == null) exists = Files.isReadable(getFile());

        return exists;
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
