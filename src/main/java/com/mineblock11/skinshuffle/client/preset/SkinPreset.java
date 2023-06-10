package com.mineblock11.skinshuffle.client.preset;

import com.mineblock11.skinshuffle.client.skin.Skin;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SkinPreset {
    public static final Codec<SkinPreset> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                Skin.CODEC.fieldOf("skin").forGetter(SkinPreset::getSkin),
                Codec.STRING.fieldOf("name").forGetter(SkinPreset::getName)
        ).apply(instance, SkinPreset::new));

    private String name;
    private Skin skin;

    public SkinPreset(Skin skin) {
        this(skin, "Unnamed Preset");
    }

    public SkinPreset(Skin skin, String name) {
        this.skin = skin;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }
}
