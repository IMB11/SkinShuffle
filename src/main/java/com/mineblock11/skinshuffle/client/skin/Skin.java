package com.mineblock11.skinshuffle.client.skin;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public interface Skin {
    @Nullable Identifier getTexture();

    String getModel();
}
