package com.mineblock11.skinshuffle.client.skin;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public record BasicSkin(Identifier texture, String model) implements Skin {
    @Override
    public @Nullable Identifier getTexture() {
        return texture;
    }

    @Override
    public String getModel() {
        return model;
    }
}
