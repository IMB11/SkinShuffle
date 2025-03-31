package dev.imb11.skinshuffle.api.data;

import com.mojang.authlib.properties.Property;
import org.jetbrains.annotations.Nullable;

public record SkinQueryResult(boolean usesDefaultSkin, @Nullable String skinURL, @Nullable String modelType,
                              @Nullable String textureSignature, @Nullable String textureValue) {
    public static final SkinQueryResult EMPTY_RESULT = new SkinQueryResult(true, null, null, null, null);

    public Property toProperty() {
        return new Property("textures", textureValue, textureSignature);
    }
}
