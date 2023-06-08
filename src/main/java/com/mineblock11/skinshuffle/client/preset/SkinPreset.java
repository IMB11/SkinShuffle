package com.mineblock11.skinshuffle.client.preset;

import com.mineblock11.skinshuffle.client.skin.Skin;

public class SkinPreset {
    private String name;
    private Skin skin;

    // Temporary, for testing
    public SkinPreset(Skin skin) {
        this.skin = skin;
    }

    public String getName() {
        return name;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }
}
