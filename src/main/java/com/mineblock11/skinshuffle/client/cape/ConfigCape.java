package com.mineblock11.skinshuffle.client.cape;

import com.mineblock11.skinshuffle.SkinShuffle;
import com.mineblock11.skinshuffle.client.config.SkinPresetManager;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Identifier;

import java.nio.file.Path;
import java.util.Objects;

public class ConfigCape extends FileBackedCape {
    public static final Identifier SERIALIZATION_ID = SkinShuffle.id("config");
    public static final Codec<ConfigCape> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("cape_name").forGetter(ConfigCape::getCapeName)
    ).apply(instance, ConfigCape::new));

    public String getCapeName() {
        return capeName;
    }

    private final String capeName;

    public ConfigCape(String capeName) {
        this.capeName = capeName;
    }

    @Override
    public Identifier getSerializationId() {
        return SERIALIZATION_ID;
    }

    @Override
    public ConfigCape saveToConfig() {
        return this;
    }

    public Path getFile() {
        return SkinPresetManager.PERSISTENT_SKINS_DIR.resolve(capeName + ".png");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConfigCape that = (ConfigCape) o;

        return Objects.equals(capeName, that.capeName);
    }

    @Override
    public int hashCode() {
        int result = capeName != null ? capeName.hashCode() : 0;
        result = 31 * result;
        return result;
    }
}
