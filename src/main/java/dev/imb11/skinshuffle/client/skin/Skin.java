package dev.imb11.skinshuffle.client.skin;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.imb11.skinshuffle.MixinStatics;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.DefaultSkinHelper;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public interface Skin {
    Map<Identifier, MapCodec<? extends Skin>> TYPES = Map.of(
            UrlSkin.SERIALIZATION_ID, UrlSkin.CODEC,
            ResourceSkin.SERIALIZATION_ID, ResourceSkin.CODEC,
            ConfigSkin.SERIALIZATION_ID, ConfigSkin.CODEC,
            FileSkin.SERIALIZATION_ID, FileSkin.CODEC,
            UsernameSkin.SERIALIZATION_ID, UsernameSkin.CODEC,
            UUIDSkin.SERIALIZATION_ID, UUIDSkin.CODEC
    );
    Codec<Skin> CODEC = Identifier.CODEC.dispatch("type", Skin::getSerializationId, TYPES::get);

    static ResourceSkin randomDefaultSkin() {
        var uuid = UUID.randomUUID();
        var txt = DefaultSkinHelper.getSkinTextures(uuid);
        return new ResourceSkin(txt.texture(), txt.model().getName());
    }

    @Nullable Identifier getTexture();

    default SkinTextures getSkinTextures() {
        MinecraftClient client = MinecraftClient.getInstance();
        Supplier<SkinTextures> textureSupplier = () ->
                client.getSkinProvider().getSkinTextures(client.getGameProfile());

        SkinTextures clientTexture = MixinStatics.INITIAL_SKIN_TEXTURES.isDone()
                ? MixinStatics.INITIAL_SKIN_TEXTURES.join().orElseGet(textureSupplier)
                : textureSupplier.get();

        return new SkinTextures(this.getTexture(), null, clientTexture.capeTexture(), clientTexture.elytraTexture(), SkinTextures.Model.fromName(this.getModel()), false);
    }

    boolean isLoading();

    String getModel();

    void setModel(String value);

    Identifier getSerializationId();

    /**
     * Saves this skin to the config and returns a new reference to it.
     * THIS METHOD CAN AND WILL THROW, MAKE SURE TO CATCH IT!
     *
     * @return A new reference to this skin.
     * @throws RuntimeException If the skin could not be saved for whatever reason.
     */
    ConfigSkin saveToConfig();
}
