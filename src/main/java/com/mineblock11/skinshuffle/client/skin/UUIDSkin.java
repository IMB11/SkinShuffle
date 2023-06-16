package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.api.MojangSkinAPI;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class UUIDSkin extends UrlSkin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("uuid");
    public static final Codec<UUIDSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("uuid").forGetter(skin -> skin.uuid.toString()),
            Codec.STRING.fieldOf("model").forGetter(UUIDSkin::getModel)
    ).apply(instance, (uuid, model) -> new UUIDSkin(UUID.fromString(uuid), model)));

    protected UUID uuid;

    public UUIDSkin(UUID uuid, String model) {
        super(model);
        this.uuid = uuid;
    }

    protected UUIDSkin(String model) {
        super(model);
    }

    @Override
    protected Object getTextureUniqueness() {
        return uuid;
    }

    @Override
    protected @Nullable AbstractTexture loadTexture(Runnable completionCallback) {
        var profile = MojangSkinAPI.getPlayerSkinTexture(uuid.toString());

        if (profile.skinURL() == null) {
            return null;
        }

        url = profile.skinURL();

        return super.loadTexture(completionCallback);
    }
}
