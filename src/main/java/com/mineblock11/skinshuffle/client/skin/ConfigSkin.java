package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.SkinShuffleConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class ConfigSkin extends FileBackedSkin {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("config");
    public static final Codec<ConfigSkin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("skin_name").forGetter(skin -> skin.skinName),
            Codec.STRING.fieldOf("model").forGetter(ConfigSkin::getModel)
    ).apply(instance, ConfigSkin::new));

    private final String skinName;
    private final String model;

    public ConfigSkin(String skinName, String model) {
        this.skinName = skinName;
        this.model = model;
    }

    @Override
    public String getModel() {
        return model;
    }

    @Override
    public Identifier getSerializationId() {
        return SERIALIZATION_ID;
    }

    @Override
    public ConfigSkin saveToConfig() {
        return this;
    }

    protected Path getFile() {
        return SkinShuffleConfig.PERSISTENT_SKINS_DIR.resolve(skinName + ".png");
    }
}
