package dev.imb11.skinshuffle.client.skin;

import dev.imb11.skinshuffle.SkinShuffle;
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
        //? if <1.21.4 {
        /*try (ResourceTexture.TextureData data = ResourceTexture.TextureData.load(resourceManager, getTexture())) {
            var nativeImage = data.getImage();
            nativeImage.writeTo(configSkin.getFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ResourceSkin to config.", e);
        }
        *///?} else {
        try (var resource = new ResourceTexture(getTexture())) {
            var resourceTex = resource.loadContents(resourceManager);
            var image = resourceTex.image();
            image.writeTo(configSkin.getFile());
        } catch (IOException e) {
            throw new RuntimeException("Failed to save ResourceSkin to config.", e);
        }
        //?}

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
