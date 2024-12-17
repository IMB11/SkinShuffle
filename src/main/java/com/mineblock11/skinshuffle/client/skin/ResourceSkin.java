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
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.ResourceTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public final class ResourceSkin implements Skin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("resource");

    public static final MapCodec<ResourceSkin> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("texture").forGetter(ResourceSkin::getTexture),
            Codec.STRING.fieldOf("model").forGetter(ResourceSkin::getModel)
    ).apply(instance, ResourceSkin::new));
    private final Identifier texture;
    private String model;

    public ResourceSkin(Identifier texture, String model) {
        this.texture = texture;
        this.model = model;
    }

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
    public void setModel(String value) {
        this.model = value;
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

    public Identifier texture() {
        return texture;
    }

    public String model() {
        return model;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ResourceSkin) obj;
        return Objects.equals(this.texture, that.texture) &&
                Objects.equals(this.model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(texture, model);
    }

    @Override
    public String toString() {
        return "ResourceSkin[" +
                "texture=" + texture + ", " +
                "model=" + model + ']';
    }

}
