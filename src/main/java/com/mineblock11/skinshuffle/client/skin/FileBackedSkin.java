/*
 * ALL RIGHTS RESERVED
 *
 * Copyright (c) 2024 Calum H. (IMB11) and enjarai
 *
 * THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.util.LegacySkinConverter;
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
            var image = LegacySkinConverter.processTexture(NativeImage.read(NativeImage.Format.RGBA, inputStream));

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
