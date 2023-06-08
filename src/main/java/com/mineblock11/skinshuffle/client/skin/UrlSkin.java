package com.mineblock11.skinshuffle.client.skin;

import com.mineblock11.skinshuffle.SkinShuffle;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.PlayerSkinTexture;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class UrlSkin implements Skin {
    private final String url;
    private final String model;
    private boolean fetching = false;
    private boolean fetched = false;
    @Nullable
    private Identifier textureId;

    public UrlSkin(String url, String model) {
        this.url = url;
        this.model = model;
    }

    public void fetchSkin() {
        fetching = true;
        var id = SkinShuffle.id("skin/url/" + url.hashCode());
        var textureManager = MinecraftClient.getInstance().getTextureManager();
        var texture = new PlayerSkinTexture(null, url, null, true, () -> {
            fetching = false;
            fetched = true;
            textureId = id;
        });
        textureManager.registerTexture(id, texture);
    }

    @Override
    public @Nullable Identifier getTexture() {
        if (textureId == null && !fetching && !fetched) {
            fetchSkin();
        }

        return textureId;
    }

    @Override
    public String getModel() {
        return model;
    }
}
